/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
    private final World world;
    private final BlockPos posFrom;
    private final boolean field_12247;
    private final BlockPos posTo;
    private final Direction motionDirection;
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();
    private final Direction field_12248;

    public PistonHandler(World world, BlockPos pos, Direction dir, boolean retracted) {
        this.world = world;
        this.posFrom = pos;
        this.field_12248 = dir;
        this.field_12247 = retracted;
        if (retracted) {
            this.motionDirection = dir;
            this.posTo = pos.offset(dir);
        } else {
            this.motionDirection = dir.getOpposite();
            this.posTo = pos.offset(dir, 2);
        }
    }

    public boolean calculatePush() {
        this.movedBlocks.clear();
        this.brokenBlocks.clear();
        BlockState blockState = this.world.getBlockState(this.posTo);
        if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.field_12248)) {
            if (this.field_12247 && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(this.posTo);
                return true;
            }
            return false;
        }
        if (!this.tryMove(this.posTo, this.motionDirection)) {
            return false;
        }
        for (int i = 0; i < this.movedBlocks.size(); ++i) {
            BlockPos blockPos = this.movedBlocks.get(i);
            if (this.world.getBlockState(blockPos).getBlock() != Blocks.SLIME_BLOCK || this.method_11538(blockPos)) continue;
            return false;
        }
        return true;
    }

    private boolean tryMove(BlockPos pos, Direction dir) {
        int k;
        BlockState blockState = this.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (blockState.isAir()) {
            return true;
        }
        if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
            return true;
        }
        if (pos.equals(this.posFrom)) {
            return true;
        }
        if (this.movedBlocks.contains(pos)) {
            return true;
        }
        int i = 1;
        if (i + this.movedBlocks.size() > 12) {
            return false;
        }
        while (block == Blocks.SLIME_BLOCK) {
            BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
            blockState = this.world.getBlockState(blockPos);
            block = blockState.getBlock();
            if (blockState.isAir() || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite()) || blockPos.equals(this.posFrom)) break;
            if (++i + this.movedBlocks.size() <= 12) continue;
            return false;
        }
        int j = 0;
        for (k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
            ++j;
        }
        k = 1;
        while (true) {
            BlockPos blockPos2;
            int l;
            if ((l = this.movedBlocks.indexOf(blockPos2 = pos.offset(this.motionDirection, k))) > -1) {
                this.method_11539(j, l);
                for (int m = 0; m <= l + j; ++m) {
                    BlockPos blockPos3 = this.movedBlocks.get(m);
                    if (this.world.getBlockState(blockPos3).getBlock() != Blocks.SLIME_BLOCK || this.method_11538(blockPos3)) continue;
                    return false;
                }
                return true;
            }
            blockState = this.world.getBlockState(blockPos2);
            if (blockState.isAir()) {
                return true;
            }
            if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
                return false;
            }
            if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(blockPos2);
                return true;
            }
            if (this.movedBlocks.size() >= 12) {
                return false;
            }
            this.movedBlocks.add(blockPos2);
            ++j;
            ++k;
        }
    }

    private void method_11539(int i, int j) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, j));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - i, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(j, this.movedBlocks.size() - i));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    private boolean method_11538(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == this.motionDirection.getAxis() || this.tryMove(blockPos.offset(direction), direction)) continue;
            return false;
        }
        return true;
    }

    public List<BlockPos> getMovedBlocks() {
        return this.movedBlocks;
    }

    public List<BlockPos> getBrokenBlocks() {
        return this.brokenBlocks;
    }
}

