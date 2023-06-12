package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.*;
import static quantumxenon.origins_randomiser.enums.Objective.USES;

public class RandomiseCommand {
    public RandomiseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("randomise").executes(context -> randomise(context.getSource())));
    }

    private static int randomise(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.randomiseCommand()) {
                if (ConfigUtils.limitCommandUses()) {
                    if (ScoreboardUtils.getValue(USES, player) > 0) {
                        OriginUtils.randomOrigin(Reason.COMMAND, player);
                        ScoreboardUtils.decrementValue(USES, player);
                        source.sendSystemMessage(MessageUtils.getMessage(USES_LEFT, ScoreboardUtils.getValue(USES, player)));
                    } else {
                        source.sendFailure(MessageUtils.getMessage(OUT_OF_USES));
                    }
                } else {
                    OriginUtils.randomOrigin(Reason.COMMAND, player);
                }
            } else {
                source.sendFailure(MessageUtils.getMessage(COMMAND_DISABLED));
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(DISABLED));
        }
        return 1;
    }
}