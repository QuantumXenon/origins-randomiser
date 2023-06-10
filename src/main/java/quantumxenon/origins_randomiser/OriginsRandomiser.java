package quantumxenon.origins_randomiser;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(OriginsRandomiser.MOD_ID)
public class OriginsRandomiser {
    public static final String MOD_ID = "origins_randomiser";

    public OriginsRandomiser() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}