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
import org.apache.commons.lang3.StringUtils;
import quantumxenon.randomiser.enums.Reason;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public interface OriginUtils {
    OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));

    static void randomOrigin(Reason reason, ServerPlayerEntity player) {
        if (ConfigUtils.dropExtraInventory()) {
            dropItems(player);
        }
        Origin newOrigin = getOrigin(player);
        setOrigin(player, newOrigin);
        if (ConfigUtils.randomiserMessages()) {
            List<ServerPlayerEntity> playerList = Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList();
            for (ServerPlayerEntity serverPlayer : playerList) {
                serverPlayer.sendMessage(getReason(reason, player.getName().getString(), format(newOrigin).toString()));
            }
        }
    }

    private static Origin getOrigin(ServerPlayerEntity player) {
        List<Origin> origins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
        Origin newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!ConfigUtils.allowDuplicateOrigins()) {
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

    private static StringBuilder format(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    static void dropItems(ServerPlayerEntity player) {
        PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(inventory -> {
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack itemStack = inventory.getStack(slot);
                player.dropItem(itemStack, true, false);
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        });
    }

    private static Text getReason(Reason reason, String player, String origin) {
        switch (reason) {
            case DEATH -> {
                return Text.translatable("origins-randomiser.reason.death", player, origin);
            }
            case FIRST_JOIN -> {
                return Text.translatable("origins-randomiser.reason.firstJoin", player, origin);
            }
            case SLEEP -> {
                return Text.translatable("origins-randomiser.reason.sleep", player, origin);
            }
            default -> {
                return Text.translatable("origins-randomiser.reason.command", player, origin);
            }
        }
    }
}