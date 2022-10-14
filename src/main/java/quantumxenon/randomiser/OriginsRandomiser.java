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
import net.minecraft.util.Formatting;
import quantumxenon.randomiser.config.RandomiserConfig;
import quantumxenon.randomiser.entity.Player;

import java.util.Collection;
import java.util.Objects;

public class OriginsRandomiser implements ModInitializer {
    public static final RandomiserConfig CONFIG = RandomiserConfig.createAndLoad();

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> randomise(context.getSource()))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register((CommandManager.literal("setLives").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setLives))))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register((CommandManager.literal("setCommandUses").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setCommandUses))))));
    }

    private String translate(String key){
        return Text.translatable(key).toString();
    }

    private int randomise(ServerCommandSource source) {
        if (source.getEntity() instanceof Player player) {
            if (CONFIG.randomiseCommand()) {
                player.randomOrigin(translate("origins-randomiser.command.randomiseCommandMessage"),true);
                if (CONFIG.limitCommandUses()) {
                    Objects.requireNonNull(source.getPlayer()).getScoreboard().getPlayerScore(source.getPlayer().getName().getString(), source.getPlayer().getScoreboard().getObjective("uses")).incrementScore(-1);
                    source.sendMessage(Text.of( translate("origins-randomiser.message.nowHave") + Formatting.BOLD + source.getPlayer().getScoreboard().getPlayerScore(source.getPlayer().getName().getString(), source.getPlayer().getScoreboard().getObjective("commandUses")) + Formatting.RESET + translate("origins-randomiser.command.usesLeft")));
                }
            } else {
                source.sendMessage(Text.translatable("origins-randomiser.command.disabled"));
            }
        }
        return 1;
    }

    private int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();
        if (CONFIG.enableLives()) {
            for (ServerPlayerEntity target : targets) {
                target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective("lives")).setScore(number);
                source.sendMessage(Text.of(translate("origins-randomiser.command.set") + target.getName().getString() + translate("origins-randomiser.command.lives") + number + "."));
            }

        } else {
            source.sendMessage(Text.translatable("origins-randomiser.lives.disabled"));
        }
        return 1;
    }

    private int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        ServerCommandSource source = context.getSource();
        if (CONFIG.limitCommandUses()) {
            for (ServerPlayerEntity target : targets) {
                target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective("uses")).setScore(number);
                source.sendMessage(Text.of(translate("origins-randomiser.command.set") + target.getName().getString() + translate("origins-randomiser.command.randomiseUses") + number + "."));
            }
        } else {
            source.sendMessage(Text.translatable("origins-randomiser.command.unlimited"));
        }
        return 1;
    }
}