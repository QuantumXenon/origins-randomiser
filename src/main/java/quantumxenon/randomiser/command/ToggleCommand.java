package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;

import static net.minecraft.server.command.CommandManager.literal;

public class ToggleCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
            dispatcher.register(literal("toggleRandomiser").requires(source -> source.hasPermissionLevel(2))
                .executes(context -> toggle(context.getSource()))));
    }

    private static int toggle(ServerCommandSource source) {
        OriginsRandomiserConfig config = ConfigUtils.getConfig();

        if (ConfigUtils.randomiseOrigins()) {
            config.general.randomiseOrigins = false;
            source.sendFeedback(MessageUtils.getMessage(Message.DISABLED), true);
        } else {
            config.general.randomiseOrigins = true;
            source.sendFeedback(MessageUtils.getMessage(Message.ENABLED), true);
        }
        return 1;
    }
}