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
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.world.level.GameType.SPECTATOR;
import static quantumxenon.origins_randomiser.enums.Message.*;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.ID)
public class OriginsRandomiserEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new ChangeCommand(event.getDispatcher());
        new RandomiseCommand(event.getDispatcher());
        new SetCommand(event.getDispatcher());
        new ToggleCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (ScoreboardUtils.noScoreboardTag("firstJoin", player)) {
                player.addTag("firstJoin");
                ScoreboardUtils.createObjective("livesUntilRandomise", ConfigUtils.livesBetweenRandomises(), player);
                ScoreboardUtils.createObjective("sleepsUntilRandomise", ConfigUtils.sleepsBetweenRandomises(), player);
                ScoreboardUtils.createObjective("uses", ConfigUtils.randomiseCommandUses(), player);
                ScoreboardUtils.createObjective("lives", ConfigUtils.startingLives(), player);
                if (ConfigUtils.randomiseOrigins()) {
                    OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.isEndConquered()) {
            if (ConfigUtils.randomiseOrigins()) {
                if (ConfigUtils.deathRandomisesOrigin()) {
                    ScoreboardUtils.changeValue("livesUntilRandomise", -1, player);
                    if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue("livesUntilRandomise", player) > 0) {
                        player.sendSystemMessage(MessageUtils.getMessage(LIVES_UNTIL_NEXT_RANDOMISE, ScoreboardUtils.getValue("livesUntilRandomise", player)));
                    }
                    if (ConfigUtils.enableLives()) {
                        ScoreboardUtils.changeValue("lives", -1, player);
                        if (ScoreboardUtils.getValue("lives", player) <= 0) {
                            player.setGameMode(SPECTATOR);
                            player.sendSystemMessage(MessageUtils.getMessage(OUT_OF_LIVES));
                        } else {
                            player.sendSystemMessage(MessageUtils.getMessage(LIVES_REMAINING, ScoreboardUtils.getValue("lives", player)));
                        }
                    }
                    if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
                        OriginUtils.randomOrigin(Reason.DEATH, player);
                    }
                }
            } else {
                player.sendSystemMessage(MessageUtils.getMessage(RANDOMISER_DISABLED));
            }
        }
    }
}