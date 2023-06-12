package quantumxenon.origins_randomiser.utils;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
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
            List<ServerPlayer> playerList = player.getServer().getPlayerList().getPlayers();
            for (ServerPlayer serverPlayer : playerList) {
                serverPlayer.sendSystemMessage(MessageUtils.getMessage(reason, player.getScoreboardName(), format(newOrigin)));
            }
        }
    }

    private static Holder<Origin> getRandomOrigin(ServerPlayer player) {
        List<Holder<Origin>> origins = layer.value().randomOrigins(player);
        Optional<Holder<Origin>> currentOrigin = IOriginContainer.get(player).map(container -> OriginsAPI.getOriginsRegistry().getHolder(container.getOrigin(layer)).get());
        Holder<Origin> newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!ConfigUtils.allowDuplicateOrigins()) {
            while (Optional.of(newOrigin).equals(currentOrigin)) {
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

    private static String format(Holder<Origin> origin) {
        return origin.value().getName().getString();
    }
}