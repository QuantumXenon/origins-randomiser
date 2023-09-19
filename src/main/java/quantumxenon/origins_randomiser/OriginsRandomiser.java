package quantumxenon.origins_randomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;

@Mod(OriginsRandomiser.ID)
public class OriginsRandomiser {
    public static final String ID = "origins_randomiser";

    public OriginsRandomiser() {
        MinecraftForge.EVENT_BUS.register(AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new));
    }
}