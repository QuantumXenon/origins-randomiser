package com.quantumxenon.randomiser;

import com.quantumxenon.randomiser.entity.Player;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.StringUtils;

public class Randomiser implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> randomiserMessages = GameRuleRegistry.register("randomiserMessages", GameRules.Category.field_24099, GameRuleFactory.createBooleanRule(true));

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> this.randomiseOrigin(context.getSource()))));
    }

    private int randomiseOrigin(ServerCommandSource commandSource) {
        if (commandSource.getEntity() instanceof Player sourcePlayer) {
            if (commandSource.getServer() != null) {
                Text message = Text.of(Formatting.field_1067 + commandSource.getName() + Formatting.field_1070 + " randomised their origin and is now a " + Formatting.field_1067 + StringUtils.capitalize(sourcePlayer.randomOrigin().getIdentifier().toString().split(":")[1].replace("_", " ")) + Formatting.field_1070 + ".");
                if(commandSource.getServer().getGameRules().getBoolean(randomiserMessages)) {
                    for (ServerPlayerEntity player : commandSource.getServer().getPlayerManager().getPlayerList()) {
                        player.sendMessage(message, false);
                    }
                }
            }
        }
        return 1;
    }
}