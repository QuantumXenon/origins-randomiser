package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
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

public class LivesCommand {
    public LivesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher
            .register(Commands.literal("setLives")
            .requires(permissions -> permissions.hasPermission(2))
            .then(Commands.argument("target", EntityArgument.players())
            .then(Commands.argument("number", IntegerArgumentType.integer()))
            .executes(LivesCommand::setLives)));
    }

    private static int setLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int number = IntegerArgumentType.getInteger(context, "number");
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
        CommandSource source = context.getSource().getPlayer();

        if (ConfigUtils.enableLives()) {
            for (ServerPlayer player : players) {
                ScoreboardUtils.setValue(Objective.LIVES, number, player);
                source.sendSystemMessage(MessageUtils.getMessage(Message.SET_LIVES, player.getName().getString(), number));
            }
        } else {
            source.sendSystemMessage(MessageUtils.getMessage(Message.LIVES_DISABLED));
        }
        return 1;
    }
}
