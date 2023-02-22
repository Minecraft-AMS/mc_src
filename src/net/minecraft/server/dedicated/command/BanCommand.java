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
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
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
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.ban.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban").requires(source -> source.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).executes(context -> BanCommand.ban((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets"), null))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(context -> BanCommand.ban((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)context, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)context, "reason"))))));
    }

    private static int ban(ServerCommandSource source, Collection<GameProfile> targets, @Nullable Text reason) throws CommandSyntaxException {
        BannedPlayerList bannedPlayerList = source.getServer().getPlayerManager().getUserBanList();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (bannedPlayerList.contains(gameProfile)) continue;
            BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, source.getName(), null, reason == null ? null : reason.getString());
            bannedPlayerList.add(bannedPlayerEntry);
            ++i;
            source.sendFeedback(new TranslatableText("commands.ban.success", Texts.toText(gameProfile), bannedPlayerEntry.getReason()), true);
            ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(gameProfile.getId());
            if (serverPlayerEntity == null) continue;
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.banned"));
        }
        if (i == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        }
        return i;
    }
}

