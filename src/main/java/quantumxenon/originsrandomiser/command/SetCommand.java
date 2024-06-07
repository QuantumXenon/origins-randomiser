package quantumxenon.originsrandomiser.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.util.OriginsRandomiserMessages;
import quantumxenon.originsrandomiser.util.OriginsRandomiserPlayer;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static quantumxenon.originsrandomiser.enums.Message.*;

public class SetCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("set")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("lives")
                    .then(argument("target", EntityArgumentType.players())
                    .then(argument("number", IntegerArgumentType.integer(0))
                    .executes(SetCommand::setLives))))
                .then(literal("uses")
                    .then(argument("target", EntityArgumentType.players())
                    .then(argument("number", IntegerArgumentType.integer(0))
                    .executes(SetCommand::setUses))))));
    }

    private static int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();

        if (config.lives.enableLives) {
            for (ServerPlayerEntity serverPlayer : players) {
                OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
                player.setObjectiveValue("lives", number);
                source.sendFeedback(() -> OriginsRandomiserMessages.getMessage(NEW_LIVES, player.getName(), player.getObjectiveValue("lives")), true);
            }
        } else {
            source.sendError(OriginsRandomiserMessages.getMessage(LIVES_DISABLED));
        }
        return 1;
    }

    private static int setUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();

        if (config.command.limitCommandUses) {
            for (ServerPlayerEntity serverPlayer : players) {
                OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
                player.setObjectiveValue("uses", number);
                source.sendFeedback(() -> OriginsRandomiserMessages.getMessage(NEW_USES, player.getName(), player.getObjectiveValue("uses")), true);
            }
        } else {
            source.sendError(OriginsRandomiserMessages.getMessage(UNLIMITED_USES));
        }
        return 1;
    }
}