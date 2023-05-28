package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Argument;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Objective;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.*;

import java.util.Objects;


public class RandomiseCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register(CommandManager.literal(CommandUtils.getArgument(Argument.RANDOMISE))
                .executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (ConfigUtils.randomiseCommand()) {
            if (ConfigUtils.limitCommandUses()) {
                if (ScoreboardUtils.getValue(Objective.USES, player) > 0) {
                    OriginUtils.randomOrigin(Reason.COMMAND, player);
                    ScoreboardUtils.decrementValue(Objective.USES, Objects.requireNonNull(player));
                    source.sendMessage(Text.of(MessageUtils.getMessage(Message.USES_LEFT, ScoreboardUtils.getValue(Objective.USES, player))));
                } else {
                    source.sendMessage(Text.of(MessageUtils.getMessage(Message.OUT_OF_USES)));
                }
            } else {
                OriginUtils.randomOrigin(Reason.COMMAND, player);
            }
        } else {
            source.sendMessage(Text.of(MessageUtils.getMessage(Message.COMMAND_DISABLED)));
        }
        return 1;
    }
}