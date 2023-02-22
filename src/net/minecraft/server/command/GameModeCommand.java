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
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
    public static final int REQUIRED_PERMISSION_LEVEL = 2;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(source -> source.hasPermissionLevel(2));
        for (GameMode gameMode : GameMode.values()) {
            literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(gameMode.getName()).executes(context -> GameModeCommand.execute((CommandContext<ServerCommandSource>)context, Collections.singleton(((ServerCommandSource)context.getSource()).getPlayerOrThrow()), gameMode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes(context -> GameModeCommand.execute((CommandContext<ServerCommandSource>)context, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "target"), gameMode))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
        MutableText text = Text.translatable("gameMode." + gameMode.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(Text.translatable("commands.gamemode.success.self", text), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendMessage(Text.translatable("gameMode.changed", text));
            }
            source.sendFeedback(Text.translatable("commands.gamemode.success.other", player.getDisplayName(), text), true);
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!serverPlayerEntity.changeGameMode(gameMode)) continue;
            GameModeCommand.sendFeedback((ServerCommandSource)context.getSource(), serverPlayerEntity, gameMode);
            ++i;
        }
        return i;
    }
}

