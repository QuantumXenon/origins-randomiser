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
import net.minecraft.server.world.ServerWorld;
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

import java.util.List;
import java.util.Objects;

import static com.quantumxenon.randomiser.Randomiser.*;

@Mixin({ServerPlayerEntity.class})
public abstract class Mixins extends PlayerEntity implements Player {
    OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));

    private Mixins(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    public Origin randomOrigin() {
        List<Identifier> originsList = layer.getRandomOrigins(this);
        Origin chosenOrigin = OriginRegistry.get(originsList.get(this.getRandom().nextInt(originsList.size())));
        setOrigin(this, chosenOrigin);

        Text message = Text.of(Formatting.BOLD + this.getName().getString() + Formatting.BOLD + " is now a " + Formatting.BOLD + StringUtils.capitalize(chosenOrigin.getIdentifier().toString().split(":")[1]).replace("_", " ") + Formatting.RESET + ".");
        if (Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(randomiserMessages)) {
            if (this.getServer() != null) {
                for (ServerPlayerEntity player : this.getServer().getPlayerManager().getPlayerList()) {
                    player.sendMessage(message, false);
                }
            }
        }
        return chosenOrigin;
    }

    private void setOrigin(PlayerEntity player, Origin newOrigin) {
        OriginComponent component = ModComponents.ORIGIN.get(player);
        component.setOrigin(layer, newOrigin);
        OriginComponent.sync(player);
    }

    @Inject(at = {@At("TAIL")}, method = {"onDeath"})
    private void death(DamageSource source, CallbackInfo info) {
        if (Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(randomiseOrigins)) {
            this.randomOrigin();
        }
    }

    @Inject(at = {@At("TAIL")}, method = {"wakeUp"})
    private void sleep(CallbackInfo info) {
        if (Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(sleepRandomises) && Objects.requireNonNull(this.getServer()).getGameRules().getBoolean(randomiseOrigins)) {
            this.randomOrigin();
        }
    }

    @Inject(at = {@At("TAIL")}, method = {"moveToSpawn"})
    private void spawn(ServerWorld serverWorld, CallbackInfo info) {
        this.randomOrigin();
    }
}