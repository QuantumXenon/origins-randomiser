package quantumxenon.origins_randomiser.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.OriginsRandomiser;
import quantumxenon.origins_randomiser.command.ChangeCommand;
import quantumxenon.origins_randomiser.command.RandomiseCommand;
import quantumxenon.origins_randomiser.command.SetCommand;
import quantumxenon.origins_randomiser.command.ToggleCommand;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.util.OriginsRandomiserPlayer;

import static net.minecraft.world.level.GameType.SPECTATOR;
import static quantumxenon.origins_randomiser.enums.Message.*;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.ID)
public class OriginsRandomiserEvents {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new ChangeCommand(event.getDispatcher());
        new RandomiseCommand(event.getDispatcher());
        new SetCommand(event.getDispatcher());
        new ToggleCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
            if (!player.hasScoreboardTag("firstJoin")) {
                player.addScoreboardTag("firstJoin");
                player.createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises);
                player.createObjective("sleepsUntilRandomise", config.sleep.sleepsBetweenRandomises);
                player.createObjective("uses", config.command.randomiseCommandUses);
                player.createObjective("lives", config.lives.startingLives);
                if (config.advanced.randomiseOnFirstJoin) {
                    player.randomiseOrigin(Reason.FIRST_JOIN);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
            if (config.general.randomiseOrigins) {
                if (config.advanced.deathRandomisesOrigin) {
                    player.changeObjectiveValue("livesUntilRandomise", -1);
                    if (config.lives.livesBetweenRandomises > 1 && player.getObjectiveValue("livesUntilRandomise") > 0) {
                        player.getAndSendMessage(LIVES_UNTIL_NEXT_RANDOMISE, player.getObjectiveValue("livesUntilRandomise"));
                    }
                    if (config.lives.enableLives) {
                        player.changeObjectiveValue("lives", -1);
                        if (player.getObjectiveValue("lives") <= 0) {
                            player.setGameMode(SPECTATOR);
                            player.getAndSendMessage(OUT_OF_LIVES);
                        } else {
                            player.getAndSendMessage(LIVES_REMAINING, player.getObjectiveValue("lives"));
                        }
                    }
                    if (player.getObjectiveValue("livesUntilRandomise") <= 0) {
                        player.randomiseOrigin(Reason.DEATH);
                    }
                }
            } else if (config.advanced.showOriginScreenOnDeath && player.isNotHuman()) {
                player.clearOrigins();
                player.openOriginsScreen();
            }
        }
    }
}