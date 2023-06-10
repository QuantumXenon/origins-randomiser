package quantumxenon.origins_randomiser.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quantumxenon.origins_randomiser.OriginsRandomiser;
import quantumxenon.origins_randomiser.command.LivesCommand;
import quantumxenon.origins_randomiser.command.RandomiseCommand;
import quantumxenon.origins_randomiser.command.ToggleCommand;
import quantumxenon.origins_randomiser.command.UsesCommand;
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.enums.Tag;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

@Mod.EventBusSubscriber(modid = OriginsRandomiser.MOD_ID)
public class OriginsRandomiserEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new LivesCommand(event.getDispatcher());
        new RandomiseCommand(event.getDispatcher());
        new ToggleCommand(event.getDispatcher());
        new UsesCommand(event.getDispatcher());
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
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }
}
