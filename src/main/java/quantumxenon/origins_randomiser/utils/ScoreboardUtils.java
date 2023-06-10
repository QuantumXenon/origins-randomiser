package quantumxenon.origins_randomiser.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Score;
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.enums.Tag;

import static net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY;
import static net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER;

public interface ScoreboardUtils {
    private static Score getObjective(ServerPlayer player, Objective objective) {
        return player.getScoreboard().getOrCreatePlayerScore(objectiveName(objective), player.getScoreboard().getObjective(objectiveName(objective)));
    }

    static void createObjective(Objective objective, int number, ServerPlayer player) {
        String name = objectiveName(objective);
        if (!player.getScoreboard().hasObjective(name)) {
            player.getScoreboard().addObjective(name, DUMMY, Component.literal(name), INTEGER);
            setValue(objective, number, player);
        }
    }

    static int getValue(Objective objective, ServerPlayer player) {
        return getObjective(player, objective).getScore();
    }

    static void setValue(Objective objective, int value, ServerPlayer player) {
        getObjective(player, objective).setScore(value);
    }

    static void decrementValue(Objective objective, ServerPlayer player) {
        getObjective(player, objective).add(-1);
    }

    static boolean noScoreboardTag(Tag tag, ServerPlayer player) {
        return !player.getTags().contains(tagName(tag));
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