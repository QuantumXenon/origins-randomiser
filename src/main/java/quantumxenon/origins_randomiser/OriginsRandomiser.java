package quantumxenon.origins_randomiser;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.command.RandomiseCommand;

@Mod(OriginsRandomiser.MOD_ID)
@Mod.EventBusSubscriber(modid = OriginsRandomiser.MOD_ID)
public class OriginsRandomiser {
    public static final String MOD_ID = "origins_randomiser";

    public OriginsRandomiser() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        new RandomiseCommand(event.getDispatcher());
    }
}