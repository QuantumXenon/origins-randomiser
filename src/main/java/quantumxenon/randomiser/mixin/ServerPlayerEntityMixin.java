package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
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
import quantumxenon.randomiser.utils.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this) ;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (ScoreboardUtils.noScoreboardTag("firstJoin", player)) {
            player.addCommandTag("firstJoin");
            ScoreboardUtils.createObjective("livesUntilRandomise", ConfigUtils.livesBetweenRandomises(), player);
            ScoreboardUtils.createObjective("sleepsUntilRandomise", ConfigUtils.sleepsBetweenRandomises(), player);
            ScoreboardUtils.createObjective("uses", ConfigUtils.randomiseCommandUses(), player);
            ScoreboardUtils.createObjective("lives", ConfigUtils.startingLives(), player);
            OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("livesUntilRandomise", ConfigUtils.livesBetweenRandomises(), player);
        }
        if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
            ScoreboardUtils.setValue("sleepsUntilRandomise", ConfigUtils.sleepsBetweenRandomises(), player);
        }
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag("livesMessage", player)) {
            player.addCommandTag("livesMessage");
            PlayerUtils.send(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()), player.getCommandSource());
        }
        if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.noScoreboardTag("sleepsMessage", player)) {
            player.addCommandTag("sleepsMessage");
            PlayerUtils.send(MessageUtils.getMessage(Message.RANDOM_ORIGIN_AFTER_SLEEPS, ConfigUtils.sleepsBetweenRandomises()), player.getCommandSource());
        }
        if (ConfigUtils.limitCommandUses() && ScoreboardUtils.noScoreboardTag("limitUsesMessage", player)) {
            player.addCommandTag("limitUsesMessage");
            PlayerUtils.send(MessageUtils.getMessage(Message.LIMIT_COMMAND_USES, ConfigUtils.randomiseCommandUses()), player.getCommandSource());
        }
        if (ConfigUtils.enableLives() && ScoreboardUtils.noScoreboardTag("livesEnabledMessage", player)) {
            player.addCommandTag("livesEnabledMessage");
            PlayerUtils.send(MessageUtils.getMessage(Message.LIVES_ENABLED, ConfigUtils.startingLives()), player.getCommandSource());
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        ScoreboardUtils.decrementValue("livesUntilRandomise", player);
        if (ConfigUtils.livesBetweenRandomises() > 1 && ScoreboardUtils.getValue("livesUntilRandomise", player) > 0) {
            PlayerUtils.send(MessageUtils.getMessage(Message.LIVES_UNTIL_RANDOMISE, ScoreboardUtils.getValue("livesUntilRandomise", player)), player.getCommandSource());
        }
        if (ConfigUtils.enableLives()) {
            ScoreboardUtils.decrementValue("lives", player);
            if (ScoreboardUtils.getValue("lives", player) <= 0) {
                player.changeGameMode(GameMode.SPECTATOR);
                PlayerUtils.send(MessageUtils.getMessage(Message.OUT_OF_LIVES), player.getCommandSource());
            } else {
                PlayerUtils.send(MessageUtils.getMessage(Message.LIVES_REMAINING, ScoreboardUtils.getValue("lives", player)), player.getCommandSource());
            }
        }
        if (ScoreboardUtils.getValue("livesUntilRandomise", player) <= 0) {
            OriginUtils.randomOrigin(Reason.DEATH, player);
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (ConfigUtils.sleepRandomisesOrigin()) {
            ScoreboardUtils.decrementValue("sleepsUntilRandomise", player);
            if (ConfigUtils.sleepsBetweenRandomises() > 1 && ScoreboardUtils.getValue("sleepsUntilRandomise", player) > 0) {
                PlayerUtils.send(MessageUtils.getMessage(Message.SLEEPS_UNTIL_RANDOMISE, ScoreboardUtils.getValue("sleepsUntilRandomise", player)), player.getCommandSource());
            }
            if (ScoreboardUtils.getValue("sleepsUntilRandomise", player) <= 0) {
                OriginUtils.randomOrigin(Reason.SLEEP, player);
            }
        }
    }
}