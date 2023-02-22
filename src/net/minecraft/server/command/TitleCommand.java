/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("title").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("clear").executes(commandContext -> TitleCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"))))).then(CommandManager.literal("reset").executes(commandContext -> TitleCommand.executeReset((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"))))).then(CommandManager.literal("title").then(CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.TITLE))))).then(CommandManager.literal("subtitle").then(CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.SUBTITLE))))).then(CommandManager.literal("actionbar").then(CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.ACTIONBAR))))).then(CommandManager.literal("times").then(CommandManager.argument("fadeIn", IntegerArgumentType.integer((int)0)).then(CommandManager.argument("stay", IntegerArgumentType.integer((int)0)).then(CommandManager.argument("fadeOut", IntegerArgumentType.integer((int)0)).executes(commandContext -> TitleCommand.executeTimes((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"fadeIn"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"stay"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"fadeOut")))))))));
    }

    private static int executeClear(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.CLEAR, null);
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.cleared.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.cleared.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.reset.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.reset.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeTitle(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title, TitleS2CPacket.Action type) throws CommandSyntaxException {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(type, Texts.parse(source, title, serverPlayerEntity, 0)));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeTimes(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int fadeIn, int stay, int fadeOut) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(fadeIn, stay, fadeOut);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.times.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.times.multiple", targets.size()), true);
        }
        return targets.size();
    }
}

