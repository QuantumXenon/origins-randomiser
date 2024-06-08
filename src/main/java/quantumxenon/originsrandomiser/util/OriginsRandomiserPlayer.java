package quantumxenon.originsrandomiser.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.networking.packet.s2c.OpenChooseOriginScreenS2CPacket;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Message;
import quantumxenon.originsrandomiser.enums.Reason;

import java.util.*;

import static net.minecraft.scoreboard.ScoreboardCriterion.DUMMY;
import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public class OriginsRandomiserPlayer {
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final OriginLayer baseLayer = OriginLayers.getLayer(new Identifier("origins:origin")); // layer = origins:origin
    private final ServerPlayerEntity player;

    public OriginsRandomiserPlayer(ServerPlayerEntity serverPlayerEntity) {
        this.player = serverPlayerEntity;
    }

    public String getName() {
        return player.getNameForScoreboard();
    }

    public void addScoreboardTag(String tag) {
        player.addCommandTag(tag);
    }

    public boolean hasScoreboardTag(String tag) {
        return player.getCommandTags().contains(tag);
    }

    public void removeScoreboardTag(String tag) {
        player.getCommandTags().remove(tag);
    }

    private ScoreAccess getObjective(String objective) {
        return player.getScoreboard().getOrCreateScore(player, player.getScoreboard().getNullableObjective(objective));
    }

    public void createObjective(String objective, int number) {
        if (!player.getScoreboard().getObjectiveNames().contains(objective)) {
            player.getScoreboard().addObjective(objective, DUMMY, Text.of(objective), INTEGER, true, null);
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
        this.getObjective(objective).incrementScore(value);
    }

    public void getAndSendMessage(Message message) {
        player.sendMessage(OriginsRandomiserMessages.getMessage(message));
    }

    public void getAndSendMessage(Message message, int value) {
        player.sendMessage(OriginsRandomiserMessages.getMessage(message, value));
    }

    public void setGameMode(GameMode gameMode) {
        player.changeGameMode(gameMode);
    }

    public boolean hasSleptThroughNight() {
        return player.canResetTimeBySleeping();
    }

    public void randomiseOrigin(Reason reason) {
        if (config.general.randomiseOrigins && this.isNotHuman()) {
            this.dropItems();
            this.getRandomLayers().stream().filter(OriginLayer::isEnabled).filter(OriginLayer::isRandomAllowed).forEach(layer -> {
                Origin newOrigin = this.getRandomOrigin(layer);
                this.updateOrigin(layer, newOrigin);
                if (layer.equals(baseLayer) && config.general.randomiserMessages) {
                    List<ServerPlayerEntity> playerList = player.getServer().getPlayerManager().getPlayerList();
                    for (ServerPlayerEntity serverPlayer : playerList) {
                        serverPlayer.sendMessage(OriginsRandomiserMessages.getMessage(reason, this.getName(), formatOriginName(newOrigin)));
                    }
                }
            });
        }
    }

    public boolean isNotHuman() {
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(baseLayer);
        Origin humanOrigin = OriginRegistry.get(new Identifier("origins:human")); // origin = origins:human
        return !Objects.equals(currentOrigin, humanOrigin);
    }

    public void openOriginsScreen() {
        ServerPlayNetworking.send(player, new OpenChooseOriginScreenS2CPacket(false));
    }

    public void dropItems() { // TODO: Check if still needed
        PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(InventoryPower::dropItemsOnLost);
    }

    /* Modified from io/github/apace100/origins/content/OrbOfOriginItem */
    public void clearOrigins() {
        this.getRandomLayers().stream().filter(OriginLayer::isEnabled).filter(OriginLayer::isRandomAllowed).forEach(layer -> ModComponents.ORIGIN.get(player).setOrigin(layer, Origin.EMPTY));
        OriginComponent.sync(player);
    }

    private Collection<OriginLayer> getRandomLayers() {
        if (config.general.randomiseAllLayers) {
            return OriginLayers.getLayers();
        } else {
            return Collections.singletonList(baseLayer);
        }
    }

    private void updateOrigin(OriginLayer layer, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    private String formatOriginName(Origin origin) {
        return Text.translatable(origin.getOrCreateNameTranslationKey()).getString();
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    private Origin getRandomOrigin(OriginLayer layer) {
        List<Origin> randomOrigins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
        Origin newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
        if (!config.advanced.allowDuplicateOrigins) {
            Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
            while (newOrigin.equals(currentOrigin)) {
                newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
            }
        }
        return newOrigin;
    }
}