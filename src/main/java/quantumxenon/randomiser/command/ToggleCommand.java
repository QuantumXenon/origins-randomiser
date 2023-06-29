package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.utils.MessageUtils;

import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.randomiser.enums.Message.RANDOMISER_DISABLED;
import static quantumxenon.randomiser.enums.Message.RANDOMISER_ENABLED;

public class ToggleCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
            dispatcher.register(literal("toggleRandomiser")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> toggle(context.getSource()))));
    }

    private static int toggle(ServerCommandSource source) {
        if (config.general.randomiseOrigins) {
            config.general.randomiseOrigins = false;
            source.sendFeedback(MessageUtils.getMessage(RANDOMISER_DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendFeedback(MessageUtils.getMessage(RANDOMISER_ENABLED), true);
        }
        return 1;
    }
}