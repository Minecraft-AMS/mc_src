/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class BanCommand {
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.ban.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).executes(commandContext -> BanCommand.ban((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets"), null))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(commandContext -> BanCommand.ban((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }

    private static int ban(ServerCommandSource serverCommandSource, Collection<GameProfile> collection, @Nullable Text text) throws CommandSyntaxException {
        BannedPlayerList bannedPlayerList = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (bannedPlayerList.contains(gameProfile)) continue;
            BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, serverCommandSource.getName(), null, text == null ? null : text.getString());
            bannedPlayerList.add(bannedPlayerEntry);
            ++i;
            serverCommandSource.sendFeedback(new TranslatableText("commands.ban.success", Texts.toText(gameProfile), bannedPlayerEntry.getReason()), true);
            ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(gameProfile.getId());
            if (serverPlayerEntity == null) continue;
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.banned", new Object[0]));
        }
        if (i == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        }
        return i;
    }
}

