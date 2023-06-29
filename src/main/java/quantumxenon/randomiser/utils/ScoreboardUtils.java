package quantumxenon.randomiser.utils;

import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.scoreboard.ScoreboardCriterion.DUMMY;
import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static ScoreboardPlayerScore getObjective(ServerPlayerEntity player, String objective) {
        return player.getScoreboard().getPlayerScore(player.getEntityName(), player.getScoreboard().getObjective(objective));
    }

    static void createObjective(String objective, int number, ServerPlayerEntity player) {
        if (!player.getScoreboard().containsObjective(objective)) {
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

    static void decrementValue(String objective, ServerPlayerEntity player) {
        getObjective(player, objective).incrementScore(-1);
    }

    static boolean noScoreboardTag(String tag, ServerPlayerEntity player) {
        return !player.getCommandTags().contains(tag);
    }

    static String tagName(Tag tag) {
        switch (tag) {
            case FIRST_JOIN -> {
                return "firstJoin";
            }
            case LIMIT_USES_MESSAGE -> {
                return "limitUsesMessage";
            }
            case LIVES_ENABLED_MESSAGE -> {
                return "livesEnabledMessage";
            }
            case LIVES_MESSAGE -> {
                return "livesMessage";
            }
            case SLEEPS_MESSAGE -> {
                return "sleepsMessage";
            }
        }
        return null;
    }
}