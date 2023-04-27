/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;

public class ResetChunksCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("resetchunks").requires(source -> source.hasPermissionLevel(2))).executes(context -> ResetChunksCommand.executeResetChunks((ServerCommandSource)context.getSource(), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("range", IntegerArgumentType.integer((int)0, (int)5)).executes(context -> ResetChunksCommand.executeResetChunks((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger((CommandContext)context, (String)"range"), true))).then(CommandManager.argument("skipOldChunks", BoolArgumentType.bool()).executes(context -> ResetChunksCommand.executeResetChunks((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger((CommandContext)context, (String)"range"), BoolArgumentType.getBool((CommandContext)context, (String)"skipOldChunks"))))));
    }

    private static int executeResetChunks(ServerCommandSource source, int radius, boolean skipOldChunks) {
        ServerWorld serverWorld = source.getWorld();
        ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
        serverChunkManager.threadedAnvilChunkStorage.verifyChunkGenerator();
        Vec3d vec3d = source.getPosition();
        ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(vec3d));
        int i = chunkPos.z - radius;
        int j = chunkPos.z + radius;
        int k = chunkPos.x - radius;
        int l = chunkPos.x + radius;
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                ChunkPos chunkPos2 = new ChunkPos(n, m);
                WorldChunk worldChunk = serverChunkManager.getWorldChunk(n, m, false);
                if (worldChunk == null || skipOldChunks && worldChunk.usesOldNoise()) continue;
                for (BlockPos blockPos : BlockPos.iterate(chunkPos2.getStartX(), serverWorld.getBottomY(), chunkPos2.getStartZ(), chunkPos2.getEndX(), serverWorld.getTopY() - 1, chunkPos2.getEndZ())) {
                    serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 16);
                }
            }
        }
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "worldgen-resetchunks");
        long o = System.currentTimeMillis();
        int p = (radius * 2 + 1) * (radius * 2 + 1);
        for (ChunkStatus chunkStatus : ImmutableList.of((Object)ChunkStatus.BIOMES, (Object)ChunkStatus.NOISE, (Object)ChunkStatus.SURFACE, (Object)ChunkStatus.CARVERS, (Object)ChunkStatus.LIQUID_CARVERS, (Object)ChunkStatus.FEATURES)) {
            long q = System.currentTimeMillis();
            CompletionStage<Object> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);
            for (int r = chunkPos.z - radius; r <= chunkPos.z + radius; ++r) {
                for (int s = chunkPos.x - radius; s <= chunkPos.x + radius; ++s) {
                    ChunkPos chunkPos3 = new ChunkPos(s, r);
                    WorldChunk worldChunk2 = serverChunkManager.getWorldChunk(s, r, false);
                    if (worldChunk2 == null || skipOldChunks && worldChunk2.usesOldNoise()) continue;
                    ArrayList list = Lists.newArrayList();
                    int t = Math.max(1, chunkStatus.getTaskMargin());
                    for (int u = chunkPos3.z - t; u <= chunkPos3.z + t; ++u) {
                        for (int v = chunkPos3.x - t; v <= chunkPos3.x + t; ++v) {
                            Chunk chunk = serverChunkManager.getChunk(v, u, chunkStatus.getPrevious(), true);
                            Chunk chunk2 = chunk instanceof ReadOnlyChunk ? new ReadOnlyChunk(((ReadOnlyChunk)chunk).getWrappedChunk(), true) : (chunk instanceof WorldChunk ? new ReadOnlyChunk((WorldChunk)chunk, true) : chunk);
                            list.add(chunk2);
                        }
                    }
                    completableFuture = completableFuture.thenComposeAsync(unit -> chunkStatus.runGenerationTask(taskExecutor::send, serverWorld, serverChunkManager.getChunkGenerator(), serverWorld.getStructureTemplateManager(), serverChunkManager.getLightingProvider(), chunk -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                    }, list, true).thenApply(either -> {
                        if (chunkStatus == ChunkStatus.NOISE) {
                            either.left().ifPresent(chunk -> Heightmap.populateHeightmaps(chunk, ChunkStatus.POST_CARVER_HEIGHTMAPS));
                        }
                        return Unit.INSTANCE;
                    }), taskExecutor::send);
                }
            }
            source.getServer().runTasks(() -> completableFuture.isDone());
            LOGGER.debug(chunkStatus.getId() + " took " + (System.currentTimeMillis() - q) + " ms");
        }
        long w = System.currentTimeMillis();
        for (int x = chunkPos.z - radius; x <= chunkPos.z + radius; ++x) {
            for (int y = chunkPos.x - radius; y <= chunkPos.x + radius; ++y) {
                ChunkPos chunkPos4 = new ChunkPos(y, x);
                WorldChunk worldChunk3 = serverChunkManager.getWorldChunk(y, x, false);
                if (worldChunk3 == null || skipOldChunks && worldChunk3.usesOldNoise()) continue;
                for (BlockPos blockPos2 : BlockPos.iterate(chunkPos4.getStartX(), serverWorld.getBottomY(), chunkPos4.getStartZ(), chunkPos4.getEndX(), serverWorld.getTopY() - 1, chunkPos4.getEndZ())) {
                    serverChunkManager.markForUpdate(blockPos2);
                }
            }
        }
        LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - w) + " ms");
        long q = System.currentTimeMillis() - o;
        source.sendFeedback(Text.literal(String.format(Locale.ROOT, "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", p, q, p, Float.valueOf((float)q / (float)p))), true);
        return 1;
    }
}

