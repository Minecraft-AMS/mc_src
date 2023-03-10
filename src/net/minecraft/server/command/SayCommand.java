/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            Text text = MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)context, "message");
            TranslatableText text2 = new TranslatableText("chat.type.announcement", ((ServerCommandSource)context.getSource()).getDisplayName(), text);
            Entity entity = ((ServerCommandSource)context.getSource()).getEntity();
            if (entity != null) {
                ((ServerCommandSource)context.getSource()).getServer().getPlayerManager().broadcast(text2, MessageType.CHAT, entity.getUuid());
            } else {
                ((ServerCommandSource)context.getSource()).getServer().getPlayerManager().broadcast(text2, MessageType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }
}

