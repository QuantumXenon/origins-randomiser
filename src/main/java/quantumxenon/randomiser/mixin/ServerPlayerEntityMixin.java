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
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.entity.Player;

import java.util.List;
import java.util.Objects;

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

    private void send(String message, PlayerEntity player) {
        player.sendMessage(Text.of(message));
    }

    private int getValue(String objective) {
        return (scoreboard.getPlayerScore(player, scoreboard.getObjective(objective)).getScore());
    }

    public void modifyValue(int number, PlayerEntity target, String objective) {
        target.getScoreboard().getPlayerScore(target.getName().getString(), target.getScoreboard().getObjective(objective)).incrementScore(number);
    }

    public void randomOrigin(String reason) {
        List<Identifier> originList = layer.getRandomOrigins(this);
        Origin origin = OriginRegistry.get(originList.get(this.getRandom().nextInt(originList.size())));
        if (CONFIG.randomiseOrigins()) {
            setOrigin(this, origin);
            if (CONFIG.randomiserMessages()) {
                for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
                    send(String.valueOf(Text.of(Formatting.BOLD + player + Formatting.RESET + reason + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET)), serverPlayer);
                }
            }
        } else {
            send("Origin randomising has been disabled.", this);
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder formattedOrigin = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].replace("_", " ").split("\\s+")) {
            formattedOrigin.append(StringUtils.capitalize(word)).append(" ");
        }
        return formattedOrigin;
    }

    private void setOrigin(PlayerEntity player, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (CONFIG.sleepRandomisesOrigin()) {
            randomOrigin(" slept and woke up as a ");
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        randomOrigin(" died and respawned as a ");
        if (CONFIG.enableLives()) {
            send("You now have " + Formatting.BOLD + getValue("lives") + Formatting.RESET + " lives remaining.", this);
            modifyValue(-1, this, "lives");
        }
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        String tag = "firstJoin";
        if (!this.getScoreboardTags().contains(tag)) {
            this.addScoreboardTag(tag);
            randomOrigin(" spawned for the first time as a ");
        }

        if (CONFIG.enableLives()) {
            String objective = "lives";
            if (!scoreboard.containsObjective(objective)) {
                scoreboard.addObjective(objective, ScoreboardCriterion.DUMMY, Text.of(objective), ScoreboardCriterion.RenderType.INTEGER);
                modifyValue(CONFIG.startingLives(), this, objective);
                send("Lives have been enabled. You start with " + CONFIG.startingLives() + " lives.", this);
            }
            if (getValue(objective) == 0) {
                //Set gamemode
            }
        }

        if (CONFIG.limitCommandUses()) {
            String objective = "uses";
            if (!scoreboard.containsObjective(objective)) {
                scoreboard.addObjective(objective, ScoreboardCriterion.DUMMY, Text.of(objective), ScoreboardCriterion.RenderType.INTEGER);
                modifyValue(CONFIG.randomiseCommandUses(), this, objective);
                send("Use of the /randomise command has been limited. You start with " + CONFIG.randomiseCommandUses() + " randomises.", this);
            }
        }
    }
}