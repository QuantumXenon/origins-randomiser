package quantumxenon.randomiser.utils;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import quantumxenon.randomiser.enums.Message;
import quantumxenon.randomiser.enums.Reason;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.RESET;

public interface OriginUtils {
    OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));

    static void randomOrigin(Reason reason, ServerPlayerEntity player) {
        if (ConfigUtils.randomiseOrigins()) {
            if (ConfigUtils.dropExtraInventory()) {
                PlayerUtils.dropItems(player);
            }
            Origin newOrigin = getOrigin(player);
            setOrigin(player, newOrigin);
            if (ConfigUtils.randomiserMessages()) {
                List<ServerPlayerEntity> playerList = Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList();
                for (ServerPlayerEntity entity : playerList) {
                    entity.sendMessage(Text.of(BOLD + PlayerUtils.getName(player) + RESET + " " + getReason(reason) + " " + BOLD + format(newOrigin) + RESET));
                }
            }
        } else {
            PlayerUtils.send(MessageUtils.getMessage(Message.DISABLED), player.getCommandSource());
        }
    }

    private static Origin getOrigin(ServerPlayerEntity player) {
        List<Origin> origins = layer.getRandomOrigins(player).stream().map(OriginRegistry::get).toList();
        Origin currentOrigin = ModComponents.ORIGIN.get(player).getOrigin(layer);
        Origin newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!ConfigUtils.allowDuplicateOrigins()) {
            while (newOrigin.equals(currentOrigin)) {
                newOrigin = origins.get(new Random().nextInt(origins.size()));
            }
        }
        return newOrigin;
    }

    private static void setOrigin(ServerPlayerEntity player, Origin origin) {
        ModComponents.ORIGIN.get(player).setOrigin(layer, origin);
        OriginComponent.sync(player);
    }

    private static String getReason(Reason reason) {
        switch (reason) {
            case DEATH -> {
                return MessageUtils.translate("origins-randomiser.reason.death");
            }
            case FIRST_JOIN -> {
                return MessageUtils.translate("origins-randomiser.reason.firstJoin");
            }
            case SLEEP -> {
                return MessageUtils.translate("origins-randomiser.reason.sleep");
            }
            default -> {
                return MessageUtils.translate("origins-randomiser.reason.command");
            }
        }
    }

    private static StringBuilder format(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }
}