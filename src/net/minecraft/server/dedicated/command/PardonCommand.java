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
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class PardonCommand {
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.pardon.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pardon").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList().getNames(), suggestionsBuilder)).executes(commandContext -> PardonCommand.pardon((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }

    private static int pardon(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
        BannedPlayerList bannedPlayerList = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (!bannedPlayerList.contains(gameProfile)) continue;
            bannedPlayerList.remove(gameProfile);
            ++i;
            serverCommandSource.sendFeedback(new TranslatableText("commands.pardon.success", Texts.toText(gameProfile)), true);
        }
        if (i == 0) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        }
        return i;
    }
}

