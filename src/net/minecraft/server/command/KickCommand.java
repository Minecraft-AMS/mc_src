/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kick").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), new TranslatableText("multiplayer.disconnect.kicked", new Object[0])))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text reason) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.disconnect(reason);
            source.sendFeedback(new TranslatableText("commands.kick.success", serverPlayerEntity.getDisplayName(), reason), true);
        }
        return targets.size();
    }
}

