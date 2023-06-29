package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.players;
import static quantumxenon.origins_randomiser.enums.Message.*;

public class SetCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public SetCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("set")
            .requires(source -> source.hasPermission(2))
            .then(literal("lives")
                .then(argument("target", players())
                .then(argument("number", integer(0))
                .executes(SetCommand::setLives))))
            .then(literal("uses")
                .then(argument("target", players())
                .then(argument("number", integer(0))
                .executes(SetCommand::setUses)))));
    }

    private static int setLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack source = context.getSource();

        if (config.lives.enableLives) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.setValue("lives", number, player);
                source.sendSuccess(MessageUtils.getMessage(SET_LIVES, player.getScoreboardName(), number), true);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(LIVES_DISABLED));
        }
        return 1;
    }

    private static int setUses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack source = context.getSource();

        if (config.command.limitCommandUses) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.setValue("uses", number, player);
                source.sendSuccess(MessageUtils.getMessage(SET_USES, player.getScoreboardName(), number), true);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(UNLIMITED_USES));
        }
        return 1;
    }
}