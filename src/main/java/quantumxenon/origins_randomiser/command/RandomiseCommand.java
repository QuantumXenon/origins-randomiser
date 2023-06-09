package quantumxenon.origins_randomiser.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import quantumxenon.origins_randomiser.enums.Reason;
import quantumxenon.origins_randomiser.utils.OriginUtils;


public class RandomiseCommand {
    public RandomiseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("randomise").executes((context) -> randomise(context.getSource())));
    }

    private static int randomise(CommandSourceStack source) {
        OriginUtils.randomOrigin(Reason.COMMAND, source.getPlayer());
        return 1;
    }
}