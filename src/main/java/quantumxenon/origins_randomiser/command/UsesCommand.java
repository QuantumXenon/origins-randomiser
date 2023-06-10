package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import java.util.Collection;

public class UsesCommand {
    public UsesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher
            .register(Commands.literal("setUses")
            .requires(permissions -> permissions.hasPermission(2))
            .then(Commands.argument("target", EntityArgument.players())
            .then(Commands.argument("number", IntegerArgumentType.integer()))
            .executes(UsesCommand::setCommandUses)));
    }

    private static int setCommandUses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int number = IntegerArgumentType.getInteger(context, "number");
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        CommandSourceStack source = context.getSource();

        if (ConfigUtils.limitCommandUses()) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.setValue(Objective.USES, number, player);
                source.sendSuccess(MessageUtils.getMessage(Message.SET_USES, player.getName().getString(), number), true);
            }
        } else {
            source.sendFailure(MessageUtils.getMessage(Message.UNLIMITED));
        }
        return 1;
    }
}
