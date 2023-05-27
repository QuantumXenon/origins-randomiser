package quantumxenon.randomiser.utils;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface PlayerUtils {
    static String getName(ServerPlayerEntity player) {
        return player.getName().getString();
    }

    static void send(String message, ServerCommandSource commandSource) {
        commandSource.sendMessage(Text.of(message));
    }

    static void dropItems(ServerPlayerEntity player) {
        PowerHolderComponent.getPowers(player, InventoryPower.class).forEach(inventory -> {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.getStack(i);
                player.dropItem(itemStack, true, false);
                inventory.setStack(i, ItemStack.EMPTY);
            }
        });
    }
}