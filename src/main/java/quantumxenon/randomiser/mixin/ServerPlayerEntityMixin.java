package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.MessageUtils;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import static net.minecraft.world.GameMode.SPECTATOR;
import static quantumxenon.randomiser.enums.Message.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private final ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile, null);
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag("firstJoin", player)) {
            player.addScoreboardTag("firstJoin");
            ScoreboardUtils.createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises, player);
            ScoreboardUtils.createObjective("sleepsUntilRandomise", config.other.sleepsBetweenRandomises, player);
            ScoreboardUtils.createObjective("uses", config.command.randomiseCommandUses, player);
            ScoreboardUtils.createObjective("lives", config.lives.startingLives, player);
            if (config.general.randomiseOrigins) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("livesUntilRandomise", config.lives.livesBetweenRandomises, player);
        }
        if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("sleepsUntilRandomise", config.other.sleepsBetweenRandomises, player);
        }
        if (config.lives.enableLives && ScoreboardUtils.noScoreboardTag("livesEnabledMessage", player)) {
            player.addScoreboardTag("livesEnabledMessage");
            player.sendMessage(MessageUtils.getMessage(LIVES_ENABLED, config.lives.startingLives), false);
        }
        if (config.command.limitCommandUses && ScoreboardUtils.noScoreboardTag("limitUsesMessage", player)) {
            player.addScoreboardTag("limitUsesMessage");
            player.sendMessage(MessageUtils.getMessage(LIMIT_COMMAND_USES, config.command.randomiseCommandUses), false);
        }
        if (config.lives.livesBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag("livesMessage", player)) {
            player.addScoreboardTag("livesMessage");
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_LIVES, config.lives.livesBetweenRandomises), false);
        }
        if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.noScoreboardTag("sleepsMessage", player)) {
            player.addScoreboardTag("sleepsMessage");
            player.sendMessage(MessageUtils.getMessage(RANDOM_ORIGIN_AFTER_SLEEPS, config.other.sleepsBetweenRandomises), false);
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        if (config.general.randomiseOrigins) {
            if (config.other.deathRandomisesOrigin) {
                ScoreboardUtils.changeValue("livesUntilRandomise", -1, player);
                if (config.lives.livesBetweenRandomises > 1 && ScoreboardUtils.getValue("livesUntilRandomise", player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(LIVES_UNTIL_NEXT_RANDOMISE, ScoreboardUtils.getValue("livesUntilRandomise", player)), false);
                }
                if (config.lives.enableLives) {
                    ScoreboardUtils.changeValue("lives", -1, player);
                    if (ScoreboardUtils.getValue("lives", player) <= 0) {
                        player.changeGameMode(SPECTATOR);
                        player.sendMessage(MessageUtils.getMessage(OUT_OF_LIVES), false);
                    } else {
                        player.sendMessage(MessageUtils.getMessage(LIVES_REMAINING, ScoreboardUtils.getValue("lives", player)), false);
                    }
                }
                if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
                    OriginUtils.randomOrigin(Reason.DEATH, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(RANDOMISER_DISABLED), false);
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (config.general.randomiseOrigins) {
            if (config.other.sleepRandomisesOrigin) {
                ScoreboardUtils.changeValue("sleepsUntilRandomise", -1, player);
                if (config.other.sleepsBetweenRandomises > 1 && ScoreboardUtils.getValue("sleepsUntilRandomise", player) > 0) {
                    player.sendMessage(MessageUtils.getMessage(SLEEPS_UNTIL_NEXT_RANDOMISE, ScoreboardUtils.getValue("sleepsUntilRandomise", player)), false);
                }
                if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
                    OriginUtils.randomOrigin(Reason.SLEEP, player);
                }
            }
        } else {
            player.sendMessage(MessageUtils.getMessage(RANDOMISER_DISABLED), false);
        }
    }
}