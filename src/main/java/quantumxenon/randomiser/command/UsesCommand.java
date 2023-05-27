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
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.PlayerUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import java.util.Collection;

public class UsesCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register((CommandManager.literal("setCommandUses")
                .requires((permissions) -> permissions.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.players())
                .then(CommandManager.argument("number", IntegerArgumentType.integer(0))
                .executes(UsesCommand::setCommandUses))))));
    }

    private static int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int number = IntegerArgumentType.getInteger(context, "number");
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        if (ConfigUtils.limitCommandUses()) {
            for (ServerPlayerEntity player : players) {
                ScoreboardUtils.setValue("uses", number, player);
                source.sendFeedback(Text.of(MessageUtils.getMessage(Message.SET_USES, PlayerUtils.getName(player), number)), true);
            }
        } else {
            source.sendMessage(Text.of(MessageUtils.getMessage(Message.UNLIMITED)));
        }
        return 1;
    }
}