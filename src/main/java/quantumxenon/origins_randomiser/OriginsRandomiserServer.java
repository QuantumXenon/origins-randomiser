package quantumxenon.origins_randomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.MOD_ID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OriginsRandomiserServer {
    @SubscribeEvent
    public static void registerConfig(FMLDedicatedServerSetupEvent event) {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);
    }
}