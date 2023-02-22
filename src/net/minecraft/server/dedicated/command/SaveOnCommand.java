/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class SaveOnCommand {
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.save.alreadyOn"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-on").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> {
            ServerCommandSource serverCommandSource = (ServerCommandSource)commandContext.getSource();
            boolean bl = false;
            for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
                if (serverWorld == null || !serverWorld.savingDisabled) continue;
                serverWorld.savingDisabled = false;
                bl = true;
            }
            if (!bl) {
                throw ALREADY_ON_EXCEPTION.create();
            }
            serverCommandSource.sendFeedback(new TranslatableText("commands.save.enabled"), true);
            return 1;
        }));
    }
}

