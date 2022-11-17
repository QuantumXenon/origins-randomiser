package quantumxenon.randomiser;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.entity.Player;

import java.util.Collection;
import java.util.Objects;

public class OriginsRandomiser implements ModInitializer {
    private static ServerCommandSource commandSource;
    private static OriginsRandomiserConfig config;

    public void onInitialize() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
        config = OriginsRandomiserConfig.getConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> randomise(context.getSource()))));
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register((CommandManager.literal("setLives").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(0)).executes(this::setLives))))));
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register((CommandManager.literal("setCommandUses").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(0)).executes(this::setCommandUses))))));
    }

    private String translate(String key) {
        return new TranslatableText(key).getString();
    }

    private void send(String message) {
        commandSource.sendFeedback(Text.of(message), false);
    }

    private String getName(ServerPlayerEntity player) {
        return player.getName().getString();
    }

    private int getNumber(CommandContext<ServerCommandSource> context) {
        return IntegerArgumentType.getInteger(context, "number");
    }

    private int getUses(ServerCommandSource source) throws CommandSyntaxException {
        return Objects.requireNonNull(source.getPlayer()).getScoreboard().getPlayerScore(source.getName(), source.getPlayer().getScoreboard().getObjective("uses")).getScore();
    }

    private void decrementUses(ServerCommandSource source) throws CommandSyntaxException {
        Objects.requireNonNull(source.getPlayer()).getScoreboard().getPlayerScore(source.getName(), source.getPlayer().getScoreboard().getObjective("uses")).incrementScore(-1);
    }

    private void setValue(ServerPlayerEntity target, String objective, int value) {
        target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective(objective)).setScore(value);
    }

    private Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return EntityArgumentType.getPlayers(context, "player");
    }

    private int randomise(ServerCommandSource source) throws CommandSyntaxException {
        commandSource = source;
        if (source.getEntity() instanceof Player player) {
            if (config.command.randomiseCommand) {
                player.randomOrigin(translate("origins-randomiser.reason.command"));
                if (config.command.limitCommandUses) {
                    decrementUses(source);
                    source.sendFeedback(new TranslatableText("origins-randomiser.command.usesLeft", getUses(source)), true);
                }
            } else {
                send(translate("origins-randomiser.command.disabled"));
            }
        }
        return 1;
    }

    private int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int number = getNumber(context);
        commandSource = context.getSource();
        if (config.lives.enableLives) {
            for (ServerPlayerEntity player : getPlayers(context)) {
                setValue(player, "lives", number);
                commandSource.sendFeedback(new TranslatableText("origins-randomiser.command.setLives", getName(player), number), true);
            }
        } else {
            send(translate("origins-randomiser.lives.disabled"));
        }
        return 1;
    }

    private int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int number = getNumber(context);
        commandSource = context.getSource();
        if (config.command.limitCommandUses) {
            for (ServerPlayerEntity player : getPlayers(context)) {
                setValue(player, "uses", number);
                commandSource.sendFeedback(new TranslatableText("origins-randomiser.command.setUses", getName(player), number), true);
            }
        } else {
            send(translate("origins-randomiser.command.unlimited"));
        }
        return 1;
    }
}