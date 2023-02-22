/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class BanListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("banlist").requires(serverCommandSource -> (serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled() || serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled()) && serverCommandSource.hasPermissionLevel(3))).executes(commandContext -> {
            PlayerManager playerManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return BanListCommand.execute((ServerCommandSource)commandContext.getSource(), Lists.newArrayList((Iterable)Iterables.concat(playerManager.getUserBanList().values(), playerManager.getIpBanList().values())));
        })).then(CommandManager.literal("ips").executes(commandContext -> BanListCommand.execute((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList().values())))).then(CommandManager.literal("players").executes(commandContext -> BanListCommand.execute((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList().values()))));
    }

    private static int execute(ServerCommandSource source, Collection<? extends BanEntry<?>> targets) {
        if (targets.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.banlist.none", new Object[0]), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.banlist.list", targets.size()), false);
            for (BanEntry<?> banEntry : targets) {
                source.sendFeedback(new TranslatableText("commands.banlist.entry", banEntry.toText(), banEntry.getSource(), banEntry.getReason()), false);
            }
        }
        return targets.size();
    }
}

