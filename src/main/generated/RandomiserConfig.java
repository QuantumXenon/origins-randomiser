package quantumxenon.randomiser.config;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RandomiserConfig extends ConfigWrapper<quantumxenon.randomiser.config.OriginsRandomiserConfig> {

    private final Option<java.lang.Boolean> randomiseOrigins = this.optionForKey(new Option.Key("randomiseOrigins"));
    private final Option<java.lang.Boolean> randomiserMessages = this.optionForKey(new Option.Key("randomiserMessages"));
    private final Option<java.lang.Boolean> randomiseCommand = this.optionForKey(new Option.Key("randomiseCommand"));
    private final Option<java.lang.Boolean> limitCommandUses = this.optionForKey(new Option.Key("limitCommandUses"));
    private final Option<java.lang.Integer> randomiseCommandUses = this.optionForKey(new Option.Key("randomiseCommandUses"));
    private final Option<java.lang.Boolean> enableLives = this.optionForKey(new Option.Key("enableLives"));
    private final Option<java.lang.Integer> startingLives = this.optionForKey(new Option.Key("startingLives"));
    private final Option<java.lang.Integer> livesBetweenRandomises = this.optionForKey(new Option.Key("livesBetweenRandomises"));
    private final Option<java.lang.Boolean> sleepRandomisesOrigin = this.optionForKey(new Option.Key("sleepRandomisesOrigin"));
    private final Option<java.lang.Integer> sleepsBetweenRandomises = this.optionForKey(new Option.Key("sleepsBetweenRandomises"));

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
        randomiseOrigins.set(value);
    }

    public boolean randomiserMessages() {
        return randomiserMessages.value();
    }

    public void randomiserMessages(boolean value) {
        randomiserMessages.set(value);
    }

    public boolean randomiseCommand() {
        return randomiseCommand.value();
    }

    public void randomiseCommand(boolean value) {
        randomiseCommand.set(value);
    }

    public boolean limitCommandUses() {
        return limitCommandUses.value();
    }

    public void limitCommandUses(boolean value) {
        limitCommandUses.set(value);
    }

    public int randomiseCommandUses() {
        return randomiseCommandUses.value();
    }

    public void randomiseCommandUses(int value) {
        randomiseCommandUses.set(value);
    }

    public boolean enableLives() {
        return enableLives.value();
    }

    public void enableLives(boolean value) {
        enableLives.set(value);
    }

    public int startingLives() {
        return startingLives.value();
    }

    public void startingLives(int value) {
        startingLives.set(value);
    }

    public int livesBetweenRandomises() {
        return livesBetweenRandomises.value();
    }

    public void livesBetweenRandomises(int value) {
        livesBetweenRandomises.set(value);
    }

    public boolean sleepRandomisesOrigin() {
        return sleepRandomisesOrigin.value();
    }

    public void sleepRandomisesOrigin(boolean value) {
        sleepRandomisesOrigin.set(value);
    }

    public int sleepsBetweenRandomises() {
        return sleepsBetweenRandomises.value();
    }

    public void sleepsBetweenRandomises(int value) {
        sleepsBetweenRandomises.set(value);
    }




}

