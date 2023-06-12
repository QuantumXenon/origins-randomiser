package quantumxenon.origins_randomiser.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static quantumxenon.origins_randomiser.enums.Message.*;
import static quantumxenon.origins_randomiser.enums.Objective.LIVES_UNTIL_RANDOMISE;
import static quantumxenon.origins_randomiser.enums.Objective.SLEEPS_UNTIL_RANDOMISE;
import static quantumxenon.origins_randomiser.enums.Tag.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    private final ServerPlayer player = ((ServerPlayer) (Object) this);

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
        }
        if (ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
        }
        if (ConfigUtils.enableLives() && ScoreboardUtils.noScoreboardTag(LIVES_ENABLED_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(LIVES_ENABLED_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(LIVES_ENABLED, ConfigUtils.startingLives()));
        }
        if (ConfigUtils.limitCommandUses() && ScoreboardUtils.noScoreboardTag(LIMIT_USES_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(LIMIT_USES_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(LIMIT_COMMAND_USES, ConfigUtils.randomiseCommandUses()));
        }
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(LIVES_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(LIVES_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_LIVES, ConfigUtils.livesBetweenRandomises()));
        }
        if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(SLEEPS_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(SLEEPS_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()));
        }
    }

    @Inject(at = @At("TAIL"), method = "stopSleepInBed")
    private void sleep(CallbackInfo info) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.sleepRandomisesOrigin()) {
                ScoreboardUtils.decrementValue(SLEEPS_UNTIL_RANDOMISE, player);
                if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) > 0) {
                    player.sendSystemMessage(MessageUtils.getMessage(Message.SLEEPS_UNTIL_RANDOMISE, ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player)));
                }
                if (ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
                    OriginUtils.randomOrigin(Reason.SLEEP, player);
                }
            }
        } else {
            player.sendSystemMessage(MessageUtils.getMessage(DISABLED));
        }
    }
}