/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
    default public void updateSectionStatus(BlockPos pos, boolean status) {
        this.updateSectionStatus(ChunkSectionPos.from(pos), status);
    }

    public void updateSectionStatus(ChunkSectionPos var1, boolean var2);
}

