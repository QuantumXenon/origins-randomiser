package quantumxenon.randomiser.utils;

import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;

public interface MessageUtils {
    static Text getMessage(Message message) {
        switch (message) {
            case COMMAND_DISABLED -> {
                return Text.translatable("origins-randomiser.message.command.disabled");
            }
            case RANDOMISER_DISABLED -> {
                return Text.translatable("origins-randomiser.message.disabled");
            }
            case RANDOMISER_ENABLED -> {
                return Text.translatable("origins-randomiser.message.enabled");
            }
            case LIVES_DISABLED -> {
                return Text.translatable("origins-randomiser.message.lives.disabled");
            }
            case OUT_OF_LIVES -> {
                return Text.translatable("origins-randomiser.message.lives.out");
            }
            case OUT_OF_USES -> {
                return Text.translatable("origins-randomiser.message.command.out");
            }
            case UNLIMITED_USES -> {
                return Text.translatable("origins-randomiser.message.command.unlimitedUses");
            }
        }
        return null;
    }

    static Text getMessage(Message message, int value) {
        switch (message) {
            case LIMIT_COMMAND_USES -> {
                return Text.translatable("origins-randomiser.message.command.limitedUses", value);
            }
            case LIVES_ENABLED -> {
                return Text.translatable("origins-randomiser.message.lives.enabled", value);
            }
            case LIVES_REMAINING -> {
                return Text.translatable("origins-randomiser.message.lives.remaining", value);
            }
            case LIVES_UNTIL_NEXT_RANDOMISE -> {
                return Text.translatable("origins-randomiser.message.lives.untilRandomise", value);
            }
            case RANDOM_ORIGIN_AFTER_LIVES -> {
                return Text.translatable("origins-randomiser.message.lives.betweenRandomises", value);
            }
            case RANDOM_ORIGIN_AFTER_SLEEPS -> {
                return Text.translatable("origins-randomiser.message.sleeps.betweenRandomises", value);
            }
            case SLEEPS_UNTIL_NEXT_RANDOMISE -> {
                return Text.translatable("origins-randomiser.message.sleeps.untilRandomise", value);
            }
            case USES_LEFT -> {
                return Text.translatable("origins-randomiser.message.command.usesLeft", value);
            }
        }
        return null;
    }

    static Text getMessage(Message message, String name, int value) {
        switch (message) {
            case NEW_LIVES -> {
                return Text.translatable("origins-randomiser.message.command.newLives", name, value);
            }
            case NEW_USES -> {
                return Text.translatable("origins-randomiser.message.command.newUses", name, value);
            }
        }
        return null;
    }

    static Text getMessage(Reason reason, String player, String origin) {
        switch (reason) {
            case DEATH -> {
                return Text.translatable("origins-randomiser.reason.death", player, origin);
            }
            case FIRST_JOIN -> {
                return Text.translatable("origins-randomiser.reason.firstJoin", player, origin);
            }
            case SLEEP -> {
                return Text.translatable("origins-randomiser.reason.sleep", player, origin);
            }
            default -> {
                return Text.translatable("origins-randomiser.reason.command", player, origin);
            }
        }
    }
}