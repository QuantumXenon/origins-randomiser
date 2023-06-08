package quantumxenon.randomiser.utils;

import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Message;

public interface MessageUtils {
    static String translate(String key) {
        return Text.translatable(key).getString();
    }

    static String translate(String key, int value) {
        return Text.translatable(key, value).getString();
    }

    static String translate(String key, String player, int value) {
        return Text.translatable(key, player, value).getString();
    }

    static String getMessage(Message message) {
        switch (message) {
            case COMMAND_DISABLED -> {
                return translate("origins-randomiser.message.command.disabled");
            }
            case DISABLED -> {
                return translate("origins-randomiser.message.disabled");
            }
            case ENABLED -> {
                return translate("origins-randomiser.message.enabled");
            }
            case LIVES_DISABLED -> {
                return translate("origins-randomiser.message.lives.disabled");
            }
            case OUT_OF_LIVES -> {
                return translate("origins-randomiser.message.lives.out");
            }
            case OUT_OF_USES -> {
                return translate("origins-randomiser.message.command.out");
            }
            case UNLIMITED -> {
                return translate("origins-randomiser.message.command.unlimitedUses");
            }
        }
        return null;
    }

    static String getMessage(Message message, int value) {
        switch (message) {
            case LIMIT_COMMAND_USES -> {
                return translate("origins-randomiser.message.command.limitedUses", value);
            }
            case LIVES_ENABLED -> {
                return translate("origins-randomiser.message.lives.enabled", value);
            }
            case LIVES_REMAINING -> {
                return translate("origins-randomiser.message.lives.remaining", value);
            }
            case LIVES_UNTIL_RANDOMISE -> {
                return translate("origins-randomiser.message.lives.untilRandomise", value);
            }
            case RANDOM_ORIGIN_AFTER_LIVES -> {
                return translate("origins-randomiser.message.lives.betweenRandomises", value);
            }
            case RANDOM_ORIGIN_AFTER_SLEEPS -> {
                return translate("origins-randomiser.message.sleeps.betweenRandomises", value);
            }
            case SLEEPS_UNTIL_RANDOMISE -> {
                return translate("origins-randomiser.message.sleeps.untilRandomise", value);
            }
            case USES_LEFT -> {
                return translate("origins-randomiser.message.command.usesLeft", value);
            }
        }
        return null;
    }

    static String getMessage(Message message, String name, int value) {
        switch (message) {
            case SET_LIVES -> {
                return translate("origins-randomiser.message.command.setLives", name, value);
            }
            case SET_USES -> {
                return translate("origins-randomiser.message.command.setUses", name, value);
            }
        }
        return null;
    }
}