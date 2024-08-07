package akai.thest;

import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;

public final class ReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("chestloot")
                .requires(source -> source.hasPermissionLevel(2)) // Must be a game master to use the command. Command will not show up in tab completion or execute to non operators or any operator that is permission level 1.
                .then(
                        CommandManager.literal("reload").executes(
                                commandContext -> execute()
                        )
                )
        );
    }

    private static int execute() {
        ChestLoot.loadEntries();
        return Command.SINGLE_SUCCESS; // Success
    }
}