package quantumxenon.randomiser.entity;

import net.minecraft.entity.player.PlayerEntity;

public interface Player {
    void modifyLives(int number, PlayerEntity player);

    void randomOrigin(String reason);
}