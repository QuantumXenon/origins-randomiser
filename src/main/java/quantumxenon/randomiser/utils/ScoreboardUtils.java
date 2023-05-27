package quantumxenon.randomiser.utils;

import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public interface ScoreboardUtils {
    static void setValue(String objective, int value, ServerPlayerEntity player) {
        player.getScoreboard().getPlayerScore(PlayerUtils.getName(player), player.getScoreboard().getObjective(objective)).setScore(value);
    }

    static int getValue(String objective, ServerPlayerEntity player) {
        return player.getScoreboard().getPlayerScore(PlayerUtils.getName(player), player.getScoreboard().getObjective(objective)).getScore();
    }

    static void decrementValue(String objective, ServerPlayerEntity player) {
        player.getScoreboard().getPlayerScore(PlayerUtils.getName(player), player.getScoreboard().getObjective(objective)).incrementScore(-1);
    }

    static void createObjective(String name, int number, ServerPlayerEntity player) {
        if (!player.getScoreboard().containsObjective(name)) {
            player.getScoreboard().addObjective(name, ScoreboardCriterion.DUMMY, Text.of(name), INTEGER);
            setValue(name, number, player);
        }
    }

    static boolean noScoreboardTag(String tag, ServerPlayerEntity player) {
        return !player.getCommandTags().contains(tag);
    }
}