package com.quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({ServerPlayerEntity.class})
public abstract class Mixins extends PlayerEntity implements Player {
    private Mixins(World world, BlockPos position, float yaw, GameProfile profile) {
        super(world, position, yaw, profile, null);
    }

    public Origin randomOrigin(boolean deathMessage) {
        OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
        List<Identifier> origins = layer.getRandomOrigins(this);
        Origin origin = OriginRegistry.get(origins.get(this.getRandom().nextInt(origins.size())));
        setOrigin(this, layer, origin);
        return origin;
    }

    private void setOrigin(PlayerEntity player, OriginLayer layer, Origin origin) {
        OriginComponent component = ModComponents.ORIGIN.get(player);
        component.setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    @Inject(at = {@At("TAIL")}, method = {"onDeath"})
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        this.randomOrigin(false);
    }
}

