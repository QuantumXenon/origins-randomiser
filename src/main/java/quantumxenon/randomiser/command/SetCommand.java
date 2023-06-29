package quantumxenon.randomiser.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.randomiser.enums.Message.*;
import static quantumxenon.randomiser.enums.Objective.LIVES;
import static quantumxenon.randomiser.enums.Objective.USES;

public class SetCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(
                literal("set").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("lives")
                        .then(argument("target", players())
                        .then(argument("number", integer(0))
                        .executes(SetCommand::setLives))))
                    .then(literal("uses")
                        .then(argument("target", players())
                        .then(argument("number", integer(0))
                        .executes(SetCommand::setUses))))));
    }

    private static int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();

        if (config.lives.enableLives) {
            for (ServerPlayerEntity player : players) {
                ScoreboardUtils.setValue(LIVES, number, player);
                source.sendFeedback(() -> MessageUtils.getMessage(SET_LIVES, player.getEntityName(), number), true);
            }
        } else {
            source.sendError(MessageUtils.getMessage(LIVES_DISABLED));
        }
        return 1;
    }

    private static int setUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();

        if (config.command.limitCommandUses) {
            for (ServerPlayerEntity player : players) {
                ScoreboardUtils.setValue(USES, number, player);
                source.sendFeedback(() -> MessageUtils.getMessage(SET_USES, player.getEntityName(), number), true);
            }
        } else {
            source.sendError(MessageUtils.getMessage(UNLIMITED));
        }
        return 1;
    }
}