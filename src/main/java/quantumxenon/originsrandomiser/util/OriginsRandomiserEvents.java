package quantumxenon.originsrandomiser.util;

import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.originsrandomiser.config.OriginsRandomiserConfig;
import quantumxenon.originsrandomiser.enums.Reason;

public class OriginsRandomiserEvents {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void join(ServerPlayerEntity serverPlayer){
        OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
        if (!player.hasScoreboardTag("firstJoin")) {
            player.createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises);
            player.createObjective("sleepsUntilRandomise", config.sleep.sleepsBetweenRandomises);
            player.createObjective("uses", config.command.randomiseCommandUses);
            player.createObjective("lives", config.lives.startingLives);
            if (config.advanced.randomiseOnFirstJoin) {
                player.randomiseOrigin(Reason.FIRST_JOIN);
            }
        }
    }

    public static void respawn(ServerPlayerEntity serverPlayer) {
        OriginsRandomiserPlayer player = new OriginsRandomiserPlayer(serverPlayer);
        if (player.hasScoreboardTag("showOriginsScreen")) {
            player.clearOrigins();
            player.openOriginsScreen();
            player.removeScoreboardTag("showOriginsScreen");
        }
    }
}
