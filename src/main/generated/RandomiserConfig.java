package quantumxenon.randomiser.config;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RandomiserConfig extends ConfigWrapper<quantumxenon.randomiser.config.OriginsRandomiserConfig> {

    private final Option<java.lang.Boolean> randomiseOrigins = this.optionForKey(new Option.Key("randomiseOrigins"));
    private final Option<java.lang.Boolean> randomiseCommand = this.optionForKey(new Option.Key("randomiseCommand"));
    private final Option<java.lang.Boolean> randomiserMessages = this.optionForKey(new Option.Key("randomiserMessages"));
    private final Option<java.lang.Boolean> enableLives = this.optionForKey(new Option.Key("enableLives"));
    private final Option<java.lang.Integer> startingLives = this.optionForKey(new Option.Key("startingLives"));
    private final Option<java.lang.Boolean> sleepRandomisesOrigin = this.optionForKey(new Option.Key("sleepRandomisesOrigin"));

    private RandomiserConfig() {
        super(quantumxenon.randomiser.config.OriginsRandomiserConfig.class);
    }

    public static RandomiserConfig createAndLoad() {
        var wrapper = new RandomiserConfig();
        wrapper.load();
        return wrapper;
    }

    public boolean randomiseOrigins() {
        return randomiseOrigins.value();
    }

    public void randomiseOrigins(boolean value) {
        instance.randomiseOrigins = value;
        randomiseOrigins.synchronizeWithBackingField();
    }

    public boolean randomiseCommand() {
        return randomiseCommand.value();
    }

    public void randomiseCommand(boolean value) {
        instance.randomiseCommand = value;
        randomiseCommand.synchronizeWithBackingField();
    }

    public boolean randomiserMessages() {
        return randomiserMessages.value();
    }

    public void randomiserMessages(boolean value) {
        instance.randomiserMessages = value;
        randomiserMessages.synchronizeWithBackingField();
    }

    public boolean enableLives() {
        return enableLives.value();
    }

    public void enableLives(boolean value) {
        instance.enableLives = value;
        enableLives.synchronizeWithBackingField();
    }

    public int startingLives() {
        return startingLives.value();
    }

    public void startingLives(int value) {
        instance.startingLives = value;
        startingLives.synchronizeWithBackingField();
    }

    public boolean sleepRandomisesOrigin() {
        return sleepRandomisesOrigin.value();
    }

    public void sleepRandomisesOrigin(boolean value) {
        instance.sleepRandomisesOrigin = value;
        sleepRandomisesOrigin.synchronizeWithBackingField();
    }




}

