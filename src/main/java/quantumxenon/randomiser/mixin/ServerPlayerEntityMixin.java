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
import quantumxenon.randomiser.enums.Objective;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.enums.Tag;
import quantumxenon.randomiser.utils.ConfigUtils;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private final ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile, null);
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag(Tag.FIRST_JOIN, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(Tag.FIRST_JOIN));
            ScoreboardUtils.createObjective(Objective.LIVES_UNTIL_RANDOMISE, ConfigUtils.livesBetweenRandomises(), player);
            ScoreboardUtils.createObjective(Objective.SLEEPS_UNTIL_RANDOMISE, ConfigUtils.sleepsBetweenRandomises(), player);
            ScoreboardUtils.createObjective(Objective.USES, ConfigUtils.randomiseCommandUses(), player);
            ScoreboardUtils.createObjective(Objective.LIVES, ConfigUtils.startingLives(), player);
            if (ConfigUtils.randomiseOrigins()) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
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
        if (ConfigUtils.enableLives() && ScoreboardUtils.noScoreboardTag(Tag.LIVES_ENABLED_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(Tag.LIVES_ENABLED_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(Message.LIVES_ENABLED, ConfigUtils.startingLives()));
        }
        if (ConfigUtils.limitCommandUses() && ScoreboardUtils.noScoreboardTag(Tag.LIMIT_USES_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(Tag.LIMIT_USES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(Message.LIMIT_COMMAND_USES, ConfigUtils.randomiseCommandUses()));
        }
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(Tag.LIVES_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(Tag.LIVES_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_LIVES, ConfigUtils.livesBetweenRandomises()));
        }
        if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag(Tag.SLEEPS_MESSAGE, player)) {
            player.addScoreboardTag(ScoreboardUtils.tagName(Tag.SLEEPS_MESSAGE));
            player.sendMessage(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()));
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.deathRandomisesOrigin()) {
                ScoreboardUtils.decrementValue(Objective.LIVES_UNTIL_RANDOMISE, player);
                if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player)));
                }
                if (ConfigUtils.enableLives()) {
                    ScoreboardUtils.decrementValue(Objective.LIVES, player);
                    if (ScoreboardUtils.getValue(Objective.LIVES, player) <= 0) {
                        player.changeGameMode(GameMode.SPECTATOR);
                        player.sendMessage(MessageUtils.getMessage(Message.OUT_OF_LIVES));
                    } else {
                        player.sendMessage(MessageUtils.getMessage(Message.LIVES_REMAINING, ScoreboardUtils.getValue(Objective.LIVES, player)));
                    }
                }
                if (ScoreboardUtils.getValue(Objective.LIVES_UNTIL_RANDOMISE, player) <= 0) {
                    OriginUtils.randomOrigin(Reason.DEATH, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(Message.DISABLED));
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.sleepRandomisesOrigin()) {
                ScoreboardUtils.decrementValue(Objective.SLEEPS_UNTIL_RANDOMISE, player);
                if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(Message.SLEEPS_UNTIL_RANDOMISE, ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player)));
                }
                if (ScoreboardUtils.getValue(Objective.SLEEPS_UNTIL_RANDOMISE, player) <= 0) {
                    OriginUtils.randomOrigin(Reason.SLEEP, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(Message.DISABLED));
        }
    }
}