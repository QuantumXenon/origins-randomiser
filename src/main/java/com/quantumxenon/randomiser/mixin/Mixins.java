package com.quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import com.quantumxenon.randomiser.Randomiser;
import com.quantumxenon.randomiser.entity.Player;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class Mixins extends PlayerEntity implements Player {
    private static final OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));

    private Mixins(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    public void randomOrigin(String reason) {
        List<Identifier> originsList = layer.getRandomOrigins(this);
        Origin origin = OriginRegistry.get(originsList.get(this.getRandom().nextInt(originsList.size())));
        setOrigin(this, origin);

        if (gameRule(Randomiser.randomiserMessages)) {
            for (ServerPlayerEntity player : Objects.requireNonNull(this.getServer()).getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.of(Formatting.BOLD + this.getName().getString() + Formatting.RESET + reason + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET));
            }
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder formattedOrigin = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].replace("_", " ").toLowerCase().split("\\s+")) {
            formattedOrigin.append(word.replaceFirst(".", word.substring(0, 1).toUpperCase())).append(" ");
        }
        return formattedOrigin;
    }

    private void setOrigin(PlayerEntity player, Origin origin) {
        OriginComponent component = ModComponents.ORIGIN.get(player);
        component.setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    private boolean gameRule(GameRules.Key<GameRules.BooleanRule> gameRule) {
        return Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(gameRule);
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(DamageSource source, CallbackInfo info) {
        if (gameRule(Randomiser.randomiseOrigins)) {
            randomOrigin(" died and respawned as a ");
        } else {
            this.sendMessage(Text.of("Origin randomising has been disabled."));
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (gameRule(Randomiser.sleepRandomisesOrigin) && gameRule(Randomiser.randomiseOrigins)) {
            randomOrigin(" slept and woke up as a ");
        } else if (gameRule(Randomiser.sleepRandomisesOrigin) && !gameRule(Randomiser.randomiseOrigins)) {
            this.sendMessage(Text.of("Origin randomising has been disabled."));
        }
    }

    @Inject(at = @At("TAIL"), method = "moveToSpawn")
    private void spawn(ServerWorld serverWorld, CallbackInfo info) {
        randomOrigin(" spawned for the first time as a ");
    }
}