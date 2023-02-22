/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;

@FunctionalInterface
public interface ChunkStatusChangeListener {
    public void onChunkStatusChange(ChunkPos var1, ChunkHolder.LevelType var2);
}

