package quantumxenon.randomiser;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import quantumxenon.randomiser.config.RandomiserConfig;
import quantumxenon.randomiser.entity.Player;

import java.util.Collection;

public class OriginsRandomiser implements ModInitializer {
    public static final RandomiserConfig CONFIG = RandomiserConfig.createAndLoad();

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> randomiseOrigin(context.getSource()))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("setLives").then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setLives)))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("setCommandUses").then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setCommandUses)))));
    }

    private int randomiseOrigin(ServerCommandSource source) {
        if (source instanceof Player player) {
            if (CONFIG.randomiseCommand()) {
                player.randomOrigin(" randomised their origin and is now a ");
            } else {
                source.sendMessage(Text.of("Use of the /randomise command has been disabled."));
            }
        }
        return 1;
    }

    private int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();
        if (CONFIG.enableLives() && source.hasPermissionLevel(2)) {
            for (ServerPlayerEntity target : targets) {
                target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective("lives")).setScore(number);
                source.sendMessage(Text.of("Set " + target.getName().getString() + "'s lives to " + number + "."));
            }
        } else {
            source.sendMessage(Text.of("Lives are disabled. Toggle this with 'enableLives'."));
        }
        return 1;
    }

    private int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();
        if (CONFIG.limitCommandUses() && source.hasPermissionLevel(2)) {
            for (ServerPlayerEntity target : targets) {
                target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective("commandUses")).setScore(number);
                source.sendMessage(Text.of("Set " + target.getName().getString() + "'s /randomise uses to " + number + "."));
            }
        } else {
            source.sendMessage(Text.of("Use of the /randomise command is unlimited. Toggle this with 'limitCommandUses'."));
        }
        return 1;
    }
}