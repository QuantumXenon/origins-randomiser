package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.randomiser.enums.Message.*;


public class RandomiseCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("randomise")
                .executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (config.general.randomiseOrigins) {
            if (config.command.randomiseCommand) {
                if (config.command.limitCommandUses) {
                    if (ScoreboardUtils.getValue("uses", player) > 0) {
                        OriginUtils.randomOrigin(Reason.COMMAND, player);
                        ScoreboardUtils.decrementValue("uses", player);
                        source.sendMessage(MessageUtils.getMessage(USES_LEFT, ScoreboardUtils.getValue("uses", player)));
                    } else {
                        source.sendError(MessageUtils.getMessage(OUT_OF_USES));
                    }
                } else {
                    OriginUtils.randomOrigin(Reason.COMMAND, player);
                }
            } else {
                source.sendError(MessageUtils.getMessage(COMMAND_DISABLED));
            }
        } else {
            source.sendError(MessageUtils.getMessage(RANDOMISER_DISABLED));
        }
        return 1;
    }
}