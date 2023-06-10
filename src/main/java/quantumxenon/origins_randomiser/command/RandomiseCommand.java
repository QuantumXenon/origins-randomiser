package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

public class RandomiseCommand {
    public RandomiseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("randomise").executes((context) -> randomise(context.getSource())));
    }

    private static int randomise(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();

        if (ConfigUtils.randomiseCommand()) {
            if (ConfigUtils.limitCommandUses()) {
                if (ScoreboardUtils.getValue(Objective.USES, player) > 0) {
                    OriginUtils.randomOrigin(Reason.COMMAND, player);
                    ScoreboardUtils.decrementValue(Objective.USES, player);
                    source.sendSystemMessage(MessageUtils.getMessage(Message.USES_LEFT, ScoreboardUtils.getValue(Objective.USES, player)));
                } else {
                    source.sendSystemMessage(MessageUtils.getMessage(Message.OUT_OF_USES));
                }
            } else {
                OriginUtils.randomOrigin(Reason.COMMAND, player);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(Message.COMMAND_DISABLED));
        }
        return 1;
    }
}