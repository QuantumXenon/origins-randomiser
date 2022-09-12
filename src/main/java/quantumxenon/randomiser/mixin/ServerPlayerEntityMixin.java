package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.OriginsRandomiser;
import quantumxenon.randomiser.entity.Player;

import java.util.List;
import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Player {
    private static final OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));

    private ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    private boolean gameRule(GameRules.Key<GameRules.BooleanRule> gameRule) {
        return Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(gameRule);
    }

    public void randomOrigin(String reason) {
        List<Identifier> originsList = layer.getRandomOrigins(this);
        Origin origin = OriginRegistry.get(originsList.get(this.getRandom().nextInt(originsList.size())));
        if (gameRule(OriginsRandomiser.randomiseOrigins)) {
            setOrigin(this, origin);
            if (gameRule(OriginsRandomiser.randomiserMessages)) {
                for (ServerPlayerEntity player : Objects.requireNonNull(this.getServer()).getPlayerManager().getPlayerList()) {
                    player.sendMessage(Text.of(Formatting.BOLD + this.getName().getString() + Formatting.RESET + reason + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET));
                }
            }
        } else {
            this.sendMessage(Text.of("Origin randomising has been disabled."));
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder formattedOrigin = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].replace("_", " ").toLowerCase().split("\\s+")) {
            formattedOrigin.append(StringUtils.capitalize(word)).append(" ");
        }
        return formattedOrigin;
    }

    private void setOrigin(PlayerEntity player, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void die(CallbackInfo info) {
        randomOrigin(" died and respawned as a ");
    }

    @Inject(at = @At("TAIL"), method = "moveToSpawn")
    private void spawn(CallbackInfo info) {
        randomOrigin(" spawned for the first time as a ");
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (gameRule(OriginsRandomiser.sleepRandomisesOrigin)) {
            randomOrigin(" slept and woke up as a ");
        }
    }
}