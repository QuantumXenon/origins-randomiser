package quantumxenon.randomiser.utils;

import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Message;

public interface MessageUtils {
    static String getMessage(Message message) {
        switch (message) {
            case DISABLED -> {
                return translate("origins-randomiser.message.disabled");
            }
            case ENABLED -> {
                return translate("origins-randomiser.message.enabled");
            }
            case OUT_OF_LIVES -> {
                return translate("origins-randomiser.message.outOfLives");
            }
            case UNLIMITED -> {
                return translate("origins-randomiser.command.unlimited");
            }
        }
        return null;
    }

    static String getMessage(Message message, int value) {
        switch (message) {
            case LIMIT_COMMAND_USES -> {
                return translate("origins-randomiser.message.limitCommandUses", value);
            }
            case LIVES_ENABLED -> {
                return translate("origins-randomiser.message.livesEnabled", value);
            }
            case LIVES_REMAINING -> {
                return translate("origins-randomiser.message.livesRemaining", value);
            }
            case LIVES_UNTIL_RANDOMISE -> {
                return translate("origins-randomiser.message.livesUntilRandomise", value);
            }
            case RANDOM_ORIGIN_AFTER_LIVES -> {
                return translate("origins-randomiser.message.randomOriginAfterLives", value);
            }
            case RANDOM_ORIGIN_AFTER_SLEEPS -> {
                return translate("origins-randomiser.message.randomOriginAfterSleeps", value);
            }
            case SLEEPS_UNTIL_RANDOMISE -> {
                return translate("origins-randomiser.message.sleepsUntilRandomise", value);
            }
            case USES_LEFT -> {
                return translate("origins-randomiser.command.usesLeft", value);
            }
        }
        return null;
    }

    static String getMessage(Message message, String name, int value) {
        switch (message) {
            case SET_LIVES -> {
                return translate("origins-randomiser.command.setLives", name, value);
            }
            case SET_USES -> {
                return translate("origins-randomiser.command.setUses", name, value);
            }
        }
        return null;
    }

    static String translate(String key) {
        return Text.translatable(key).getString();
    }

    static String translate(String key, int value) {
        return Text.translatable(key, value).getString();
    }

    static String translate(String key, String player, int value) {
        return Text.translatable(key, player, value).getString();
    }
}