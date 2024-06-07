package quantumxenon.originsrandomiser;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import quantumxenon.originsrandomiser.command.ChangeCommand;
import quantumxenon.originsrandomiser.command.RandomiseCommand;
import quantumxenon.originsrandomiser.command.SetCommand;
import quantumxenon.originsrandomiser.command.ToggleCommand;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Reason;
import quantumxenon.originsrandomiser.util.OriginsRandomiserPlayer;

public class OriginsRandomiser implements ModInitializer {
    public void onInitialize() {
        AutoConfig.register(OriginsRandomiserConfig.class, GsonConfigSerializer::new);

        ChangeCommand.register();
        RandomiseCommand.register();
        SetCommand.register();
        ToggleCommand.register();

        configureFirstJoin();
    }

    private void configureFirstJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(handler.getPlayer());
            OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

            if (!player.hasScoreboardTag("firstJoin")) {
                player.createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises);
                player.createObjective("sleepsUntilRandomise", config.sleep.sleepsBetweenRandomises);
                player.createObjective("uses", config.command.randomiseCommandUses);
                player.createObjective("lives", config.lives.startingLives);
                if (config.advanced.randomiseOnFirstJoin) {
                    player.randomiseOrigin(Reason.FIRST_JOIN);
                }
            }
        });
    }
}