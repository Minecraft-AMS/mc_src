/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3);

    public boolean removeBlock(BlockPos var1, boolean var2);

    public boolean breakBlock(BlockPos var1, boolean var2);

    default public boolean spawnEntity(Entity entity) {
        return false;
    }
}

