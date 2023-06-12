package quantumxenon.randomiser.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import java.util.Objects;

import static quantumxenon.randomiser.enums.Message.*;
import static quantumxenon.randomiser.enums.Objective.USES;


public class RandomiseCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
            .register(CommandManager.literal("randomise").executes(context -> randomise(context.getSource()))));
    }

    private static int randomise(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.randomiseCommand()) {
                if (ConfigUtils.limitCommandUses()) {
                    if (ScoreboardUtils.getValue(USES, player) > 0) {
                        OriginUtils.randomOrigin(Reason.COMMAND, player);
                        ScoreboardUtils.decrementValue(USES, Objects.requireNonNull(player));
                        source.sendMessage(MessageUtils.getMessage(USES_LEFT, ScoreboardUtils.getValue(USES, player)));
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
            source.sendError(MessageUtils.getMessage(DISABLED));
        }
        return 1;
    }
}