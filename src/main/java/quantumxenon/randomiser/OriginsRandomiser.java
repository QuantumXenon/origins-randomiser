package quantumxenon.randomiser;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.entity.Player;

import java.util.Collection;
import java.util.Objects;

public class OriginsRandomiser implements ModInitializer {
    public static OriginsRandomiserConfig config;
    public static OriginsRandomiserConfig defaultConfig = null;
    private static final Logger LOGGER = LoggerFactory.getLogger("origins-randomiser");
    private ServerCommandSource commandSource;

    public void onInitialize() {
        try {
            AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
        }
        catch (Exception e) {
            defaultConfig = new OriginsRandomiserConfig();
            LOGGER.error("Origins Randomiser config is broken! Delete 'origins-randomiser.json' to generate a new one.", e);
        }
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> randomise(context.getSource()))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register((CommandManager.literal("setLives").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setLives))))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register((CommandManager.literal("setCommandUses").requires((permissions) -> permissions.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.players()).then(CommandManager.argument("number", IntegerArgumentType.integer(1)).executes(this::setCommandUses))))));
        config = OriginsRandomiserConfig.getConfig();
    }

    private String translate(String key) {
        return Text.translatable(key).getString();
    }

    private void send(String message) {
        commandSource.sendMessage(Text.of(message));
    }

    private int getUses() {
        return Objects.requireNonNull(commandSource.getPlayer()).getScoreboard().getPlayerScore(commandSource.getName(), commandSource.getPlayer().getScoreboard().getObjective("uses")).getScore();
    }

    private void decrementUses() {
        Objects.requireNonNull(commandSource.getPlayer()).getScoreboard().getPlayerScore(commandSource.getName(), commandSource.getPlayer().getScoreboard().getObjective("uses")).incrementScore(-1);
    }

    private void setValue(ServerPlayerEntity target, String objective, int value) {
        target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective(objective)).setScore(value);
    }

    private int randomise(ServerCommandSource source) {
        commandSource = source;
        if (commandSource.getEntity() instanceof Player player) {
            if (config.command.randomiseCommand) {
                player.randomOrigin(translate("origins-randomiser.reason.command"));
                if (config.command.limitCommandUses) {
                    decrementUses();
                    send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getUses() + " " + Formatting.RESET + translate("origins-randomiser.command.usesRemaining"));
                }
            } else {
                send(translate("origins-randomiser.command.disabled"));
            }
        }
        return 1;
    }

    private int setLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        commandSource = context.getSource();
        if (config.lives.enableLives) {
            for (ServerPlayerEntity target : targets) {
                setValue(target, "lives", number);
                send(translate("origins-randomiser.command.set") + " " + target.getName().getString() + translate("origins-randomiser.command.lives") + " " + number + ".");
            }
        } else {
            send(translate("origins-randomiser.lives.disabled"));
        }
        return 1;
    }

    private int setCommandUses(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int number = IntegerArgumentType.getInteger(context, "number");
        commandSource = context.getSource();
        if (config.command.limitCommandUses) {
            for (ServerPlayerEntity target : targets) {
                setValue(target, "uses", number);
                send(translate("origins-randomiser.command.set") + " " + target.getName().getString() + translate("origins-randomiser.command.uses") + " " + number + ".");
            }
        } else {
            send(translate("origins-randomiser.command.unlimited"));
        }
        return 1;
    }
}