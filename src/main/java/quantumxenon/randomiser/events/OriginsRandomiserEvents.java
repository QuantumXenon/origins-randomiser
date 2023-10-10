package quantumxenon.randomiser.events;

import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

import static quantumxenon.randomiser.utils.OriginUtils.config;

public class OriginsRandomiserEvents {
    public static void firstJoin(ServerPlayerEntity player) {
        if (ScoreboardUtils.noScoreboardTag("firstJoin", player)) {
            if (config.general.randomiseOrigins) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }
}