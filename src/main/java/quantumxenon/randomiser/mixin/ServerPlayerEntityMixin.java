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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
import quantumxenon.randomiser.entity.Player;

import java.util.List;
import java.util.Objects;

import static quantumxenon.randomiser.OriginsRandomiser.config;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Player {
    private static final OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
    private final String player = getName().getString();
    private final Scoreboard scoreboard = getScoreboard();

    private ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile, null);
    }

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    private void send(String message) {
        sendMessage(Text.of(message));
    }

    private String translate(String key) {
        return Text.translatable(key).getString();
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

    private void createObjective(String name, int number) {
        if (!scoreboard.containsObjective(name)) {
            scoreboard.addObjective(name, ScoreboardCriterion.DUMMY, Text.of(name), ScoreboardCriterion.RenderType.INTEGER);
            setValue(name, number);
            if (name.equals("uses")) {
                send(translate("origins-randomiser.message.commandLimited") + " " + Formatting.BOLD + config.command.randomiseCommandUses + Formatting.RESET + " " + translate("origins-randomiser.message.uses"));
            }
            if (name.equals("lives")) {
                send(translate("origins-randomiser.message.livesEnabled") + " " + Formatting.BOLD + config.lives.startingLives + Formatting.RESET + " " + translate("origins-randomiser.message.lives"));
            }
        }
    }

    private StringBuilder formatOrigin(Origin origin) {
        StringBuilder originName = new StringBuilder();
        for (String word : origin.getIdentifier().toString().split(":")[1].split("_")) {
            originName.append(StringUtils.capitalize(word)).append(" ");
        }
        return originName;
    }

    private void setOrigin(Origin origin) {
        ModComponents.ORIGIN.get(this).setOrigin(layer, origin);
        OriginComponent.sync(this);
    }

    public void randomOrigin(String reason) {
        if (config.general.randomiseOrigins) {
            if (getValue("livesUntilRandomise") <= 0) {
                setValue("livesUntilRandomise", config.lives.livesBetweenRandomises);
            }
            if (getValue("sleepsUntilRandomise") <= 0) {
                setValue("sleepsUntilRandomise", config.other.sleepsBetweenRandomises);
            }
            if (config.general.dropExtraInventory) {
                dropItems();
            }
            List<Identifier> originList = layer.getRandomOrigins(this);
            Origin origin = OriginRegistry.get(originList.get(getRandom().nextInt(originList.size())));
            setOrigin(origin);
            if (config.general.randomiserMessages) {
                for (ServerPlayerEntity entity : Objects.requireNonNull(getServer()).getPlayerManager().getPlayerList()) {
                    entity.sendMessage(Text.of(Formatting.BOLD + player + Formatting.RESET + " " + reason + " " + Formatting.BOLD + formatOrigin(origin) + Formatting.RESET));
                }
            }
        } else {
            send(translate("origins-randomiser.origin.disabled"));
        }
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    private void sleep(CallbackInfo info) {
        if (config.other.sleepRandomisesOrigin) {
            decrementValue("sleepsUntilRandomise");
            if (config.other.sleepsBetweenRandomises > 1 && getValue("sleepsUntilRandomise") > 0) {
                send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("sleepsUntilRandomise") + Formatting.RESET + " " + translate("origins-randomiser.message.sleepsUntilRandomise"));
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
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("livesUntilRandomise") + Formatting.RESET + " " + translate("origins-randomiser.message.livesUntilRandomise"));
        }
        if (getValue("livesUntilRandomise") <= 0) {
            randomOrigin(translate("origins-randomiser.reason.death"));
        }
        if (config.lives.enableLives) {
            decrementValue("lives");
            send(translate("origins-randomiser.message.nowHave") + " " + Formatting.BOLD + getValue("lives") + Formatting.RESET + " " + translate("origins-randomiser.message.livesRemaining"));
        }
    }

    private void dropItems() {
        PowerHolderComponent.getPowers(this, InventoryPower.class).forEach(inventory -> {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.getStack(i);
                this.dropItem(itemStack, true, false);
                inventory.setStack(i, ItemStack.EMPTY);
            }
        });
    }

    @Inject(at = @At("TAIL"), method = "onSpawn")
    private void spawn(CallbackInfo info) {
        if (!getScoreboardTags().contains("firstJoin")) {
            addScoreboardTag("firstJoin");
            randomOrigin(translate("origins-randomiser.reason.firstJoin"));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        createObjective("livesUntilRandomise", config.lives.livesBetweenRandomises);
        createObjective("sleepsUntilRandomise", config.other.sleepsBetweenRandomises);
        if (config.lives.livesBetweenRandomises > 1 && !getScoreboardTags().contains("livesMessage")) {
            addScoreboardTag("livesMessage");
            send(translate("origins-randomiser.message.randomOriginAfter") + " " + Formatting.BOLD + config.lives.livesBetweenRandomises + Formatting.RESET + " " + translate("origins-randomiser.message.lives"));
        }
        if (config.other.sleepsBetweenRandomises > 1 && !getScoreboardTags().contains("sleepsMessage")) {
            addScoreboardTag("sleepsMessage");
            send(translate("origins-randomiser.message.randomOriginAfter") + " " + Formatting.BOLD + config.other.sleepsBetweenRandomises + Formatting.RESET + " " + translate("origins-randomiser.message.sleeps"));
        }
        if (config.lives.enableLives) {
            createObjective("lives", config.lives.startingLives);
            if (getValue("lives") == 0) {
                changeGameMode(GameMode.SPECTATOR);
                send(translate("origins-randomiser.message.outOfLives"));
            }
        }
        if (config.command.limitCommandUses) {
            createObjective("uses", config.command.randomiseCommandUses);
        }
    }
}