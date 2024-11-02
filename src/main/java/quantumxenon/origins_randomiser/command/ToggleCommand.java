package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.util.OriginsRandomiserMessages;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.RANDOMISER_DISABLED;
import static quantumxenon.origins_randomiser.enums.Message.RANDOMISER_ENABLED;

public class ToggleCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public ToggleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("toggleOriginsRandomiser")
                .requires(source -> source.hasPermission(2))
                .executes(context -> toggleOriginsRandomiser(context.getSource())));
    }

    private static int toggleOriginsRandomiser(CommandSourceStack  source) {
        if (config.general.randomiseOrigins) {
            config.general.randomiseOrigins = false;
            source.sendFailure(OriginsRandomiserMessages.getMessage(RANDOMISER_DISABLED));
        } else {
            config.general.randomiseOrigins = true;
            source.sendSuccess(OriginsRandomiserMessages.getMessage(RANDOMISER_ENABLED), true);
        }
        return 1;
    }
}