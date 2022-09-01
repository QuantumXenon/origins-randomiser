package com.quantumxenon.randomiser;

import com.quantumxenon.randomiser.entity.Player;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;

public class OriginsRandomiser implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> randomiserMessages = GameRuleRegistry.register("randomiserMessages", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> randomiseOrigins = GameRuleRegistry.register("randomiseOrigins", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> randomiseCommand = GameRuleRegistry.register("randomiseCommand", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> sleepRandomisesOrigin = GameRuleRegistry.register("sleepRandomisesOrigin", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> this.randomiseOrigin(context.getSource()))));
    }

    private int randomiseOrigin(ServerCommandSource commandSource) {
        if (commandSource.getEntity() instanceof Player sourcePlayer) {
            if (commandSource.getServer().getGameRules().getBoolean(randomiseCommand)) {
                sourcePlayer.randomOrigin(" randomised their origin and is now a ");
            } else {
                commandSource.getEntity().sendSystemMessage(new LiteralText("Use of the /randomise command has been disabled."), Util.NIL_UUID);
            }
        }
        return 1;
    }
}