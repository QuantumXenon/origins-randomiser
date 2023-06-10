package quantumxenon.origins_randomiser.utils;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Reason;

import java.util.List;
import java.util.Random;

public interface OriginUtils {
    Holder<OriginLayer> layer = OriginsAPI.getActiveLayers().get(0);

    static void randomOrigin(Reason reason, ServerPlayer player) {
        Holder<Origin> newOrigin = getRandomOrigin(player);
        setOrigin(player, newOrigin);
        for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(Component.literal(player.getScoreboardName() + " ").append(Component.translatable(getReason(reason)).append(Component.literal(" " + getName(newOrigin)))));
        }
    }

    private static Holder<Origin> getRandomOrigin(ServerPlayer player) {
        List<Holder<Origin>> origins = layer.value().randomOrigins(player);
        return origins.get(new Random().nextInt(origins.size()));
    }

    private static void setOrigin(ServerPlayer player, Holder<Origin> origin) {
        IOriginContainer.get(player).ifPresent(container -> {
            container.setOrigin(layer, origin);
            container.synchronize();
        });
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
            default -> {
                return "origins-randomiser.reason.command";
            }
        }
    }
}