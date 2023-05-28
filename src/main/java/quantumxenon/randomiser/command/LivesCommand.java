package quantumxenon.randomiser.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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

public class LivesCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register((CommandManager.literal(CommandUtils.getArgument(Argument.SET_LIVES))
                .requires((permissions) -> permissions.hasPermissionLevel(2))
                .then(CommandManager.argument(CommandUtils.getArgument(Argument.TARGET), EntityArgumentType.players())
                .then(CommandManager.argument(CommandUtils.getArgument(Argument.NUMBER), IntegerArgumentType.integer(0))
                .executes(LivesCommand::setLives))))));
    }

    private static int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int number = IntegerArgumentType.getInteger(context, CommandUtils.getArgument(Argument.NUMBER));
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, CommandUtils.getArgument(Argument.TARGET));
        ServerCommandSource source = context.getSource();

        if (ConfigUtils.enableLives()) {
            for (ServerPlayerEntity player : players) {
                ScoreboardUtils.setValue(Objective.LIVES, number, player);
                source.sendFeedback(Text.of(MessageUtils.getMessage(Message.SET_LIVES, PlayerUtils.getName(player), number)), true);
            }
        } else {
            source.sendMessage(Text.of(MessageUtils.getMessage(Message.LIVES_DISABLED)));
        }
        return 1;
    }
}