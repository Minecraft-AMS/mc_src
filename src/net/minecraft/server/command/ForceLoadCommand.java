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
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ForceLoadCommand {
    private static final int MAX_CHUNKS = 256;
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> new TranslatableText("commands.forceload.toobig", maxCount, count));
    private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((chunkPos, registryKey) -> new TranslatableText("commands.forceload.query.failure", chunkPos, registryKey));
    private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.removed.failure"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("forceload").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(context -> ForceLoadCommand.executeChange((ServerCommandSource)context.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), true))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(context -> ForceLoadCommand.executeChange((ServerCommandSource)context.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "to"), true)))))).then(((LiteralArgumentBuilder)CommandManager.literal("remove").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(context -> ForceLoadCommand.executeChange((ServerCommandSource)context.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), false))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(context -> ForceLoadCommand.executeChange((ServerCommandSource)context.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "to"), false))))).then(CommandManager.literal("all").executes(context -> ForceLoadCommand.executeRemoveAll((ServerCommandSource)context.getSource()))))).then(((LiteralArgumentBuilder)CommandManager.literal("query").executes(context -> ForceLoadCommand.executeQuery((ServerCommandSource)context.getSource()))).then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos()).executes(context -> ForceLoadCommand.executeQuery((ServerCommandSource)context.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)context, "pos"))))));
    }

    private static int executeQuery(ServerCommandSource source, ColumnPos pos) throws CommandSyntaxException {
        ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(pos.x), ChunkSectionPos.getSectionCoord(pos.z));
        ServerWorld serverWorld = source.getWorld();
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        boolean bl = serverWorld.getForcedChunks().contains(chunkPos.toLong());
        if (bl) {
            source.sendFeedback(new TranslatableText("commands.forceload.query.success", chunkPos, registryKey.getValue()), false);
            return 1;
        }
        throw QUERY_FAILURE_EXCEPTION.create((Object)chunkPos, (Object)registryKey.getValue());
    }

    private static int executeQuery(ServerCommandSource source) {
        ServerWorld serverWorld = source.getWorld();
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        LongSet longSet = serverWorld.getForcedChunks();
        int i = longSet.size();
        if (i > 0) {
            String string = Joiner.on((String)", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (i == 1) {
                source.sendFeedback(new TranslatableText("commands.forceload.list.single", registryKey.getValue(), string), false);
            } else {
                source.sendFeedback(new TranslatableText("commands.forceload.list.multiple", i, registryKey.getValue(), string), false);
            }
        } else {
            source.sendError(new TranslatableText("commands.forceload.added.none", registryKey.getValue()));
        }
        return i;
    }

    private static int executeRemoveAll(ServerCommandSource source) {
        ServerWorld serverWorld = source.getWorld();
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        LongSet longSet = serverWorld.getForcedChunks();
        longSet.forEach(l -> serverWorld.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
        source.sendFeedback(new TranslatableText("commands.forceload.removed.all", registryKey.getValue()), true);
        return 0;
    }

    private static int executeChange(ServerCommandSource source, ColumnPos from, ColumnPos to, boolean forceLoaded) throws CommandSyntaxException {
        int p;
        int i = Math.min(from.x, to.x);
        int j = Math.min(from.z, to.z);
        int k = Math.max(from.x, to.x);
        int l = Math.max(from.z, to.z);
        if (i < -30000000 || j < -30000000 || k >= 30000000 || l >= 30000000) {
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        }
        int m = ChunkSectionPos.getSectionCoord(i);
        int n = ChunkSectionPos.getSectionCoord(j);
        int o = ChunkSectionPos.getSectionCoord(k);
        long q = ((long)(o - m) + 1L) * ((long)((p = ChunkSectionPos.getSectionCoord(l)) - n) + 1L);
        if (q > 256L) {
            throw TOO_BIG_EXCEPTION.create((Object)256, (Object)q);
        }
        ServerWorld serverWorld = source.getWorld();
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
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
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".single", chunkPos, registryKey.getValue()), true);
        } else {
            ChunkPos chunkPos2 = new ChunkPos(m, n);
            ChunkPos chunkPos3 = new ChunkPos(o, p);
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".multiple", r, registryKey.getValue(), chunkPos2, chunkPos3), true);
        }
        return r;
    }
}

