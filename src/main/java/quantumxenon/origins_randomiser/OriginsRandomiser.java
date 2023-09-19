package quantumxenon.origins_randomiser;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(OriginsRandomiser.ID)
public class OriginsRandomiser {
    public static final String ID = "origins_randomiser";

    public OriginsRandomiser() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}