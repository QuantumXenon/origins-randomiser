package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;

public class ToggleCommand {
    public ToggleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("toggleRandomiser").executes((context) -> toggle(context.getSource())));
    }

    private static int toggle(CommandSourceStack source) {
        OriginsRandomiserConfig config = ConfigUtils.getConfig();

        if (ConfigUtils.randomiseOrigins()) {
            config.general.randomiseOrigins = false;
            source.sendSuccess(MessageUtils.getMessage(Message.DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendSuccess(MessageUtils.getMessage(Message.ENABLED), true);
        }
        return 1;
    }
}