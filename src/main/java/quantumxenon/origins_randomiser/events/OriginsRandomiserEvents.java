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
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.enums.Tag;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.world.level.GameType.SPECTATOR;

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
            if (ScoreboardUtils.noScoreboardTag(Tag.FIRST_JOIN, player)) {
                player.addTag(ScoreboardUtils.tagName(Tag.FIRST_JOIN));
                ScoreboardUtils.createObjective(Objective.LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
                ScoreboardUtils.createObjective(Objective.SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
                ScoreboardUtils.createObjective(Objective.USES, ConfigUtils.randomiseCommandUses(), player);
                ScoreboardUtils.createObjective(Objective.LIVES, ConfigUtils.startingLives(), player);
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
                    ScoreboardUtils.decrementValue(Objective.LIVES_UNTIL_RANDOMISE, player);
                    if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player) > 0) {
                        player.sendSystemMessage(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player)));
                    }
                    if (ConfigUtils.enableLives()) {
                        ScoreboardUtils.decrementValue(Objective.LIVES, player);
                        if (ScoreboardUtils.getValue(Objective.LIVES, player) <= 0) {
                            player.setGameMode(SPECTATOR);
                            player.sendSystemMessage(MessageUtils.getMessage(Message.OUT_OF_LIVES));
                        } else {
                            player.sendSystemMessage(MessageUtils.getMessage(Message.LIVES_REMAINING, ScoreboardUtils.getValue(Objective.LIVES, player)));
                        }
                    }
                    if (ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player) <= 0) {
                        OriginUtils.randomOrigin(Reason.DEATH, player);
                    }
                }
            } else {
                player.sendSystemMessage(MessageUtils.getMessage(Message.DISABLED));
            }
        }
    }
}
