package quantumxenon.originsrandomiser.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Reason;
import quantumxenon.originsrandomiser.util.OriginsRandomiserMessages;
import quantumxenon.originsrandomiser.util.OriginsRandomiserPlayer;

import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.originsrandomiser.enums.Message.*;


public class RandomiseCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
                dispatcher.register(literal("randomise")
                        .executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) throws CommandSyntaxException {
        OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(source.getPlayer());  // TODO: Check what happens if this is run from the console
        if (config.general.randomiseOrigins) {
            if (config.command.randomiseCommand) {
                if (player.isNotHuman()) {
                    if (config.command.limitCommandUses) {
                        if (player.getObjectiveValue("uses") > 0) {
                            player.randomiseOrigin(Reason.COMMAND);
                            player.changeObjectiveValue("uses", -1);
                            source.sendFeedback(OriginsRandomiserMessages.getMessage(USES_LEFT, player.getObjectiveValue("uses")), false);
                        } else {
                            source.sendError(OriginsRandomiserMessages.getMessage(OUT_OF_USES));
                        }
                    } else {
                        player.randomiseOrigin(Reason.COMMAND);
                    }
                } else {
                    source.sendError(OriginsRandomiserMessages.getMessage(IS_HUMAN));
                }
            } else {
                source.sendError(OriginsRandomiserMessages.getMessage(COMMAND_DISABLED));
            }
        } else {
            source.sendError(OriginsRandomiserMessages.getMessage(RANDOMISER_DISABLED));
        }
        return 1;
    }
}