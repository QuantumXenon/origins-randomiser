package quantumxenon.randomiser.utils;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Reason;

import java.util.*;

public interface OriginUtils {
    OriginLayer baseLayer = OriginLayers.getLayer(new Identifier("origins", "origin")); // layer = origins:origin
    OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    Collection<OriginLayer> randomLayers = getRandomLayers();

    static void randomOrigin(Reason reason, ServerPlayerEntity player) {
        if (isNotHuman(player)) {
            dropItems(player);

            randomLayers.stream().filter(OriginLayer::isEnabled).filter(OriginLayer::isRandomAllowed).forEach(layer -> {
                Origin newOrigin = getRandomOrigin(player, layer);
                setOrigin(player, layer, newOrigin);
                if (layer.equals(baseLayer) && config.general.randomiserMessages) {
                    List<ServerPlayerEntity> playerList = player.getServer().getPlayerManager().getPlayerList();
                    for (ServerPlayerEntity serverPlayer : playerList) {
                        serverPlayer.sendMessage(MessageUtils.getMessage(reason, player.getNameForScoreboard(), getFormattedName(newOrigin)));
                    }
                }
            });
        }
    }

    static boolean isNotHuman(ServerPlayerEntity player) {
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(baseLayer);
        Origin humanOrigin = OriginRegistry.get(new Identifier("origins", "human")); // origin = origins:human
        return !Objects.equals(currentOrigin, humanOrigin);
    }

    private static Collection<OriginLayer> getRandomLayers() {
        if (config.general.randomiseAllLayers) {
            return OriginLayers.getLayers();
        } else {
            return Collections.singletonList(baseLayer);
        }
    }

    private static Origin getRandomOrigin(ServerPlayerEntity player, OriginLayer layer) {
        List<Origin> randomOrigins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
        Origin newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
        if (!config.general.allowDuplicateOrigins) {
            Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
            while (newOrigin.equals(currentOrigin)) {
                newOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
            }
        }
        return newOrigin;
    }

    private static void setOrigin(ServerPlayerEntity player, OriginLayer layer, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    static void clearOrigins(ServerPlayerEntity player) {
        randomLayers.stream().filter(OriginLayer::isEnabled).filter(OriginLayer::isRandomAllowed).forEach(layer -> ModComponents.ORIGIN.get(player).setOrigin(layer, Origin.EMPTY));
        OriginComponent.sync(player);
    }

    private static String getFormattedName(Origin origin) {
        return Text.translatable(origin.getOrCreateNameTranslationKey()).getString();
    }

    static void dropItems(ServerPlayerEntity player) {
        if (config.general.dropExtraInventory) {
            PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(inventory -> {
                for (int slot = 0; slot < inventory.size(); slot++) {
                    ItemStack itemStack = inventory.getStack(slot);
                    player.dropItem(itemStack, true, false);
                    inventory.setStack(slot, ItemStack.EMPTY);
                }
            });
        }
    }
}