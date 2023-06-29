package quantumxenon.origins_randomiser.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Reason;

public interface MessageUtils {
    static MutableComponent getMessage(Message message) {
        switch (message) {
            case COMMAND_DISABLED -> {
                return Component.translatable("origins-randomiser.message.command.disabled");
            }
            case RANDOMISER_DISABLED -> {
                return Component.translatable("origins-randomiser.message.disabled");
            }
            case RANDOMISER_ENABLED -> {
                return Component.translatable("origins-randomiser.message.enabled");
            }
            case LIVES_DISABLED -> {
                return Component.translatable("origins-randomiser.message.lives.disabled");
            }
            case OUT_OF_LIVES -> {
                return Component.translatable("origins-randomiser.message.lives.out");
            }
            case OUT_OF_USES -> {
                return Component.translatable("origins-randomiser.message.command.out");
            }
            case UNLIMITED_USES -> {
                return Component.translatable("origins-randomiser.message.command.unlimitedUses");
            }
        }
        return null;
    }

    static MutableComponent getMessage(Message message, int value) {
        switch (message) {
            case LIMIT_COMMAND_USES -> {
                return Component.translatable("origins-randomiser.message.command.limitedUses", value);
            }
            case LIVES_ENABLED -> {
                return Component.translatable("origins-randomiser.message.lives.enabled", value);
            }
            case LIVES_REMAINING -> {
                return Component.translatable("origins-randomiser.message.lives.remaining", value);
            }
            case LIVES_UNTIL_NEXT_RANDOMISE -> {
                return Component.translatable("origins-randomiser.message.lives.untilRandomise", value);
            }
            case RANDOM_ORIGIN_AFTER_LIVES -> {
                return Component.translatable("origins-randomiser.message.lives.betweenRandomises", value);
            }
            case RANDOM_ORIGIN_AFTER_SLEEPS -> {
                return Component.translatable("origins-randomiser.message.sleeps.betweenRandomises", value);
            }
            case SLEEPS_UNTIL_NEXT_RANDOMISE -> {
                return Component.translatable("origins-randomiser.message.sleeps.untilRandomise", value);
            }
            case USES_LEFT -> {
                return Component.translatable("origins-randomiser.message.command.usesLeft", value);
            }
        }
        return null;
    }

    static MutableComponent getMessage(Message message, String name, int value) {
        switch (message) {
            case SET_LIVES -> {
                return Component.translatable("origins-randomiser.message.command.setLives", name, value);
            }
            case SET_USES -> {
                return Component.translatable("origins-randomiser.message.command.setUses", name, value);
            }
        }
        return null;
    }

    static MutableComponent getMessage(Reason reason, String player, String origin) {
        switch (reason) {
            case DEATH -> {
                return Component.translatable("origins-randomiser.reason.death", player, origin);
            }
            case FIRST_JOIN -> {
                return Component.translatable("origins-randomiser.reason.firstJoin", player, origin);
            }
            case SLEEP -> {
                return Component.translatable("origins-randomiser.reason.sleep", player, origin);
            }
            default -> {
                return Component.translatable("origins-randomiser.reason.command", player, origin);
            }
        }
    }
}
