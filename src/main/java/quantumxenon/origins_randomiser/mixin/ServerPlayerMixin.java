package quantumxenon.origins_randomiser.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.origins_randomiser.config.OriginsRandomiserConfig;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static quantumxenon.origins_randomiser.enums.Message.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    private final ServerPlayer player = ((ServerPlayer) (Object) this);
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("livesUntilRandomise", config.lives.livesBetweenRandomises, player);
        }
        if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("sleepsUntilRandomise", config.other.sleepsBetweenRandomises, player);
        }
        if (config.lives.enableLives && ScoreboardUtils.noScoreboardTag("livesEnabledMessage", player)) {
            player.addTag("livesEnabledMessage");
            player.sendSystemMessage(MessageUtils.getMessage(LIVES_ENABLED, config.lives.startingLives));
        }
        if (config.command.limitCommandUses && ScoreboardUtils.noScoreboardTag("limitUsesMessage", player)) {
            player.addTag("limitUsesMessage");
            player.sendSystemMessage(MessageUtils.getMessage(LIMIT_COMMAND_USES, config.command.randomiseCommandUses));
        }
        if (config.lives.livesBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag("livesMessage", player)) {
            player.addTag("livesMessage");
            player.sendSystemMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_LIVES, config.lives.livesBetweenRandomises));
        }
        if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag("sleepsMessage", player)) {
            player.addTag("sleepsMessage");
            player.sendSystemMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_SLEEPS, config.other.sleepsBetweenRandomises));
        }
    }

    @Inject(at = @At("HEAD"), method = "stopSleepInBed")
    private void sleep(CallbackInfo info) {
        if (config.general.randomiseOrigins) {
            if (config.other.sleepRandomisesOrigin && player.isSleepingLongEnough()) {
                ScoreboardUtils.changeValue("sleepsUntilRandomise", -1, player);
                if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.getValue("sleepsUntilRandomise", player) > 0) {
                    player.sendSystemMessage(MessageUtils.getMessage(SLEEPS_UNTIL_NEXT_RANDOMISE, ScoreboardUtils.getValue("sleepsUntilRandomise", player)));
                }
                if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
                    OriginUtils.randomOrigin(Reason.SLEEP, player);
                }
            }
        } else {
            player.sendSystemMessage(MessageUtils.getMessage(RANDOMISER_DISABLED));
        }
    }
}