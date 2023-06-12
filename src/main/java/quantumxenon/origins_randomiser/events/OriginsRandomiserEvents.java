package quantumxenon.origins_randomiser.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.OriginsRandomiser;
import quantumxenon.origins_randomiser.command.RandomiseCommand;
import quantumxenon.origins_randomiser.command.SetCommand;
import quantumxenon.origins_randomiser.command.ToggleCommand;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.world.level.GameType.SPECTATOR;
import static quantumxenon.origins_randomiser.enums.Message.*;
import static quantumxenon.origins_randomiser.enums.Objective.LIVES_UNTIL_RANDOMISE;
import static quantumxenon.origins_randomiser.enums.Objective.SLEEPS_UNTIL_RANDOMISE;
import static quantumxenon.origins_randomiser.enums.Objective.*;
import static quantumxenon.origins_randomiser.enums.Tag.FIRST_JOIN;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.MOD_ID)
public class OriginsRandomiserEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new RandomiseCommand(event.getDispatcher());
        new SetCommand(event.getDispatcher());
        new ToggleCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (ScoreboardUtils.noScoreboardTag(FIRST_JOIN, player)) {
                player.addTag(ScoreboardUtils.tagName(FIRST_JOIN));
                ScoreboardUtils.createObjective(LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
                ScoreboardUtils.createObjective(SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
                ScoreboardUtils.createObjective(USES, ConfigUtils.randomiseCommandUses(), player);
                ScoreboardUtils.createObjective(LIVES, ConfigUtils.startingLives(), player);
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
                    ScoreboardUtils.decrementValue(LIVES_UNTIL_RANDOMISE, player);
                    if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) > 0) {
                        player.sendSystemMessage(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player)));
                    }
                    if (ConfigUtils.enableLives()) {
                        ScoreboardUtils.decrementValue(LIVES, player);
                        if (ScoreboardUtils.getValue(LIVES, player) <= 0) {
                            player.setGameMode(SPECTATOR);
                            player.sendSystemMessage(MessageUtils.getMessage(OUT_OF_LIVES));
                        } else
                            player.sendSystemMessage(MessageUtils.getMessage(LIVES_REMAINING, ScoreboardUtils.getValue(LIVES, player)));
                    }
                    if (ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) <= 0) {
                        OriginUtils.randomOrigin(Reason.DEATH, player);
                    }
                }
            } else {
                player.sendSystemMessage(MessageUtils.getMessage(DISABLED));
            }
        }
    }
}
