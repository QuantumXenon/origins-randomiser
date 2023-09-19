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

public class ChangeCommand {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public ChangeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("change")
                .requires(source -> source.hasPermission(2))
                .then(literal("lives")
                        .then(argument("target", players())
                                .then(argument("number", integer())
                                        .executes(ChangeCommand::changeLives))))
                .then(literal("uses")
                        .then(argument("target", players())
                                .then(argument("number", integer())
                                        .executes(ChangeCommand::changeUses)))));
    }

    private static int changeLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack source = context.getSource();

        if (config.lives.enableLives) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.changeValue("lives", number, player);
                source.sendSuccess(() -> MessageUtils.getMessage(NEW_LIVES, player.getScoreboardName(), ScoreboardUtils.getValue("lives", player)), true);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(LIVES_DISABLED));
        }
        return 1;
    }

    private static int changeUses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        final int number = IntegerArgumentType.getInteger(context, "number");
        CommandSourceStack source = context.getSource();

        if (config.command.limitCommandUses) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.changeValue("uses", number, player);
                source.sendSuccess(() -> MessageUtils.getMessage(NEW_USES, player.getScoreboardName(), ScoreboardUtils.getValue("uses", player)), true);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(UNLIMITED_USES));
        }
        return 1;
    }
}