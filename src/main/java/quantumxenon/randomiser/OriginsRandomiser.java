package quantumxenon.randomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import quantumxenon.randomiser.command.LivesCommand;
import quantumxenon.randomiser.command.RandomiseCommand;
import quantumxenon.randomiser.command.ToggleCommand;
import quantumxenon.randomiser.command.UsesCommand;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;

public class OriginsRandomiser implements ModInitializer {
    public void onInitialize() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
        LivesCommand.register();
        RandomiseCommand.register();
        ToggleCommand.register();
        UsesCommand.register();
    }
}