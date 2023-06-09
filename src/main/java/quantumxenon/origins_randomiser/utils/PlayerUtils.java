package quantumxenon.origins_randomiser.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Tag;

public interface PlayerUtils {
    static String getName(ServerPlayer player) {
        return player.getName().getString();
    }

    static String translate(String key) {
        return Component.translatable(key).getString();
    }

    static boolean noScoreboardTag(Tag tag, ServerPlayer player) {
        return !player.getTags().contains(tagName(tag));
    }

    static String tagName(Tag tag) {
        if (tag == Tag.FIRST_JOIN) {
            return "firstJoin";
        }
        return null;
    }
}