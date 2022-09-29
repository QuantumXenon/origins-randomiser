package quantumxenon.randomiser.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;

@Modmenu(modId = "origins-randomiser")
@Config(name = "origins-randomiser", wrapperName = "RandomiserConfig")
public class OriginsRandomiserConfig {
    @SectionHeader("General")
    public boolean randomiseOrigins = true;
    public boolean randomiserMessages = true;

    @SectionHeader("Command")
    public boolean randomiseCommand = true;
    public boolean limitCommandUses = false;
    public int randomiseCommandUses = 3;

    @SectionHeader("Lives")
    public boolean enableLives = false;
    public int startingLives = 10;
    public int livesBetweenRandomises = 1;

    @SectionHeader("Other")
    public boolean sleepRandomisesOrigin = false;
}
