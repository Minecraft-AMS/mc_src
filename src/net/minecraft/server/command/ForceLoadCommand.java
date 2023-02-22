/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.server.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.dimension.DimensionType;

public class ForceLoadCommand {
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.forceload.toobig", object, object2));
    private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.forceload.query.failure", object, object2));
    private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.added.failure", new Object[0]));
    private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.removed.failure", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("forceload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), true))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)CommandManager.literal("remove").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), false))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), false))))).then(CommandManager.literal("all").executes(commandContext -> ForceLoadCommand.executeRemoveAll((ServerCommandSource)commandContext.getSource()))))).then(((LiteralArgumentBuilder)CommandManager.literal("query").executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }

    private static int executeQuery(ServerCommandSource source, ColumnPos pos) throws CommandSyntaxException {
        ChunkPos chunkPos = new ChunkPos(pos.x >> 4, pos.z >> 4);
        DimensionType dimensionType = source.getWorld().getDimension().getType();
        boolean bl = source.getMinecraftServer().getWorld(dimensionType).getForcedChunks().contains(chunkPos.toLong());
        if (bl) {
            source.sendFeedback(new TranslatableText("commands.forceload.query.success", chunkPos, dimensionType), false);
            return 1;
        }
        throw QUERY_FAILURE_EXCEPTION.create((Object)chunkPos, (Object)dimensionType);
    }

    private static int executeQuery(ServerCommandSource source) {
        DimensionType dimensionType = source.getWorld().getDimension().getType();
        LongSet longSet = source.getMinecraftServer().getWorld(dimensionType).getForcedChunks();
        int i = longSet.size();
        if (i > 0) {
            String string = Joiner.on((String)", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (i == 1) {
                source.sendFeedback(new TranslatableText("commands.forceload.list.single", dimensionType, string), false);
            } else {
                source.sendFeedback(new TranslatableText("commands.forceload.list.multiple", i, dimensionType, string), false);
            }
        } else {
            source.sendError(new TranslatableText("commands.forceload.added.none", dimensionType));
        }
        return i;
    }

    private static int executeRemoveAll(ServerCommandSource source) {
        DimensionType dimensionType = source.getWorld().getDimension().getType();
        ServerWorld serverWorld = source.getMinecraftServer().getWorld(dimensionType);
        LongSet longSet = serverWorld.getForcedChunks();
        longSet.forEach(l -> serverWorld.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
        source.sendFeedback(new TranslatableText("commands.forceload.removed.all", dimensionType), true);
        return 0;
    }

    private static int executeChange(ServerCommandSource source, ColumnPos from, ColumnPos to, boolean forceLoaded) throws CommandSyntaxException {
        int i = Math.min(from.x, to.x);
        int j = Math.min(from.z, to.z);
        int k = Math.max(from.x, to.x);
        int l = Math.max(from.z, to.z);
        if (i < -30000000 || j < -30000000 || k >= 30000000 || l >= 30000000) {
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        }
        int o = k >> 4;
        int m = i >> 4;
        int p = l >> 4;
        int n = j >> 4;
        long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
        if (q > 256L) {
            throw TOOBIG_EXCEPTION.create((Object)256, (Object)q);
        }
        DimensionType dimensionType = source.getWorld().getDimension().getType();
        ServerWorld serverWorld = source.getMinecraftServer().getWorld(dimensionType);
        ChunkPos chunkPos = null;
        int r = 0;
        for (int s = m; s <= o; ++s) {
            for (int t = n; t <= p; ++t) {
                boolean bl = serverWorld.setChunkForced(s, t, forceLoaded);
                if (!bl) continue;
                ++r;
                if (chunkPos != null) continue;
                chunkPos = new ChunkPos(s, t);
            }
        }
        if (r == 0) {
            throw (forceLoaded ? ADDED_FAILURE_EXCEPTION : REMOVED_FAILURE_EXCEPTION).create();
        }
        if (r == 1) {
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".single", chunkPos, dimensionType), true);
        } else {
            ChunkPos chunkPos2 = new ChunkPos(m, n);
            ChunkPos chunkPos3 = new ChunkPos(o, p);
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".multiple", r, dimensionType, chunkPos2, chunkPos3), true);
        }
        return r;
    }
}

