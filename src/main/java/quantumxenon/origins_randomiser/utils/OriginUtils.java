package quantumxenon.origins_randomiser.utils;

import io.github.apace100.origins.origin.Origin;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.lang3.StringUtils;
import quantumxenon.origins_randomiser.enums.Reason;

public interface OriginUtils {
    OriginLayer layer = OriginsAPI.getLayersRegistry().get(new ResourceLocation("origins","origin"));

    static void randomOrigin(Reason reason, ServerPlayer player) {
        Origin newOrigin = getOrigin(player);
        setOrigin(player, newOrigin);
        PlayerList playerList = player.getServer().getPlayerList();
        for (ServerPlayer entity : playerList.getPlayers()) {
            entity.sendSystemMessage(Component.literal(PlayerUtils.getName(player) + " " + getReason(reason) + " " + format(newOrigin)));
        }
    }

    private static Origin getOrigin(ServerPlayer player) {

    }

    private static void setOrigin(ServerPlayer player, Origin origin) {
        
    }

    private static StringBuilder format(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
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