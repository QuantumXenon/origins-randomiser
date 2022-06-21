package com.quantumxenon.randomiser;

import com.quantumxenon.randomiser.entity.Player;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Randomiser implements ModInitializer {
    public static Logger LOGGER;

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("randomise").executes(context -> this.command(context.getSource()))));
    }

    private int command(ServerCommandSource source) {
        final Entity getEntity = source.getEntity();
        if (getEntity instanceof final Player entity) {
            if (source.getServer() != null) {
                Text message = Text.of(Formatting.field_1067 + source.getName() + Formatting.field_1070 + " randomised their origin and is now a(n) " + Formatting.field_1067 + StringUtils.capitalize(entity.randomOrigin(false).getIdentifier().toString().split(":")[1]) + Formatting.field_1070 + ".");
                for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(message, false);
                }
            }
        }
        return 1;
    }

    static {
        LOGGER = LogManager.getLogger("randomiser");
    }
}