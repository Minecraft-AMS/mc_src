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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class OpCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.op.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("op").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerManager playerManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(serverPlayerEntity -> !playerManager.isOperator(serverPlayerEntity.getGameProfile())).map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> OpCommand.op((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }

    private static int op(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (playerManager.isOperator(gameProfile)) continue;
            playerManager.addToOperators(gameProfile);
            ++i;
            source.sendFeedback(new TranslatableText("commands.op.success", targets.iterator().next().getName()), true);
        }
        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        }
        return i;
    }
}

