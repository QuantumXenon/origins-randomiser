package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.entity.Player;

import java.util.List;
import java.util.Objects;

import static net.minecraft.scoreboard.ScoreboardCriterion.RenderType.INTEGER;
import static quantumxenon.randomiser.OriginsRandomiser.CONFIG;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Player {
    private static final OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
    private final String player = this.getName().getString();
    private final MinecraftServer server = Objects.requireNonNull(this.getServer());
    private final Scoreboard scoreboard = this.getScoreboard();

    private ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    private void send(String message, PlayerEntity player) {
        player.sendMessage(Text.of(message));
    }

    private String translate(String key) {
        return Text.translatable(key).getString();
    }

    private int getValue(String objective) {
        return (scoreboard.getPlayerScore(player, scoreboard.getObjective(objective)).getScore());
    }

    public void modifyValue(String objective, int number, PlayerEntity target) {
        target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective(objective)).incrementScore(number);
    }

    public void setValue(String objective, int number, PlayerEntity target) {
        target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective(objective)).setScore(number);
    }

    public void randomOrigin(String reason, boolean bypass) {
        if ((getValue("livesUntilRandomise") <= 0) || bypass) {
            if (CONFIG.randomiseOrigins()) {
                setValue("livesUntilRandomise", CONFIG.livesBetweenRandomises(), this);
                List<Identifier> originList = layer.getRandomOrigins(this);
                Origin origin = OriginRegistry.get(originList.get(this.getRandom().nextInt(originList.size())));
                setOrigin(this, origin);
                if (CONFIG.randomiserMessages()) {
                    for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
                        send(Formatting.BOLD + player + Formatting.RESET + " " + reason + " " + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET, serverPlayer);
                    }
                }
            } else {
                send(translate("origins-randomiser.origin.disabled"), this);
            }
        } else {
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("livesUntilRandomise") + Formatting.RESET + " " + translate("origins-randomiser.message.livesUntilRandomise"), this);
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].replace("_", " ").split("\\s+")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    private void setOrigin(PlayerEntity player, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (CONFIG.sleepRandomisesOrigin()) {
            randomOrigin(translate("origins-randomiser.reason.sleep"), true);
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        modifyValue("livesUntilRandomise", -1, this);
        randomOrigin(translate("origins-randomiser.reason.death"), false);
        if (CONFIG.enableLives()) {
            modifyValue("lives", -1, this);
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("lives") + Formatting.RESET + " " + translate("origins-randomiser.message.livesRemaining"), this);
        }
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        String tag = "firstJoin";
        if (!this.getScoreboardTags().contains(tag)) {
            this.addScoreboardTag(tag);
            randomOrigin(translate("origins-randomiser.reason.firstJoin"), true);
        }

        String objective = "livesUntilRandomise";
        if (!scoreboard.containsObjective(objective)) {
            scoreboard.addObjective(objective, ScoreboardCriterion.DUMMY, Text.of(objective), INTEGER);
            setValue(objective, CONFIG.livesBetweenRandomises(), this);
            if (CONFIG.livesBetweenRandomises() > 1) {
                send(translate("origins-randomiser.message.livesBetweenRandomise") + " " + Formatting.BOLD + CONFIG.livesBetweenRandomises() + Formatting.RESET + " " + translate("origins-randomiser.message.lives"), this);
            }
        }

        if (CONFIG.limitCommandUses()) {
            String uses = "uses";
            if (!scoreboard.containsObjective(uses)) {
                scoreboard.addObjective(uses, ScoreboardCriterion.DUMMY, Text.of(uses), INTEGER);
                setValue(uses, CONFIG.randomiseCommandUses(), this);
                send(translate("origins-randomiser.message.commandLimited") + " " + Formatting.BOLD + CONFIG.randomiseCommandUses() + Formatting.RESET + " " + translate("origins-randomiser.message.uses"), this);
            }
        }

        if (CONFIG.enableLives()) {
            String lives = "lives";
            if (!scoreboard.containsObjective(lives)) {
                scoreboard.addObjective(lives, ScoreboardCriterion.DUMMY, Text.of(lives), INTEGER);
                setValue(lives, CONFIG.startingLives(), this);
                send(translate("origins-randomiser.message.livesEnabled") + " " + CONFIG.startingLives() + " " + translate("origins-randomiser.message.lives"), this);
            }
            if (getValue(lives) == 0) {
                this.changeGameMode(GameMode.SPECTATOR);
                send(translate("origins-randomiser.message.outOfLives"), this);
            }
        }
    }
}