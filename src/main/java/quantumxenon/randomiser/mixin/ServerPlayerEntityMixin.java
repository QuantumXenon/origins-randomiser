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
    private final Scoreboard scoreboard = this.getScoreboard();

    private ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    private void send(String message) {
        this.sendMessage(Text.of(message));
    }

    private String translate(String key) {
        return Text.translatable(key).getString();
    }

    private int getValue(String objective) {
        return (scoreboard.getPlayerScore(player, scoreboard.getObjective(objective)).getScore());
    }

    public void modifyValue(String objective, int value) {
        this.getScoreboard().getPlayerScore(player, this.getScoreboard().getObjective(objective)).incrementScore(value);
    }

    public void setValue(String objective, int value) {
        this.getScoreboard().getPlayerScore(player, this.getScoreboard().getObjective(objective)).setScore(value);
    }

    private void createObjective(String name, int number) {
        if (!scoreboard.containsObjective(name)) {
            scoreboard.addObjective(name, ScoreboardCriterion.DUMMY, Text.of(name), INTEGER);
            setValue(name, number);
            if (name.equals("uses")) {
                send(translate("origins-randomiser.message.commandLimited") + " " + Formatting.BOLD + CONFIG.randomiseCommandUses() + Formatting.RESET + " " + translate("origins-randomiser.message.uses"));
            }
            if (name.equals("lives")) {
                send(translate("origins-randomiser.message.livesEnabled") + " " + Formatting.BOLD + CONFIG.startingLives() + Formatting.RESET + " " + translate("origins-randomiser.message.lives"));
            }
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    private void setOrigin(Origin origin) {
        ModComponents.ORIGIN.get(this).setOrigin(layer, origin);
        OriginComponent.sync(this);
    }

    public void randomOrigin(String reason) {
        if (CONFIG.randomiseOrigins()) {
            if (getValue("livesUntilRandomise") <= 0) {
                setValue("livesUntilRandomise", CONFIG.livesBetweenRandomises());
            }
            if (getValue("sleepsUntilRandomise") <= 0) {
                setValue("sleepsUntilRandomise", CONFIG.sleepsBetweenRandomises());
            }
            List<Identifier> originList = layer.getRandomOrigins(this);
            Origin origin = OriginRegistry.get(originList.get(this.getRandom().nextInt(originList.size())));
            setOrigin(origin);
            if (CONFIG.randomiserMessages()) {
                for (ServerPlayerEntity entity : Objects.requireNonNull(this.getServer()).getPlayerManager().getPlayerList()) {
                    entity.sendMessage(Text.of(Formatting.BOLD + player + Formatting.RESET + " " + reason + " " + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET));
                }
            }
        } else {
            send(translate("origins-randomiser.origin.disabled"));
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (CONFIG.sleepRandomisesOrigin()) {
            modifyValue("sleepsUntilRandomise", -1);
            if (CONFIG.sleepsBetweenRandomises() > 1 && getValue("sleepsUntilRandomise") >= 0) {
                send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("sleepsUntilRandomise") + Formatting.RESET + " " + translate("origins-randomiser.message.sleepsUntilRandomise"));
            }
            if (getValue("sleepsUntilRandomise") <= 0) {
                randomOrigin(translate("origins-randomiser.reason.sleep"));
            }
        }

    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        modifyValue("livesUntilRandomise", -1);
        if(CONFIG.livesBetweenRandomises() > 1 && getValue("livesUntilRandomise") >= 0){
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("livesUntilRandomise") + Formatting.RESET + " " + translate("origins-randomiser.message.livesUntilRandomise"));
        }
        if (getValue("livesUntilRandomise") <= 0) {
            randomOrigin(translate("origins-randomiser.reason.death"));
        }
        if (CONFIG.enableLives()) {
            modifyValue("lives", -1);
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("lives") + Formatting.RESET + " " + translate("origins-randomiser.message.livesRemaining"));
        }
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (!this.getScoreboardTags().contains("firstJoin")) {
            this.addScoreboardTag("firstJoin");
            randomOrigin(translate("origins-randomiser.reason.firstJoin"));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        createObjective("livesUntilRandomise", CONFIG.livesBetweenRandomises());
        createObjective("sleepsUntilRandomise", CONFIG.sleepsBetweenRandomises());
        if (CONFIG.limitCommandUses()) {
            createObjective("uses", CONFIG.randomiseCommandUses());
        }
        if (CONFIG.enableLives()) {
            createObjective("lives", CONFIG.startingLives());
            if (getValue("lives") == 0) {
                this.changeGameMode(GameMode.SPECTATOR);
                send(translate("origins-randomiser.message.outOfLives"));
            }
        }
        if (CONFIG.livesBetweenRandomises() > 1 && !this.getScoreboardTags().contains("livesMessage")) {
            this.addScoreboardTag("livesMessage");
            send(translate("origins-randomiser.message.randomOriginAfter") + " " + Formatting.BOLD + CONFIG.livesBetweenRandomises() + Formatting.RESET + " " + translate("origins-randomiser.message.lives"));
        }
        if (CONFIG.sleepsBetweenRandomises() > 1 && !this.getScoreboardTags().contains("sleepsMessage")) {
            this.addScoreboardTag("sleepsMessage");
            send(translate("origins-randomiser.message.randomOriginAfter") + " " + Formatting.BOLD + CONFIG.sleepsBetweenRandomises() + Formatting.RESET + " " + translate("origins-randomiser.message.sleeps"));
        }
    }
}