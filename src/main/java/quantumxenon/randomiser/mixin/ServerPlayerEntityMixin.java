package quantumxenon.randomiser.mixin;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quantumxenon.randomiser.config.OriginsRandomiserConfig;
import quantumxenon.randomiser.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.RESET;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Player {
    private final OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
    private final OriginsRandomiserConfig config = OriginsRandomiserConfig.getConfig();
    private final Scoreboard scoreboard = getScoreboard();
    private final String player = getName().getString();

    private ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    @Shadow
    public abstract void sendMessage(Text message, boolean overlay);

    private void send(String message) {
        sendMessage(Text.of(message), false);
    }

    private String translate(String key) {
        return new TranslatableText(key).getString();
    }

    private int getValue(String objective) {
        return (scoreboard.getPlayerScore(player, scoreboard.getObjective(objective)).getScore());
    }

    private void decrementValue(String objective) {
        getScoreboard().getPlayerScore(player, getScoreboard().getObjective(objective)).incrementScore(-1);
    }

    private void setValue(String objective, int value) {
        getScoreboard().getPlayerScore(player, getScoreboard().getObjective(objective)).setScore(value);
    }

    private boolean noScoreboardTag(String tag) {
        return !getScoreboardTags().contains(tag);
    }

    private void createObjective(String name, int number) {
        if (!scoreboard.containsObjective(name)) {
            scoreboard.addObjective(name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.RenderType.INTEGER);
            setValue(name, number);
            if (name.equals("uses")) {
                sendMessage(new TranslatableText("origins-randomiser.message.commandLimited", config.command.randomiseCommandUses),false);
            }
            if (name.equals("lives")) {
                sendMessage(new TranslatableText("origins-randomiser.message.livesEnabled", config.lives.startingLives),false);
            }
        }
    }

    private StringBuilder format(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    private Origin getOrigin() {
        List<Origin> origins = layer.getRandomOrigins(this).stream().map(OriginRegistry::get).toList();
        Origin currentOrigin = ModComponents.ORIGIN.get(this).getOrigin(layer);
        Origin newOrigin = origins.get(new Random().nextInt(origins.size()));
        if (!config.general.allowDuplicateOrigins) {
            while (newOrigin.equals(currentOrigin)) {
                newOrigin = origins.get(new Random().nextInt(origins.size()));
            }
        }
        return newOrigin;
    }

    private void setOrigin(Origin origin) {
        ModComponents.ORIGIN.get(this).setOrigin(layer, origin);
        OriginComponent.sync(this);
    }

    public void randomOrigin(String reason) {
        if (config.general.randomiseOrigins) {
            if (config.general.dropExtraInventory) {
                dropItems();
            }
            Origin origin = getOrigin();
            setOrigin(origin);
            if (config.general.randomiserMessages) {
                List<ServerPlayerEntity> playerList = Objects.requireNonNull(getServer()).getPlayerManager().getPlayerList();
                for (ServerPlayerEntity entity : playerList) {
                    entity.sendMessage(new LiteralText(BOLD + player + RESET + " " + reason + " " + BOLD + format(origin) + RESET), false);
                }
            }
        } else {
            send(translate("origins-randomiser.disabled"));
        }
    }

    private void dropItems() {
        PowerHolderComponent.getPowers(this, InventoryPower.class).forEach(inventory -> {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.getStack(i);
                dropItem(itemStack, true, false);
                inventory.setStack(i, ItemStack.EMPTY);
            }
        });
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (config.other.sleepRandomisesOrigin) {
            decrementValue("sleepsUntilRandomise");
            if (config.other.sleepsBetweenRandomises > 1 && getValue("sleepsUntilRandomise") > 0) {
                sendMessage(new TranslatableText("origins-randomiser.message.sleepsUntilRandomise", getValue("sleepsUntilRandomise")),false);
            }
            if (getValue("sleepsUntilRandomise") <= 0) {
                randomOrigin(translate("origins-randomiser.reason.sleep"));
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void death(CallbackInfo info) {
        decrementValue("livesUntilRandomise");
        if (config.lives.livesBetweenRandomises > 1 && getValue("livesUntilRandomise") > 0) {
            sendMessage(new TranslatableText("origins-randomiser.message.livesUntilRandomise", getValue("livesUntilRandomise")), false);
        }
        if (config.lives.enableLives) {
            decrementValue("lives");
            if (getValue("lives") <= 0) {
                changeGameMode(GameMode.SPECTATOR);
                send(translate("origins-randomiser.message.outOfLives"));
            } else {
                sendMessage(new TranslatableText("origins-randomiser.message.livesRemaining", getValue("lives")),false);
            }
        }
        if (getValue("livesUntilRandomise") <= 0) {
            randomOrigin(translate("origins-randomiser.reason.death"));
        }
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (noScoreboardTag("firstJoin")) {
            addScoreboardTag("firstJoin");
            randomOrigin(translate("origins-randomiser.reason.firstJoin"));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises);
        createObjective("sleepsUntilRandomise", config.other.sleepsBetweenRandomises);
        if (config.command.limitCommandUses) {
            createObjective("uses", config.command.randomiseCommandUses);
        }
        if (config.lives.enableLives) {
            createObjective("lives", config.lives.startingLives);
        }
        if (getValue("livesUntilRandomise") <= 0) {
            setValue("livesUntilRandomise", config.lives.livesBetweenRandomises);
        }
        if (getValue("sleepsUntilRandomise") <= 0) {
            setValue("sleepsUntilRandomise", config.other.sleepsBetweenRandomises);
        }
        if (config.lives.livesBetweenRandomises > 1 && noScoreboardTag("livesMessage")) {
            addScoreboardTag("livesMessage");
            sendMessage(new TranslatableText("origins-randomiser.message.randomOriginAfterLives", config.lives.livesBetweenRandomises), false);
        }
        if (config.other.sleepsBetweenRandomises > 1 && noScoreboardTag("sleepsMessage")) {
            addScoreboardTag("sleepsMessage");
            sendMessage(new TranslatableText("origins-randomiser.message.randomOriginAfterSleeps", config.other.sleepsBetweenRandomises),false);
        }
    }
}