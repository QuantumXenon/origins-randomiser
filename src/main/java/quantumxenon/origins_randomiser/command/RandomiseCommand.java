package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.util.OriginsRandomiserMessages;
import quantumxenon.origins_randomiser.util.OriginsRandomiserPlayer;

import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.*;


public class RandomiseCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public RandomiseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("randomise")
                .executes(context -> randomise(context.getSource())));
    }

    private static int randomise(CommandSourceStack source) {
        OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(source.getPlayer());  // TODO: Check what happens if this is run from the console
        if (config.general.randomiseOrigins) {
            if (config.command.randomiseCommand) {
                if (player.isNotHuman()) {
                    if (config.command.limitCommandUses) {
                        if (player.getObjectiveValue("uses") > 0) {
                            player.randomiseOrigin(Reason.COMMAND);
                            player.changeObjectiveValue("uses", -1);
                            source.sendSuccess(() -> OriginsRandomiserMessages.getMessage(USES_LEFT, player.getObjectiveValue("uses")), true);
                        } else {
                            source.sendFailure(OriginsRandomiserMessages.getMessage(OUT_OF_USES));
                        }
                    } else {
                        player.randomiseOrigin(Reason.COMMAND);
                    }
                } else {
                    source.sendFailure(OriginsRandomiserMessages.getMessage(IS_HUMAN));
                }
            } else {
                source.sendFailure(OriginsRandomiserMessages.getMessage(COMMAND_DISABLED));
            }
        } else {
            source.sendFailure(OriginsRandomiserMessages.getMessage(RANDOMISER_DISABLED));
        }
        return 1;
    }
}