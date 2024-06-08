package quantumxenon.originsrandomiser.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Reason;
import quantumxenon.originsrandomiser.util.OriginsRandomiserPlayer;

import static quantumxenon.originsrandomiser.enums.Message.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(((ServerPlayerEntity) (Object) this));

    private ServerPlayerEntityMixin() {
        super();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (player.getObjectiveValue("livesUntilRandomise") <= 0) {
            player.setObjectiveValue("livesUntilRandomise", config.lives.livesBetweenRandomises);
        }
        if (player.getObjectiveValue("sleepsUntilRandomise") <= 0) {
            player.setObjectiveValue("sleepsUntilRandomise", config.sleep.sleepsBetweenRandomises);
        }
        if (config.lives.enableLives && !player.hasScoreboardTag("livesEnabledMessage")) {
            player.addScoreboardTag("livesEnabledMessage");
            player.getAndSendMessage(LIVES_ENABLED, config.lives.startingLives);
        }
        if (config.command.limitCommandUses && !player.hasScoreboardTag("limitUsesMessage")) {
            player.addScoreboardTag("limitUsesMessage");
            player.getAndSendMessage(LIMIT_COMMAND_USES, config.command.randomiseCommandUses);
        }
        if (config.lives.livesBetweenRandomises > 1 && !player.hasScoreboardTag("livesMessage")) {
            player.addScoreboardTag("livesMessage");
            player.getAndSendMessage(RANDOM_ORIGIN_AFTER_LIVES, config.lives.livesBetweenRandomises);
        }
        if (config.sleep.sleepsBetweenRandomises > 1 && !player.hasScoreboardTag("sleepsMessage")) {
            player.addScoreboardTag("sleepsMessage");
            player.getAndSendMessage(RANDOM_ORIGIN_AFTER_SLEEPS, config.sleep.sleepsBetweenRandomises);
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        if (config.general.randomiseOrigins) {
            if (config.advanced.deathRandomisesOrigin) {
                player.changeObjectiveValue("livesUntilRandomise", -1);
                if (config.lives.livesBetweenRandomises > 1 && player.getObjectiveValue("livesUntilRandomise") > 0) {
                    player.getAndSendMessage(LIVES_UNTIL_NEXT_RANDOMISE, player.getObjectiveValue("livesUntilRandomise"));
                }
                if (config.lives.enableLives) {
                    player.changeObjectiveValue("lives", -1);
                    if (player.getObjectiveValue("lives") <= 0) {
                        player.setGameMode(GameMode.SPECTATOR);
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
            player.addScoreboardTag("showOriginsScreen");
        }
    }

    @Inject(at = @At("HEAD"), method = "wakeUp")
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