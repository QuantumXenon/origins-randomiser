package quantumxenon.originsrandomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import quantumxenon.originsrandomiser.command.ChangeCommand;
import quantumxenon.originsrandomiser.command.RandomiseCommand;
import quantumxenon.originsrandomiser.command.SetCommand;
import quantumxenon.originsrandomiser.command.ToggleCommand;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.util.OriginsRandomiserEvents;

public class OriginsRandomiser implements ModInitializer {
    public void onInitialize() {
        registerConfig();
        registerCommands();
        registerEvents();
    }

    private void registerConfig() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
    }

    private void registerCommands() {
        ChangeCommand.register();
        RandomiseCommand.register();
        SetCommand.register();
        ToggleCommand.register();
    }

    private void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> OriginsRandomiserEvents.join(handler.getPlayer()));
    }
}