package quantumxenon.origins_randomiser.utils;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.InventoryPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Reason;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface OriginUtils {
    Holder<OriginLayer> layer = OriginsAPI.getActiveLayers().get(0); // layer = origins:origin

    static void randomOrigin(Reason reason, ServerPlayer player) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.dropExtraInventory()) {
                dropItems(player);
            }
            Holder<Origin> newOrigin = getRandomOrigin(player);
            setOrigin(player, newOrigin);
            if (ConfigUtils.randomiserMessages()) {
                for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
                    serverPlayer.sendSystemMessage(Component.literal(player.getScoreboardName() + " ").append(Component.translatable(getReason(reason)).append(Component.literal(" " + getName(newOrigin)))));
                }
            }
        } else {
            player.sendSystemMessage(MessageUtils.getMessage(Message.DISABLED));
        }
    }

    private static Holder<Origin> getRandomOrigin(ServerPlayer player) {
        List<Holder<Origin>> origins = layer.value().randomOrigins(player);
        Holder<Origin> newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!ConfigUtils.allowDuplicateOrigins()) {
            while (Optional.of(newOrigin).equals(getOrigin(player))) {
                newOrigin = origins.get(new Random().nextInt(origins.size()));
            }
        }
        return newOrigin;
    }

    private static void setOrigin(ServerPlayer player, Holder<Origin> origin) {
        IOriginContainer.get(player).ifPresent(container -> {
            container.setOrigin(layer, origin);
            container.synchronize();
        });
    }


    static void dropItems(ServerPlayer player) {
        IPowerContainer.get(player).ifPresent(container -> {
            List<Holder<ConfiguredPower<InventoryConfiguration, InventoryPower>>> powers = container.getPowers(ApoliPowers.INVENTORY.get());
            for(Holder<ConfiguredPower<InventoryConfiguration, InventoryPower>> power : powers){
                Container inventory = power.value().getFactory().getInventory(power.value(), player);
                for (int i = 0; i < inventory.getContainerSize(); ++i) {
                    ItemStack itemStack = inventory.getItem(i);
                    player.drop(itemStack, true, false);
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        });
    }

    private static Optional<Holder<Origin>> getOrigin(ServerPlayer player) {
        return IOriginContainer.get(player).map(container -> OriginsAPI.getOriginsRegistry().getHolder(container.getOrigin(layer)).get());
    }

    private static String getName(Holder<Origin> origin) {
        return origin.value().getName().getString();
    }

    private static String getReason(Reason reason) {
        switch (reason) {
            case DEATH -> {
                return "origins-randomiser.reason.death";
            }
            case FIRST_JOIN -> {
                return "origins-randomiser.reason.firstJoin";
            }
            case SLEEP -> {
                return "origins-randomiser.reason.sleep";
            }
            default -> {
                return "origins-randomiser.reason.command";
            }
        }
    }
}