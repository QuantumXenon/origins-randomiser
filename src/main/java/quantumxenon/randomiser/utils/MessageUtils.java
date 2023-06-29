package quantumxenon.randomiser.utils;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;

public interface MessageUtils {
    static Text getMessage(Message message) {
        switch (message) {
            case COMMAND_DISABLED -> {
                return new TranslatableText("origins-randomiser.message.command.disabled");
            }
            case RANDOMISER_DISABLED -> {
                return new TranslatableText("origins-randomiser.message.disabled");
            }
            case RANDOMISER_ENABLED -> {
                return new TranslatableText("origins-randomiser.message.enabled");
            }
            case LIVES_DISABLED -> {
                return new TranslatableText("origins-randomiser.message.lives.disabled");
            }
            case OUT_OF_LIVES -> {
                return new TranslatableText("origins-randomiser.message.lives.out");
            }
            case OUT_OF_USES -> {
                return new TranslatableText("origins-randomiser.message.command.out");
            }
            case UNLIMITED_USES -> {
                return new TranslatableText("origins-randomiser.message.command.unlimitedUses");
            }
        }
        return null;
    }

    static Text getMessage(Message message, int value) {
        switch (message) {
            case LIMIT_COMMAND_USES -> {
                return new TranslatableText("origins-randomiser.message.command.limitedUses", value);
            }
            case LIVES_ENABLED -> {
                return new TranslatableText("origins-randomiser.message.lives.enabled", value);
            }
            case LIVES_REMAINING -> {
                return new TranslatableText("origins-randomiser.message.lives.remaining", value);
            }
            case LIVES_UNTIL_NEXT_RANDOMISE -> {
                return new TranslatableText("origins-randomiser.message.lives.untilRandomise", value);
            }
            case RANDOM_ORIGIN_AFTER_LIVES -> {
                return new TranslatableText("origins-randomiser.message.lives.betweenRandomises", value);
            }
            case RANDOM_ORIGIN_AFTER_SLEEPS -> {
                return new TranslatableText("origins-randomiser.message.sleeps.betweenRandomises", value);
            }
            case SLEEPS_UNTIL_NEXT_RANDOMISE -> {
                return new TranslatableText("origins-randomiser.message.sleeps.untilRandomise", value);
            }
            case USES_LEFT -> {
                return new TranslatableText("origins-randomiser.message.command.usesLeft", value);
            }
        }
        return null;
    }

    static Text getMessage(Message message, String name, int value) {
        switch (message) {
            case SET_LIVES -> {
                return new TranslatableText("origins-randomiser.message.command.setLives", name, value);
            }
            case SET_USES -> {
                return new TranslatableText("origins-randomiser.message.command.setUses", name, value);
            }
        }
        return null;
    }

    static Text getMessage(Reason reason, String player, String origin) {
        switch (reason) {
            case DEATH -> {
                return new TranslatableText("origins-randomiser.reason.death", player, origin);
            }
            case FIRST_JOIN -> {
                return new TranslatableText("origins-randomiser.reason.firstJoin", player, origin);
            }
            case SLEEP -> {
                return new TranslatableText("origins-randomiser.reason.sleep", player, origin);
            }
            default -> {
                return new TranslatableText("origins-randomiser.reason.command", player, origin);
            }
        }
    }
}