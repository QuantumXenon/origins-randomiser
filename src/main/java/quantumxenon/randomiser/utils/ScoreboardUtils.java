package quantumxenon.randomiser.utils;

import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Objective;
import quantumxenon.randomiser.enums.Tag;

import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static ScoreboardPlayerScore getObjective(ServerPlayerEntity player, Objective objective) {
        return player.getScoreboard().getPlayerScore(PlayerUtils.getName(player), player.getScoreboard().getObjective(objectiveName(objective)));
    }
    
    static void createObjective(Objective objective, int number, ServerPlayerEntity player) {
        String name = objectiveName(objective);
        if (!player.getScoreboard().containsObjective(name)) {
            player.getScoreboard().addObjective(name, ScoreboardCriterion.DUMMY, Text.of(name), INTEGER);
            setValue(objective, number, player);
        }
    }

    static int getValue(Objective objective, ServerPlayerEntity player) {
        return getObjective(player, objective).getScore();
    }

    static void setValue(Objective objective, int value, ServerPlayerEntity player) {
        getObjective(player, objective).setScore(value);
    }

    static void decrementValue(Objective objective, ServerPlayerEntity player) {
        getObjective(player, objective).incrementScore(-1);
    }

    static boolean noScoreboardTag(Tag tag, ServerPlayerEntity player) {
        return !player.getScoreboardTags().contains(tagName(tag));
    }

    static String objectiveName(Objective objective) {
        switch (objective) {
            case LIVES -> {
                return "lives";
            }
            case LIVES_UNTIL_RANDOMISE -> {
                return "livesUntilRandomise";
            }
            case SLEEPS_UNTIL_RANDOMISE -> {
                return "sleepsUtilRandomise";
            }
            case USES -> {
                return "uses";
            }
        }
        return null;
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