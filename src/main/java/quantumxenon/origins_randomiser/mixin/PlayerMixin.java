package quantumxenon.origins_randomiser.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.enums.Tag;
import quantumxenon.origins_randomiser.utils.OriginUtils;
import quantumxenon.origins_randomiser.utils.PlayerUtils;

@Mixin(Player.class)
public abstract class PlayerMixin {
    private final Player player = ((Player) (Object) this);

    @Inject(at = @At("TAIL"), method = "respawn")
    private void spawn(CallbackInfo info) {
        if (PlayerUtils.noScoreboardTag(Tag.FIRST_JOIN, player)) {
            player.addTag(PlayerUtils.tagName(Tag.FIRST_JOIN));
            OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
        }
    }

    @Inject(at = @At("TAIL"), method = "die")
    private void death(CallbackInfo info) {
        OriginUtils.randomOrigin(Reason.DEATH, player);
    }
}