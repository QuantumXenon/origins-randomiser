package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.util.OriginsRandomiserMessages;
import quantumxenon.origins_randomiser.util.OriginsRandomiserPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static quantumxenon.origins_randomiser.enums.Message.*;

public class SetCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public SetCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("set")
                .requires(source -> source.hasPermission(2))
                .then(literal("lives")
                        .then(argument("target", EntityArgument.players())
                                .then(argument("number", IntegerArgumentType.integer(0))
                                        .executes(SetCommand::setLives))))
                .then(literal("uses")
                        .then(argument("target", EntityArgument.players())
                                .then(argument("number", IntegerArgumentType.integer(0))
                                        .executes(SetCommand::setUses)))));
    }

    private static int setLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack  source = context.getSource();

        if (config.lives.enableLives) {
            for (ServerPlayer serverPlayer : players) {
                OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
                player.setObjectiveValue("lives", number);
                source.sendSuccess(() -> OriginsRandomiserMessages.getMessage(NEW_LIVES, player.getName(), player.getObjectiveValue("lives")), true);
            }
        } else {
            source.sendFailure(OriginsRandomiserMessages.getMessage(LIVES_DISABLED));
        }
        return 1;
    }

    private static int setUses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack  source = context.getSource();

        if (config.command.limitCommandUses) {
            for (ServerPlayer serverPlayer : players) {
                OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
                player.setObjectiveValue("uses", number);
                source.sendSuccess(() -> OriginsRandomiserMessages.getMessage(NEW_USES, player.getName(), player.getObjectiveValue("uses")), true);
            }
        } else {
            source.sendFailure(OriginsRandomiserMessages.getMessage(UNLIMITED_USES));
        }
        return 1;
    }
}