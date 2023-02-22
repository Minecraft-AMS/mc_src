/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.reload.success", new Object[0]), true);
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().reload();
            return 0;
        }));
    }
}

