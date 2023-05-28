package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Argument;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.utils.CommandUtils;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;

public class ToggleCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register(CommandManager.literal(CommandUtils.getArgument(Argument.TOGGLE_RANDOMISER))
                .requires((permissions) -> permissions.hasPermissionLevel(2))
                .executes(context -> toggle(context.getSource()))));
    }

    private static int toggle(ServerCommandSource source) {
        OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

        if (ConfigUtils.randomiseOrigins()) {
            config.general.randomiseOrigins = false;
            source.sendFeedback(() -> Text.of(MessageUtils.getMessage(Message.DISABLED)), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendFeedback(() -> Text.of(MessageUtils.getMessage(Message.ENABLED)), true);
        }
        return 1;
    }
}