package quantumxenon.origins_randomiser.util;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.OriginsCommon;
import io.github.edwinmindcraft.origins.common.network.S2COpenOriginScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Score;
import net.minecraftforge.network.PacketDistributor;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Reason;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY;
import static net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER;

public class OriginsRandomiserPlayer {
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final OriginLayer baseLayer = OriginsAPI.getLayersRegistry().get(new ResourceLocation("origins:origin")); // layer = origins:origin
    private final ServerPlayer player;

    public OriginsRandomiserPlayer(ServerPlayer serverPlayer) {
        this.player = serverPlayer;
    }

    public String getName() {
        return player.getScoreboardName();
    }

    public void addScoreboardTag(String tag) {
        player.addTag(tag);
    }

    public boolean hasScoreboardTag(String tag) {
        return player.getTags().contains(tag);
    }

    private Score getObjective(String objective) {
        return player.getScoreboard().getOrCreatePlayerScore(objective, player.getScoreboard().getOrCreateObjective(objective));
    }

    public void createObjective(String objective, int number) {
        if (!player.getScoreboard().getObjectiveNames().contains(objective)) {
            player.getScoreboard().addObjective(objective, DUMMY, Component.literal(objective), INTEGER);
            this.setObjectiveValue(objective, number);
        }
    }

    public int getObjectiveValue(String objective) {
        return this.getObjective(objective).getScore();
    }

    public void setObjectiveValue(String objective, int value) {
        this.getObjective(objective).setScore(value);
    }

    public void changeObjectiveValue(String objective, int value) {
        this.getObjective(objective).add(value);
    }

    public void getAndSendMessage(Message message) {
        player.sendSystemMessage(OriginsRandomiserMessages.getMessage(message));
    }

    public void getAndSendMessage(Message message, int value) {
        player.sendSystemMessage(OriginsRandomiserMessages.getMessage(message, value));
    }

    public void setGameMode(GameType gameMode) {
        player.setGameMode(gameMode);
    }

    public boolean hasSleptThroughNight() {
        return player.isSleepingLongEnough();
    }

    public void randomiseOrigin(Reason reason) {
        if (config.general.randomiseOrigins && this.isNotHuman()) {
            this.getRandomLayers().forEach(layer -> {
                Holder<Origin> newOrigin = this.getRandomOrigin(layer);
                this.setOrigin(layer, newOrigin);
                if (layer.equals(baseLayer) && config.general.randomiserMessages) {
                    List<ServerPlayer> playerList = player.getServer().getPlayerList().getPlayers();
                    for (ServerPlayer serverPlayer : playerList) {
                        serverPlayer.sendSystemMessage(OriginsRandomiserMessages.getMessage(reason, this.getName(), formatOriginName(newOrigin.value())));
                    }
                }
            });
        }
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    private void setOrigin(OriginLayer layer, Holder<Origin> origin) {
        Holder<OriginLayer> layerHolder = OriginsAPI.getLayersRegistry().wrapAsHolder(layer);
        IOriginContainer.get(player).ifPresent(container -> {
            container.setOrigin(layerHolder, origin);
            container.synchronize();
        });
    }

    public boolean isNotHuman() {
        Origin currentOrigin = this.getCurrentOrigin(baseLayer);
        Origin humanOrigin = OriginsAPI.getOriginsRegistry().get(new ResourceLocation("origins:human")); // origin = origins:human
        return !Objects.equals(currentOrigin, humanOrigin);
    }

    public void clearOrigins() {
        Holder<Origin> emptyOrigin = OriginsAPI.getOriginsRegistry().wrapAsHolder(Origin.EMPTY);
        this.getRandomLayers().forEach(layer -> this.setOrigin(layer, emptyOrigin));
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    public void openOriginsScreen() {
        OriginsCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2COpenOriginScreen(false));
    }

    private Stream<OriginLayer> getRandomLayers() {
        if (config.general.randomiseAllLayers) {
            return OriginsAPI.getLayersRegistry().stream().filter(OriginLayer::enabled).filter(OriginLayer::allowRandom);
        } else {
            return Stream.of(baseLayer);
        }
    }

    private String formatOriginName(Origin origin) {
        return origin.getName().getString();
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    private Holder<Origin> getRandomOrigin(OriginLayer layer) {
        List<Holder<Origin>> randomOrigins = layer.randomOrigins(player);
        Holder<Origin> newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
        if (!config.advanced.allowDuplicateOrigins) {
            while (newOrigin.equals(getCurrentOrigin(layer))) {
                newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
            }
        }
        return newOrigin;
    }

    private Origin getCurrentOrigin(OriginLayer layer) {
        return IOriginContainer.get(player).map(container -> OriginsAPI.getOriginsRegistry().get(container.getOrigin(layer))).orElse(Origin.EMPTY);
    }
}