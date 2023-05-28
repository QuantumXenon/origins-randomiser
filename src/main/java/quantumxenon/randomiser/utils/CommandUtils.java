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
                return MessageUtils.translate("origins-randomiser.command.number");
            }
            case RANDOMISE -> {
                return MessageUtils.translate("origins-randomiser.command.randomise");
            }
            case SET_COMMAND_USES -> {
                return MessageUtils.translate("origins-randomiser.command.setCommandUses");
            }
            case SET_LIVES -> {
                return MessageUtils.translate("origins-randomiser.command.setLives");
            }
            case TARGET -> {
                return MessageUtils.translate("origins-randomiser.command.target");
            }
            case TOGGLE_RANDOMISER -> {
                return MessageUtils.translate("origins-randomiser.command.toggleRandomiser");
            }
        }
        return null;
    }
}
