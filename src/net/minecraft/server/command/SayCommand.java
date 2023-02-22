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
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage((CommandContext<ServerCommandSource>)context, "message");
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
            signedMessage.decorate(serverCommandSource, message -> playerManager.broadcast((SignedMessage)message, serverCommandSource, MessageType.params(MessageType.SAY_COMMAND, serverCommandSource)));
            return 1;
        })));
    }
}

