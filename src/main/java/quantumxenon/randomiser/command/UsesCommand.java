package quantumxenon.randomiser.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import quantumxenon.randomiser.enums.Argument;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Objective;
import quantumxenon.randomiser.utils.*;

import java.util.Collection;

public class UsesCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher
                .register((CommandManager.literal(CommandUtils.getArgument(Argument.SET_COMMAND_USES))
                .requires((permissions) -> permissions.hasPermissionLevel(2))
                .then(CommandManager.argument(CommandUtils.getArgument(Argument.TARGET), EntityArgumentType.players())
                .then(CommandManager.argument(CommandUtils.getArgument(Argument.NUMBER), IntegerArgumentType.integer(0))
                .executes(UsesCommand::setCommandUses))))));
    }

    private static int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int number = IntegerArgumentType.getInteger(context, CommandUtils.getArgument(Argument.NUMBER));
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, CommandUtils.getArgument(Argument.TARGET));
        ServerCommandSource source = context.getSource();

        if (ConfigUtils.limitCommandUses()) {
            for (ServerPlayerEntity player : players) {
                ScoreboardUtils.setValue(Objective.USES, number, player);
                source.sendFeedback(Text.of(MessageUtils.getMessage(Message.SET_USES, PlayerUtils.getName(player), number)), true);
            }
        } else {
            source.sendFeedback(Text.of(MessageUtils.getMessage(Message.UNLIMITED)),false);
        }
        return 1;
    }
}