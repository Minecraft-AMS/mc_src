/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.remove.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist").requires(source -> source.hasPermissionLevel(3))).then(CommandManager.literal("on").executes(context -> WhitelistCommand.executeOn((ServerCommandSource)context.getSource())))).then(CommandManager.literal("off").executes(context -> WhitelistCommand.executeOff((ServerCommandSource)context.getSource())))).then(CommandManager.literal("list").executes(context -> WhitelistCommand.executeList((ServerCommandSource)context.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = ((ServerCommandSource)context.getSource()).getServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(player -> !playerManager.getWhitelist().isAllowed(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
        }).executes(context -> WhitelistCommand.executeAdd((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets")))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().getPlayerManager().getWhitelistedNames(), builder)).executes(context -> WhitelistCommand.executeRemove((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets")))))).then(CommandManager.literal("reload").executes(context -> WhitelistCommand.executeReload((ServerCommandSource)context.getSource()))));
    }

    private static int executeReload(ServerCommandSource source) {
        source.getServer().getPlayerManager().reloadWhitelist();
        source.sendFeedback(new TranslatableText("commands.whitelist.reloaded"), true);
        source.getServer().kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeAdd(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.add(whitelistEntry);
            source.sendFeedback(new TranslatableText("commands.whitelist.add.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Whitelist whitelist = source.getServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (!whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.remove(whitelistEntry);
            source.sendFeedback(new TranslatableText("commands.whitelist.remove.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        source.getServer().kickNonWhitelistedPlayers(source);
        return i;
    }

    private static int executeOn(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        if (playerManager.isWhitelistEnabled()) {
            throw ALREADY_ON_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(true);
        source.sendFeedback(new TranslatableText("commands.whitelist.enabled"), true);
        source.getServer().kickNonWhitelistedPlayers(source);
        return 1;
    }

    private static int executeOff(ServerCommandSource source) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        if (!playerManager.isWhitelistEnabled()) {
            throw ALREADY_OFF_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(false);
        source.sendFeedback(new TranslatableText("commands.whitelist.disabled"), true);
        return 1;
    }

    private static int executeList(ServerCommandSource source) {
        CharSequence[] strings = source.getServer().getPlayerManager().getWhitelistedNames();
        if (strings.length == 0) {
            source.sendFeedback(new TranslatableText("commands.whitelist.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.whitelist.list", strings.length, String.join((CharSequence)", ", strings)), false);
        }
        return strings.length;
    }
}

