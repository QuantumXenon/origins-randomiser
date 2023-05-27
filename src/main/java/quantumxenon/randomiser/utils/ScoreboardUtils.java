package quantumxenon.randomiser.utils;

import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static ScoreboardPlayerScore getObjective(ServerPlayerEntity player, String objective) {
        return player.getScoreboard().getPlayerScore(PlayerUtils.getName(player), player.getScoreboard().getObjective(objective));
    }

    static int getValue(String objective, ServerPlayerEntity player) {
        return getObjective(player, objective).getScore();
    }

    static void setValue(String objective, int value, ServerPlayerEntity player) {
        getObjective(player, objective).setScore(value);
    }

    static void decrementValue(String objective, ServerPlayerEntity player) {
        getObjective(player, objective).incrementScore(-1);
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