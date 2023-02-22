/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockSource {
    default public BlockState get(BlockPos pos) {
        return this.sample(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState sample(int var1, int var2, int var3);
}

