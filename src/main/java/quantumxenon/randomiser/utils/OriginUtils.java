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
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Reason;

import java.util.List;
import java.util.Random;

public interface OriginUtils {
    OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin")); // layer = origins:origin
    OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    static void randomOrigin(Reason reason, ServerPlayerEntity player) {
        if (config.general.dropExtraInventory) {
            dropItems(player);
        }
        Origin newOrigin = getRandomOrigin(player);
        setOrigin(player, newOrigin);
        String originName = getFormattedName(newOrigin);
        if (config.general.randomiserMessages) {
            List<ServerPlayerEntity> playerList = player.getServer().getPlayerManager().getPlayerList();
            for (ServerPlayerEntity serverPlayer : playerList) {
                serverPlayer.sendMessage(MessageUtils.getMessage(reason, player.getEntityName(), originName));
            }
        }
    }

    private static Origin getRandomOrigin(ServerPlayerEntity player) {
        List<Origin> origins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
        Origin newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!config.general.allowDuplicateOrigins) {
            while (newOrigin.equals(currentOrigin)) {
                newOrigin = origins.get(new Random().nextInt(origins.size()));
            }
        }
        return newOrigin;
    }

    private static void setOrigin(ServerPlayerEntity player, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    private static String getFormattedName(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName.toString();
    }

    private static void dropItems(ServerPlayerEntity player) {
        PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(inventory -> {
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack itemStack = inventory.getStack(slot);
                player.dropItem(itemStack, true, false);
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        });
    }
}