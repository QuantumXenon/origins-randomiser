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
        commandSource.sendFeedback(Text.of(message),false);
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
}