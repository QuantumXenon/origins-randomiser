package quantumxenon.randomiser.utils;

import quantumxenon.randomiser.command.LivesCommand;
import quantumxenon.randomiser.command.RandomiseCommand;
import quantumxenon.randomiser.command.ToggleCommand;
import quantumxenon.randomiser.command.UsesCommand;
import quantumxenon.randomiser.enums.Argument;

public interface CommandUtils {
    static void registerCommands() {
        LivesCommand.register();
        RandomiseCommand.register();
        ToggleCommand.register();
        UsesCommand.register();
    }

    static String getArgument(Argument argument) {
        switch (argument) {
            case NUMBER -> {
                return "number";
            }
            case RANDOMISE -> {
                return "randomise";
            }
            case SET_COMMAND_USES -> {
                return "setCommandUses";
            }
            case SET_LIVES -> {
                return "setLives";
            }
            case TARGET -> {
                return "target";
            }
            case TOGGLE_RANDOMISER -> {
                return "toggleRandomiser";
            }
        }
        return null;
    }
}
