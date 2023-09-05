package quantumxenon.randomiser.utils;

import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.scoreboard.ScoreboardCriterion.DUMMY;
import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static ScoreboardPlayerScore getObjective(ServerPlayerEntity player, String objective) {
        return player.getScoreboard().getPlayerScore(player.getEntityName(), player.getScoreboard().getNullableObjective(objective));
    }

    static void createObjective(String objective, int number, ServerPlayerEntity player) {
        if (!player.getScoreboard().playerHasObjective(player.getEntityName(), player.getScoreboard().getNullableObjective(objective))) {
            player.getScoreboard().addObjective(objective, DUMMY, Text.of(objective), INTEGER);
            setValue(objective, number, player);
        }
    }

    static int getValue(String objective, ServerPlayerEntity player) {
        return getObjective(player, objective).getScore();
    }

    static void setValue(String objective, int value, ServerPlayerEntity player) {
        getObjective(player, objective).setScore(value);
    }

    static void changeValue(String objective, int value, ServerPlayerEntity player) {
        getObjective(player, objective).incrementScore(value);
    }

    static boolean noScoreboardTag(String tag, ServerPlayerEntity player) {
        return !player.getCommandTags().contains(tag);
    }
}