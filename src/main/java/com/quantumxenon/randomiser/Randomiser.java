package com.quantumxenon.randomiser;

import com.quantumxenon.randomiser.entity.Player;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.Objects;

public class Randomiser implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> randomiserMessages = GameRuleRegistry.register("randomiserMessages", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> randomiseOrigins = GameRuleRegistry.register("randomiseOrigins", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> randomiseCommand = GameRuleRegistry.register("randomiseCommand", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> sleepRandomisesOrigin = GameRuleRegistry.register("sleepRandomisesOrigin", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> this.randomiseOrigin(context.getSource()))));
    }

    private int randomiseOrigin(ServerCommandSource commandSource) {
        if (commandSource.getEntity() instanceof Player sourcePlayer) {
            if (commandSource.getServer() != null && (commandSource.getServer()).getGameRules().getBoolean(randomiseCommand)) {
                if (Objects.requireNonNull(commandSource.getServer()).getGameRules().getBoolean(randomiseOrigins)) {
                    sourcePlayer.randomOrigin(" randomised their origin and is now a ");
                } else {
                    commandSource.getEntity().sendMessage(Text.of("Origin randomising has been disabled."));
                }
            } else {
                commandSource.getEntity().sendMessage(Text.of("Use of the /randomise command has been disabled."));
            }
        }
        return 1;
    }
}