package quantumxenon.origins_randomiser.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.util.OriginsRandomiserPlayer;

import static quantumxenon.origins_randomiser.enums.Message.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(((ServerPlayer) (Object) this));

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (player.getObjectiveValue("livesUntilRandomise") <= 0) {
            player.setObjectiveValue("livesUntilRandomise", config.lives.livesBetweenRandomises);
        }
        if (player.getObjectiveValue("sleepsUntilRandomise") <= 0) {
            player.setObjectiveValue("sleepsUntilRandomise", config.sleep.sleepsBetweenRandomises);
        }
        if (config.command.limitCommandUses && !player.hasScoreboardTag("limitCommandUsesMessage")) {
            player.addScoreboardTag("limitCommandUsesMessage");
            player.getAndSendMessage(LIMIT_COMMAND_USES, config.command.randomiseCommandUses);
        }
        if (config.lives.enableLives && !player.hasScoreboardTag("enableLivesMessage")) {
            player.addScoreboardTag("enableLivesMessage");
            player.getAndSendMessage(LIVES_ENABLED, config.lives.startingLives);
        }
        if (config.lives.livesBetweenRandomises > 1 && !player.hasScoreboardTag("livesBetweenRandomisesMessage")) {
            player.addScoreboardTag("livesBetweenRandomisesMessage");
            player.getAndSendMessage(RANDOM_ORIGIN_AFTER_LIVES, config.lives.livesBetweenRandomises);
        }
        if (config.sleep.sleepsBetweenRandomises > 1 && !player.hasScoreboardTag("sleepsBetweenRandomisesMessage")) {
            player.addScoreboardTag("sleepsBetweenRandomisesMessage");
            player.getAndSendMessage(RANDOM_ORIGIN_AFTER_SLEEPS, config.sleep.sleepsBetweenRandomises);
        }
    }

    @Inject(at = @At("HEAD"), method = "stopSleepInBed")
    private void sleep(CallbackInfo info) {
        if (config.sleep.sleepRandomisesOrigin && player.hasSleptThroughNight()) {
            player.changeObjectiveValue("sleepsUntilRandomise", -1);
            if (config.sleep.sleepsBetweenRandomises > 1 && player.getObjectiveValue("sleepsUntilRandomise") > 0) {
                player.getAndSendMessage(SLEEPS_UNTIL_NEXT_RANDOMISE, player.getObjectiveValue("sleepsUntilRandomise"));
            }
            if (player.getObjectiveValue("sleepsUntilRandomise") <= 0) {
                player.randomiseOrigin(Reason.SLEEP);
            }
        }
    }
}