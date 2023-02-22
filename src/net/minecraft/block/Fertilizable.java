/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface Fertilizable {
    public boolean isFertilizable(BlockView var1, BlockPos var2, BlockState var3, boolean var4);

    public boolean canGrow(World var1, Random var2, BlockPos var3, BlockState var4);

    public void grow(ServerWorld var1, Random var2, BlockPos var3, BlockState var4);
}

