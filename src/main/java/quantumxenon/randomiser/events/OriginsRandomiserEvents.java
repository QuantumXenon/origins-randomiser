package quantumxenon.randomiser.events;

import net.minecraft.server.network.ServerPlayerEntity;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.enums.Reason;
import quantumxenon.randomiser.utils.OriginUtils;
import quantumxenon.randomiser.utils.ScoreboardUtils;

public class OriginsRandomiserEvents {
    private static final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();

    public static void firstJoin(ServerPlayerEntity player) {
        if (ScoreboardUtils.noScoreboardTag("firstJoin", player)) {
            ScoreboardUtils.createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises, player);
            ScoreboardUtils.createObjective("sleepsUntilRandomise", config.other.sleepsBetweenRandomises, player);
            ScoreboardUtils.createObjective("uses", config.command.randomiseCommandUses, player);
            ScoreboardUtils.createObjective("lives", config.lives.startingLives, player);
            if (config.general.randomiseOrigins && config.general.randomiseOnFirstJoin) {
                OriginUtils.randomOrigin(Reason.FIRST_JOIN, player);
            }
        }
    }
}