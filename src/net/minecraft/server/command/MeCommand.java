/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.concurrent.Executor;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class MeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then(CommandManager.argument("action", StringArgumentType.greedyString()).executes(commandContext -> {
            String string = StringArgumentType.getString((CommandContext)commandContext, (String)"action");
            Entity entity = ((ServerCommandSource)commandContext.getSource()).getEntity();
            MinecraftServer minecraftServer = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer();
            if (entity != null) {
                TextStream textStream;
                if (entity instanceof ServerPlayerEntity && (textStream = ((ServerPlayerEntity)entity).getTextStream()) != null) {
                    textStream.filterText(string).thenAcceptAsync(optional -> optional.ifPresent(string -> minecraftServer.getPlayerManager().broadcastChatMessage(MeCommand.method_31373((CommandContext<ServerCommandSource>)commandContext, string), MessageType.CHAT, entity.getUuid())), (Executor)minecraftServer);
                    return 1;
                }
                minecraftServer.getPlayerManager().broadcastChatMessage(MeCommand.method_31373((CommandContext<ServerCommandSource>)commandContext, string), MessageType.CHAT, entity.getUuid());
            } else {
                minecraftServer.getPlayerManager().broadcastChatMessage(MeCommand.method_31373((CommandContext<ServerCommandSource>)commandContext, string), MessageType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }

    private static Text method_31373(CommandContext<ServerCommandSource> commandContext, String string) {
        return new TranslatableText("chat.type.emote", ((ServerCommandSource)commandContext.getSource()).getDisplayName(), string);
    }
}

