package quantumxenon.origins_randomiser.utils;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Reason;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface OriginUtils {
    Holder<OriginLayer> layer = OriginsAPI.getActiveLayers().get(0); // layer = origins:origin

    static void randomOrigin(Reason reason, ServerPlayer player) {
        Holder<Origin> newOrigin = getRandomOrigin(player);
        setOrigin(player, newOrigin);
        if (ConfigUtils.randomiserMessages()) {
            for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
                serverPlayer.sendSystemMessage(getReason(reason, player.getScoreboardName(),getName(newOrigin)));
            }
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

    private static Optional<Holder<Origin>> getOrigin(ServerPlayer player) {
        return IOriginContainer.get(player).map(container -> OriginsAPI.getOriginsRegistry().getHolder(container.getOrigin(layer)).get());
    }

    private static String getName(Holder<Origin> origin) {
        return origin.value().getName().getString();
    }

    private static MutableComponent getReason(Reason reason, String player, String origin) {
        switch (reason) {
            case DEATH -> {
                return Component.translatable("origins-randomiser.reason.death", player, origin);
            }
            case FIRST_JOIN -> {
                return Component.translatable("origins-randomiser.reason.firstJoin", player, origin);
            }
            case SLEEP -> {
                return Component.translatable("origins-randomiser.reason.sleep", player, origin);
            }
            default -> {
                return Component.translatable("origins-randomiser.reason.command", player, origin);
            }
        }
    }
}