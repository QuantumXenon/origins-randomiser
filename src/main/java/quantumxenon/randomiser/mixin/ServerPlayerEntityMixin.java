package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import static quantumxenon.randomiser.enums.Message.*;
import static quantumxenon.randomiser.enums.Objective.LIVES_UNTIL_RANDOMISE;
import static quantumxenon.randomiser.enums.Objective.SLEEPS_UNTIL_RANDOMISE;
import static quantumxenon.randomiser.enums.Objective.*;
import static quantumxenon.randomiser.enums.Tag.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private final ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag(FIRST_JOIN, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(FIRST_JOIN));
            ScoreboardUtils.createObjective(LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
            ScoreboardUtils.createObjective(SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
            ScoreboardUtils.createObjective(USES, ConfigUtils.randomiseCommandUses(), player);
            ScoreboardUtils.createObjective(LIVES, ConfigUtils.startingLives(), player);
            if (ConfigUtils.randomiseOrigins()) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
        }
        if (ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
        }
        if (ConfigUtils.enableLives() && ScoreboardUtils.noScoreboardTag(LIVES_ENABLED_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(LIVES_ENABLED_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(LIVES_ENABLED, ConfigUtils.startingLives()), false);
        }
        if (ConfigUtils.limitCommandUses() && ScoreboardUtils.noScoreboardTag(LIMIT_USES_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(LIMIT_USES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(LIMIT_COMMAND_USES, ConfigUtils.randomiseCommandUses()), false);
        }
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(LIVES_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(LIVES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_LIVES, ConfigUtils.livesBetweenRandomises()), false);
        }
        if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(SLEEPS_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(SLEEPS_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()), false);
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.deathRandomisesOrigin()) {
                ScoreboardUtils.decrementValue(LIVES_UNTIL_RANDOMISE, player);
                if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player)), false);
                }
                if (ConfigUtils.enableLives()) {
                    ScoreboardUtils.decrementValue(LIVES, player);
                    if (ScoreboardUtils.getValue(LIVES, player) <= 0) {
                        player.changeGameMode(GameMode.SPECTATOR);
                        player.sendMessage(MessageUtils.getMessage(OUT_OF_LIVES), false);
                    } else {
                        player.sendMessage(MessageUtils.getMessage(LIVES_REMAINING, ScoreboardUtils.getValue(LIVES, player)), false);
                    }
                }
                if (ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) <= 0) {
                    OriginUtils.randomOrigin(Reason.DEATH, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(DISABLED), false);
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.sleepRandomisesOrigin()) {
                ScoreboardUtils.decrementValue(SLEEPS_UNTIL_RANDOMISE, player);
                if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(Message.SLEEPS_UNTIL_RANDOMISE, ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player)), false);
                }
                if (ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
                    OriginUtils.randomOrigin(Reason.SLEEP, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(DISABLED), false);
        }
    }
}