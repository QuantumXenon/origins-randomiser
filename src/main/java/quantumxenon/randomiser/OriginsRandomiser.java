package quantumxenon.randomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import quantumxenon.randomiser.command.ChangeCommand;
import quantumxenon.randomiser.command.RandomiseCommand;
import quantumxenon.randomiser.command.SetCommand;
import quantumxenon.randomiser.command.ToggleCommand;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.events.OriginsRandomiserEvents;

public class OriginsRandomiser implements ModInitializer {
    public void onInitialize() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);

        ChangeCommand.register();
        RandomiseCommand.register();
        SetCommand.register();
        ToggleCommand.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> OriginsRandomiserEvents.firstJoin(handler.getPlayer()));
    }
}