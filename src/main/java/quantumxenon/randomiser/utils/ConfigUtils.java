package quantumxenon.randomiser.utils;

import quantumxenon.randomiser.config.OriginsRandomiserConfig;

public interface ConfigUtils {
    OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    static boolean allowDuplicateOrigins() {
        return config.general.allowDuplicateOrigins;
    }

    static boolean deathRandomisesOrigin() {
        return config.other.deathRandomisesOrigin;
    }

    static boolean dropExtraInventory() {
        return config.general.dropExtraInventory;
    }

    static boolean enableLives() {
        return config.lives.enableLives;
    }

    static boolean limitCommandUses() {
        return config.command.limitCommandUses;
    }

    static boolean randomiseCommand() {
        return config.command.randomiseCommand;
    }

    static boolean randomiseOrigins() {
        return config.general.randomiseOrigins;
    }

    static boolean randomiserMessages() {
        return config.general.randomiserMessages;
    }

    static boolean sleepRandomisesOrigin() {
        return config.other.sleepRandomisesOrigin;
    }

    static int livesBetweenRandomises() {
        return config.lives.livesBetweenRandomises;
    }
    static int randomiseCommandUses() {
        return config.command.randomiseCommandUses;
    }

    static int sleepsBetweenRandomises() {
        return config.other.sleepsBetweenRandomises;
    }

    static int startingLives() {
        return config.lives.startingLives;
    }
}