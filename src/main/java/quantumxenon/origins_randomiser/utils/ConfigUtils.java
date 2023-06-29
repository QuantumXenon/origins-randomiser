package quantumxenon.origins_randomiser.utils;

import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;

public interface ConfigUtils {
    OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    static boolean deathRandomisesOrigin() {
        return config.other.deathRandomisesOrigin;
    }

    static boolean enableLives() {
        return config.lives.enableLives;
    }

    static int livesBetweenRandomises() {
        return config.lives.livesBetweenRandomises;
    }

    static int randomiseCommandUses() {
        return config.command.randomiseCommandUses;
    }

    static boolean randomiseOrigins() {
        return config.general.randomiseOrigins;
    }

    static int sleepsBetweenRandomises() {
        return config.other.sleepsBetweenRandomises;
    }

    static int startingLives() {
        return config.lives.startingLives;
    }
}