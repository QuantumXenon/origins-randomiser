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
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;
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
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag(FIRST_JOIN, player)) {
            player.addCommandTag(ScoreboardUtils.tagName(FIRST_JOIN));
            ScoreboardUtils.createObjective(LIVES_UNTIL_RANDOMISE, config.lives.livesBetweenRandomises, player);
            ScoreboardUtils.createObjective(SLEEPS_UNTIL_RANDOMISE, config.other.sleepsBetweenRandomises, player);
            ScoreboardUtils.createObjective(USES, config.command.randomiseCommandUses, player);
            ScoreboardUtils.createObjective(LIVES, config.lives.startingLives, player);
            if (config.general.randomiseOrigins) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(LIVES_UNTIL_RANDOMISE, config.lives.livesBetweenRandomises, player);
        }
        if (ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
            ScoreboardUtils.setValue(SLEEPS_UNTIL_RANDOMISE, config.other.sleepsBetweenRandomises, player);
        }
        if (config.lives.enableLives && ScoreboardUtils.noScoreboardTag(LIVES_ENABLED_MESSAGE, player)) {
            player.addCommandTag(ScoreboardUtils.tagName(LIVES_ENABLED_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(LIVES_ENABLED, config.lives.startingLives), false);
        }
        if (config.command.limitCommandUses && ScoreboardUtils.noScoreboardTag(LIMIT_USES_MESSAGE, player)) {
            player.addCommandTag(ScoreboardUtils.tagName(LIMIT_USES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(LIMIT_COMMAND_USES, config.command.randomiseCommandUses), false);
        }
        if (config.lives.livesBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag(LIVES_MESSAGE, player)) {
            player.addCommandTag(ScoreboardUtils.tagName(LIVES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_LIVES, config.lives.livesBetweenRandomises), false);
        }
        if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag(SLEEPS_MESSAGE, player)) {
            player.addCommandTag(ScoreboardUtils.tagName(SLEEPS_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_SLEEPS, config.other.sleepsBetweenRandomises), false);
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        if (config.general.randomiseOrigins) {
            if (config.other.deathRandomisesOrigin) {
                ScoreboardUtils.decrementValue(LIVES_UNTIL_RANDOMISE, player);
                if (config.lives.livesBetweenRandomises > 1 && ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue(LIVES_UNTIL_RANDOMISE, player)), false);
                }
                if (config.lives.enableLives) {
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
        if (config.general.randomiseOrigins) {
            if (config.other.sleepRandomisesOrigin) {
                ScoreboardUtils.decrementValue(SLEEPS_UNTIL_RANDOMISE, player);
                if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.getValue(SLEEPS_UNTIL_RANDOMISE, player) > 0) {
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