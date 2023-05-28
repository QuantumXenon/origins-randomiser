package quantumxenon.randomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.utils.CommandUtils;

public class OriginsRandomiser implements ModInitializer {
    public void onInitialize() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
        CommandUtils.registerCommands();
    }
}