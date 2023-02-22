/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ModifiableWorld {
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3);

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean breakBlock(BlockPos pos, boolean drop) {
        return this.breakBlock(pos, drop, null);
    }

    public boolean breakBlock(BlockPos var1, boolean var2, @Nullable Entity var3);

    default public boolean spawnEntity(Entity entity) {
        return false;
    }
}

