package quantumxenon.origins_randomiser.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Score;

import static net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY;
import static net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static Score getObjective(ServerPlayer player, String objective) {
        return player.getScoreboard().getOrCreatePlayerScore(objective, player.getScoreboard().getObjective(objective));
    }

    static void createObjective(String objective, int number, ServerPlayer player) {
        player.getScoreboard().addObjective(objective, DUMMY, Component.literal(objective), INTEGER);
        setValue(objective, number, player);
    }

    static int getValue(String objective, ServerPlayer player) {
        return getObjective(player, objective).getScore();
    }

    static void setValue(String objective, int value, ServerPlayer player) {
        getObjective(player, objective).setScore(value);
    }

    static void decrementValue(String objective, ServerPlayer player) {
        getObjective(player, objective).add(-1);
    }

    static boolean noScoreboardTag(String tag, ServerPlayer player) {
        return !player.getTags().contains(tag);
    }
}