package quantumxenon.randomiser.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "origins-randomiser")
public class OriginsRandomiserConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public General general = new General();
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Command command = new Command();
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Lives lives = new Lives();
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Other other = new Other();

    public static class General {
        @ConfigEntry.Gui.Tooltip
        public boolean randomiseOrigins = true;
        @ConfigEntry.Gui.Tooltip
        public boolean randomiserMessages = true;
        @ConfigEntry.Gui.Tooltip
        public boolean dropExtraInventory = true;
        @ConfigEntry.Gui.Tooltip
        public boolean allowDuplicateOrigins = false;
    }

    public static class Command {
        @ConfigEntry.Gui.Tooltip
        public boolean randomiseCommand = true;
        @ConfigEntry.Gui.Tooltip
        public boolean limitCommandUses = false;
        @ConfigEntry.Gui.Tooltip
        public int randomiseCommandUses = 3;
    }

    public static class Lives {
        @ConfigEntry.Gui.Tooltip
        public boolean enableLives = false;
        @ConfigEntry.Gui.Tooltip
        public int startingLives = 10;
        @ConfigEntry.Gui.Tooltip
        public int livesBetweenRandomises = 1;
    }

    public static class Other {
        @ConfigEntry.Gui.Tooltip
        public boolean deathRandomisesOrigin = true;
        @ConfigEntry.Gui.Tooltip
        public boolean sleepRandomisesOrigin = false;
        @ConfigEntry.Gui.Tooltip
        public int sleepsBetweenRandomises = 1;
    }
}