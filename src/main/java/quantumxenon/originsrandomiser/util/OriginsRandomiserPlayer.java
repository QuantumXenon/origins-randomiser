package quantumxenon.originsrandomiser.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Message;
import quantumxenon.originsrandomiser.enums.Reason;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static net.minecraft.scoreboard.ScoreboardCriterion.DUMMY;
import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public class OriginsRandomiserPlayer {
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final OriginLayer baseLayer = OriginLayers.getLayer(new Identifier("origins:origin")); // layer = origins:origin
    private final Origin humanOrigin = OriginRegistry.get(new Identifier("origins:human")); // origin = origins:human
    private final ServerPlayerEntity player;

    public OriginsRandomiserPlayer(ServerPlayerEntity serverPlayerEntity) {
        this.player = serverPlayerEntity;
    }

    public String getName() {
        return player.getEntityName();
    }

    public void addScoreboardTag(String tag) {
        player.addScoreboardTag(tag);
    }

    public boolean hasScoreboardTag(String tag) {
        return player.getScoreboardTags().contains(tag);
    }

    public void removeScoreboardTag(String tag) {
        player.getScoreboardTags().remove(tag);
    }

    private ScoreboardPlayerScore getObjective(String objective) {
        return player.getScoreboard().getPlayerScore(player.getEntityName(), player.getScoreboard().getNullableObjective(objective));
    }

    public void createObjective(String objective, int number) {
        if (!player.getScoreboard().getObjectiveNames().contains(objective)) {
            player.getScoreboard().addObjective(objective, DUMMY, Text.of(objective), INTEGER);
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
        player.sendMessage(OriginsRandomiserMessages.getMessage(message), false);
    }

    public void getAndSendMessage(Message message, int value) {
        player.sendMessage(OriginsRandomiserMessages.getMessage(message, value), false);
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
            this.getRandomLayers().forEach(layer -> {
                Origin newOrigin = this.getRandomOrigin(layer);
                ModComponents.ORIGIN.get(player).setOrigin(layer, newOrigin);
                OriginComponent.sync(player);
                if (layer.equals(baseLayer) && config.general.randomiserMessages) {
                    List<ServerPlayerEntity> playerList = player.getServer().getPlayerManager().getPlayerList();
                    for (ServerPlayerEntity serverPlayer : playerList) {
                        serverPlayer.sendMessage(OriginsRandomiserMessages.getMessage(reason, this.getName(), formatOriginName(newOrigin)), false);
                    }
                }
            });
        }
    }

    public boolean isNotHuman() {
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(baseLayer);
        return !(currentOrigin == humanOrigin);
    }

    public void clearOrigins() {
        this.dropItems();
        this.getRandomLayers().forEach(layer -> ModComponents.ORIGIN.get(player).setOrigin(layer, Origin.EMPTY));
        OriginComponent.sync(player);
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    public void openOriginsScreen() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, ModPackets.OPEN_ORIGIN_SCREEN, buffer);
    }

    /* Modified from io/github/apace100/apoli/power/InventoryPower */
    private void dropItems() {
        PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(inventory -> {
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack itemStack = inventory.getStack(slot);
                player.dropItem(itemStack, true, false);
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        });
    }

    private Stream<OriginLayer> getRandomLayers() {
        if (config.general.randomiseAllLayers) {
            return OriginLayers.getLayers().stream().filter(OriginLayer::isEnabled).filter(OriginLayer::isRandomAllowed);
        } else {
            return Stream.of(baseLayer);
        }
    }

    private String formatOriginName(Origin origin) {
        return new TranslatableText(origin.getOrCreateNameTranslationKey()).getString();
    }

    /* Modified from io/github/apace100/origins/command/OriginCommand */
    private Origin getRandomOrigin(OriginLayer layer) {
        if (config.advanced.resetToHumanOrigin) {
            if (layer == baseLayer) {
                return humanOrigin;
            } else {
                return Origin.EMPTY;
            }
        } else {
            List<Origin> randomOrigins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
            Origin newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
            if (!config.advanced.allowDuplicateOrigins) {
                Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
                while (newOrigin == currentOrigin) {
                    newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
                }
            }
            return newOrigin;
        }
    }
}