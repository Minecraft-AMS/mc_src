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
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setworldspawn").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> SetWorldSpawnCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> SetWorldSpawnCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos")))));
    }

    private static int execute(ServerCommandSource source, BlockPos pos) {
        source.getWorld().setSpawnPos(pos);
        source.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(pos));
        source.sendFeedback(new TranslatableText("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }
}

