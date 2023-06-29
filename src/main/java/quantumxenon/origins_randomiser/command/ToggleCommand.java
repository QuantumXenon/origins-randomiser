package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.utils.MessageUtils;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.RANDOMISER_DISABLED;
import static quantumxenon.origins_randomiser.enums.Message.RANDOMISER_ENABLED;

public class ToggleCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public ToggleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("toggleRandomiser")
            .requires(source -> source.hasPermission(2))
            .executes(context -> toggle(context.getSource())));
    }

    private static int toggle(CommandSourceStack source) {
        if (config.general.randomiseOrigins) {
            config.general.randomiseOrigins = false;
            source.sendSuccess(MessageUtils.getMessage(RANDOMISER_DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendSuccess(MessageUtils.getMessage(RANDOMISER_ENABLED), true);
        }
        return 1;
    }
}
