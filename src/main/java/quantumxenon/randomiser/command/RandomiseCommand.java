package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;


public class RandomiseCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register(CommandManager.literal("randomise")
                .executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) {
        if (ConfigUtils.randomiseCommand()) {
            OriginUtils.randomOrigin(Reason.COMMAND, source.getPlayer());
            if (ConfigUtils.limitCommandUses()) {
                ScoreboardUtils.decrementValue("uses", source.getPlayer());
                source.sendMessage(Text.of(MessageUtils.getMessage(Message.USES_LEFT, ScoreboardUtils.getValue("uses", source.getPlayer()))));
            }
        } else {
            source.sendMessage(Text.of(MessageUtils.getMessage(Message.DISABLED)));
        }
        return 1;
    }
}