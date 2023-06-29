package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.*;

public class RandomiseCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public RandomiseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("randomise")
            .executes(context -> randomise(context.getSource())));
    }

    private static int randomise(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (config.general.randomiseOrigins) {
            if (config.command.randomiseCommand) {
                if (config.command.limitCommandUses) {
                    if (ScoreboardUtils.getValue("uses", player) > 0) {
                        OriginUtils.randomOrigin(Reason.COMMAND, player);
                        ScoreboardUtils.decrementValue("uses", player);
                        source.sendSystemMessage(MessageUtils.getMessage(USES_LEFT, ScoreboardUtils.getValue("uses", player)));
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
            source.sendFailure(MessageUtils.getMessage(RANDOMISER_DISABLED));
        }
        return 1;
    }
}