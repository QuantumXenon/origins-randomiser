package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import java.util.Objects;


public class RandomiseCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register(CommandManager.literal("randomise")
                .executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (ConfigUtils.randomiseCommand()) {
            OriginUtils.randomOrigin(Reason.COMMAND, player);
            if (ConfigUtils.limitCommandUses()) {
                ScoreboardUtils.decrementValue("uses", Objects.requireNonNull(player));
                source.sendMessage(Text.of(MessageUtils.getMessage(Message.USES_LEFT, ScoreboardUtils.getValue("uses", player))));
            }
        } else {
            source.sendMessage(Text.of(MessageUtils.getMessage(Message.COMMAND_DISABLED)));
        }
        return 1;
    }
}