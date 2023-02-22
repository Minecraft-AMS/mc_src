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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Texts;
import net.minecraft.util.Util;

public class TellRawCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tellraw").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", TextArgumentType.text()).executes(context -> {
            int i = 0;
            for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets")) {
                serverPlayerEntity.sendSystemMessage(Texts.parse((ServerCommandSource)context.getSource(), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)context, "message"), (Entity)serverPlayerEntity, 0), Util.NIL_UUID);
                ++i;
            }
            return i;
        }))));
    }
}

