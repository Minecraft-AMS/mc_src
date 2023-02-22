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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class MeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then(CommandManager.argument("action", StringArgumentType.greedyString()).executes(context -> {
            String string = StringArgumentType.getString((CommandContext)context, (String)"action");
            Entity entity = ((ServerCommandSource)context.getSource()).getEntity();
            MinecraftServer minecraftServer = ((ServerCommandSource)context.getSource()).getServer();
            if (entity != null) {
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                    serverPlayerEntity.getTextStream().filterText(string).thenAcceptAsync(message -> {
                        String string = message.getFiltered();
                        Text text = string.isEmpty() ? null : MeCommand.getEmoteText((CommandContext<ServerCommandSource>)context, string);
                        Text text2 = MeCommand.getEmoteText((CommandContext<ServerCommandSource>)context, message.getRaw());
                        minecraftServer.getPlayerManager().broadcast(text2, player -> serverPlayerEntity.shouldFilterMessagesSentTo((ServerPlayerEntity)player) ? text : text2, MessageType.CHAT, entity.getUuid());
                    }, (Executor)minecraftServer);
                    return 1;
                }
                minecraftServer.getPlayerManager().broadcast(MeCommand.getEmoteText((CommandContext<ServerCommandSource>)context, string), MessageType.CHAT, entity.getUuid());
            } else {
                minecraftServer.getPlayerManager().broadcast(MeCommand.getEmoteText((CommandContext<ServerCommandSource>)context, string), MessageType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }

    private static Text getEmoteText(CommandContext<ServerCommandSource> context, String arg) {
        return new TranslatableText("chat.type.emote", ((ServerCommandSource)context.getSource()).getDisplayName(), arg);
    }
}

