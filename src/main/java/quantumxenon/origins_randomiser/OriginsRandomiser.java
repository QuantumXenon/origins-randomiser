package quantumxenon.origins_randomiser;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.command.LivesCommand;
import quantumxenon.origins_randomiser.command.RandomiseCommand;
import quantumxenon.origins_randomiser.command.ToggleCommand;
import quantumxenon.origins_randomiser.command.UsesCommand;
import quantumxenon.origins_randomiser.utils.ConfigUtils;

@Mod(OriginsRandomiser.MOD_ID)
@Mod.EventBusSubscriber(modid = OriginsRandomiser.MOD_ID)
public class OriginsRandomiser {
    public static final String MOD_ID = "origins_randomiser";

    public OriginsRandomiser() {
        ConfigUtils.getConfig();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        new LivesCommand(event.getDispatcher());
        new RandomiseCommand(event.getDispatcher());
        new ToggleCommand(event.getDispatcher());
        new UsesCommand(event.getDispatcher());
    }
}