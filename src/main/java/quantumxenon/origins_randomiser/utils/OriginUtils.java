package quantumxenon.origins_randomiser.utils;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.lang3.StringUtils;
import quantumxenon.origins_randomiser.enums.Reason;

import java.util.List;
import java.util.Random;

public interface OriginUtils {
    Holder<OriginLayer> layer = OriginsAPI.getActiveLayers().get(0);

    static void randomOrigin(Reason reason, ServerPlayer player) {
        Holder<Origin> newOrigin = getRandomOrigin(player);
        setOrigin(player, newOrigin);
        PlayerList playerList = player.getServer().getPlayerList();
        for (ServerPlayer entity : playerList.getPlayers()) {
            entity.sendSystemMessage(Component.literal(PlayerUtils.getName(player) + " " + getReason(reason) + " " + format(newOrigin)));
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
                }
        );
    }

    private static StringBuilder format(Holder<Origin> origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.get().getName().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    private static String getReason(Reason reason) {
        switch (reason) {
            case DEATH -> {
                return PlayerUtils.translate("origins-randomiser.reason.death");
            }
            case FIRST_JOIN -> {
                return PlayerUtils.translate("origins-randomiser.reason.firstJoin");
            }
            default -> {
                return PlayerUtils.translate("origins-randomiser.reason.command");
            }
        }
    }
}