package quantumxenon.originsrandomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.util.OriginsRandomiserMessages;

import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.originsrandomiser.enums.Message.RANDOMISER_DISABLED;
import static quantumxenon.originsrandomiser.enums.Message.RANDOMISER_ENABLED;

public class ToggleCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("toggleOriginsRandomiser")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> toggleOriginsRandomiser(context.getSource()))));
    }

    private static int toggleOriginsRandomiser(ServerCommandSource source) {
        if (config.general.randomiseOrigins) {
            config.general.randomiseOrigins = false;
            source.sendFeedback(OriginsRandomiserMessages.getMessage(RANDOMISER_DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendFeedback(OriginsRandomiserMessages.getMessage(RANDOMISER_ENABLED), true);
        }
        return 1;
    }
}