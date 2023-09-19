package quantumxenon.origins_randomiser.events;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import quantumxenon.origins_randomiser.OriginsRandomiser;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OriginsRandomiserClient {
    @SubscribeEvent
    public static void registerConfig(FMLClientSetupEvent event) {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
    }
}