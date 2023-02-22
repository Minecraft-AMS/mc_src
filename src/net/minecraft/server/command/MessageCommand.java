/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage((CommandContext<ServerCommandSource>)context, "message");
            try {
                return MessageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), signedMessage);
            }
            catch (Exception exception) {
                signedMessage.sendHeader((ServerCommandSource)context.getSource());
                throw exception;
            }
        }))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect((CommandNode)literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect((CommandNode)literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage) {
        MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, source);
        signedMessage.decorate(source, message -> {
            SentMessage sentMessage = SentMessage.of(message);
            boolean bl = message.isFullyFiltered();
            Entity entity = source.getEntity();
            boolean bl2 = false;
            for (ServerPlayerEntity serverPlayerEntity : targets) {
                MessageType.Parameters parameters2 = MessageType.params(MessageType.MSG_COMMAND_OUTGOING, source).withTargetName(serverPlayerEntity.getDisplayName());
                source.sendChatMessage(sentMessage, false, parameters2);
                boolean bl3 = source.shouldFilterText(serverPlayerEntity);
                serverPlayerEntity.sendChatMessage(sentMessage, bl3, parameters);
                bl2 |= bl && bl3 && serverPlayerEntity != entity;
            }
            if (bl2) {
                source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
            }
            sentMessage.afterPacketsSent(source.getServer().getPlayerManager());
        });
        return targets.size();
    }
}

