package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.DISABLED;
import static quantumxenon.origins_randomiser.enums.Message.ENABLED;

public class ToggleCommand {
    public ToggleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("toggleRandomiser")
                .requires(source -> source.hasPermission(2))
                .executes(context -> toggle(context.getSource())));
    }

    private static int toggle(CommandSourceStack source) {
        OriginsRandomiserConfig config = ConfigUtils.getConfig();

        if (ConfigUtils.randomiseOrigins()) {
            config.general.randomiseOrigins = false;
            source.sendSuccess(MessageUtils.getMessage(DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendSuccess(MessageUtils.getMessage(ENABLED), true);
        }
        return 1;
    }
}
