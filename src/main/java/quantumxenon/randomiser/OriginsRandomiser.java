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
import java.util.Objects;

public class OriginsRandomiser implements ModInitializer {
    public static final RandomiserConfig CONFIG = RandomiserConfig.createAndLoad();

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> randomiseOrigin(context.getSource()))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("setLives").then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setLives)))));
    }

    private int randomiseOrigin(ServerCommandSource source) {
        if (source.getEntity() instanceof Player player) {
            if (CONFIG.randomiseCommand()) {
                player.randomOrigin(" randomised their origin and is now a ");
            } else {
                source.getEntity().sendMessage(Text.of("Use of the /randomise command has been disabled."));
            }
        }
        return 1;
    }

    private int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();
        if (CONFIG.enableLives()) {
            if (source instanceof Player player && source.hasPermissionLevel(2)) {
                for (ServerPlayerEntity target : targets) {
                    player.modifyLives(0, target);
                    player.modifyLives(number, target);
                    Objects.requireNonNull(source.getEntity()).sendMessage(Text.of("Set " + target.getName().getString() + "'s lives to " + number + "."));
                }
            }
        } else {
            Objects.requireNonNull(source.getEntity()).sendMessage(Text.of("Lives are disabled. Use the 'enableLives' game-rule to toggle them."));
        }
        return 1;
    }
}