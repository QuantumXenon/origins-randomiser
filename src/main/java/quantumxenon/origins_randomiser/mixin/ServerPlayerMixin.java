package quantumxenon.origins_randomiser.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.origins_randomiser.enums.Message;
import quantumxenon.origins_randomiser.enums.Objective;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.enums.Tag;
import quantumxenon.origins_randomiser.utils.ConfigUtils;
import quantumxenon.origins_randomiser.utils.MessageUtils;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.ScoreboardUtils;

import static net.minecraft.world.level.GameType.SPECTATOR;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    private final ServerPlayer player = ((ServerPlayer) (Object) this);

    @Inject(at = @At("TAIL"), method = "tick")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag(Tag.FIRST_JOIN, player)) {
            player.addTag(ScoreboardUtils.tagName(Tag.FIRST_JOIN));
            ScoreboardUtils.createObjective(Objective.LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
            ScoreboardUtils.createObjective(Objective.SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
            ScoreboardUtils.createObjective(Objective.USES, ConfigUtils.randomiseCommandUses(), player);
            ScoreboardUtils.createObjective(Objective.LIVES, ConfigUtils.startingLives(), player);
            OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(Objective.LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
        }
        if (ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(Objective.SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
        }
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(Tag.LIVES_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(Tag.LIVES_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_LIVES, ConfigUtils.livesBetweenRandomises()));
        }
        if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(Tag.SLEEPS_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(Tag.SLEEPS_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()));
        }
        if (ConfigUtils.limitCommandUses() && ScoreboardUtils.noScoreboardTag(Tag.LIMIT_USES_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(Tag.LIMIT_USES_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(Message.LIMIT_COMMAND_USES, ConfigUtils.randomiseCommandUses()));
        }
        if (ConfigUtils.enableLives() && ScoreboardUtils.noScoreboardTag(Tag.LIVES_ENABLED_MESSAGE, player)) {
            player.addTag(ScoreboardUtils.tagName(Tag.LIVES_ENABLED_MESSAGE));
            player.sendSystemMessage(MessageUtils.getMessage(Message.LIVES_ENABLED, ConfigUtils.startingLives()));
        }
    }

    @Inject(at = @At("TAIL"), method = "die")
    private void death(CallbackInfo info) {
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
    }

    @Inject(at = @At("TAIL"), method = "stopSleepInBed")
    private void sleep(CallbackInfo info) {
        if (ConfigUtils.sleepRandomisesOrigin()) {
            ScoreboardUtils.decrementValue(Objective.SLEEPS_UNTIL_RANDOMISE, player);
            if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player) > 0) {
                player.sendSystemMessage(MessageUtils.getMessage(Message.SLEEPS_UNTIL_RANDOMISE, ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player)));
            }
            if (ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
                OriginUtils.randomOrigin(Reason.SLEEP, player);
            }
        }
    }
}