package quantumxenon.randomiser.config;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;

public class RandomiserConfig extends ConfigWrapper<OriginsRandomiserConfig> {

    private final Option<Boolean> randomiseOrigins = this.optionForKey(new Option.Key("randomiseOrigins"));
    private final Option<Boolean> randomiserMessages = this.optionForKey(new Option.Key("randomiserMessages"));
    private final Option<Boolean> randomiseCommand = this.optionForKey(new Option.Key("randomiseCommand"));
    private final Option<Boolean> limitCommandUses = this.optionForKey(new Option.Key("limitCommandUses"));
    private final Option<Integer> randomiseCommandUses = this.optionForKey(new Option.Key("randomiseCommandUses"));
    private final Option<Boolean> enableLives = this.optionForKey(new Option.Key("enableLives"));
    private final Option<Integer> startingLives = this.optionForKey(new Option.Key("startingLives"));
    private final Option<Integer> livesBetweenRandomises = this.optionForKey(new Option.Key("livesBetweenRandomises"));
    private final Option<Boolean> sleepRandomisesOrigin = this.optionForKey(new Option.Key("sleepRandomisesOrigin"));
    private final Option<Integer> sleepsBetweenRandomises = this.optionForKey(new Option.Key("sleepsBetweenRandomises"));

    private RandomiserConfig() {
        super(OriginsRandomiserConfig.class);
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

    public boolean randomiserMessages() {
        return randomiserMessages.value();
    }

    public void randomiserMessages(boolean value) {
        instance.randomiserMessages = value;
        randomiserMessages.synchronizeWithBackingField();
    }

    public boolean randomiseCommand() {
        return randomiseCommand.value();
    }

    public void randomiseCommand(boolean value) {
        instance.randomiseCommand = value;
        randomiseCommand.synchronizeWithBackingField();
    }

    public boolean limitCommandUses() {
        return limitCommandUses.value();
    }

    public void limitCommandUses(boolean value) {
        instance.limitCommandUses = value;
        limitCommandUses.synchronizeWithBackingField();
    }

    public int randomiseCommandUses() {
        return randomiseCommandUses.value();
    }

    public void randomiseCommandUses(int value) {
        instance.randomiseCommandUses = value;
        randomiseCommandUses.synchronizeWithBackingField();
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

    public int livesBetweenRandomises() {
        return livesBetweenRandomises.value();
    }

    public void livesBetweenRandomises(int value) {
        instance.livesBetweenRandomises = value;
        livesBetweenRandomises.synchronizeWithBackingField();
    }

    public boolean sleepRandomisesOrigin() {
        return sleepRandomisesOrigin.value();
    }

    public void sleepRandomisesOrigin(boolean value) {
        instance.sleepRandomisesOrigin = value;
        sleepRandomisesOrigin.synchronizeWithBackingField();
    }

    public int sleepsBetweenRandomises() {
        return sleepsBetweenRandomises.value();
    }

    public void sleepsBetweenRandomises(int value) {
        instance.sleepsBetweenRandomises = value;
        sleepsBetweenRandomises.synchronizeWithBackingField();
	}
}

