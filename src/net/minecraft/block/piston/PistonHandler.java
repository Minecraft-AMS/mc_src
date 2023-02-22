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
    private final boolean retracted;
    private final BlockPos posTo;
    private final Direction motionDirection;
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();
    private final Direction pistonDirection;

    public PistonHandler(World world, BlockPos pos, Direction dir, boolean retracted) {
        this.world = world;
        this.posFrom = pos;
        this.pistonDirection = dir;
        this.retracted = retracted;
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
        if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
            if (this.retracted && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
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
            if (!PistonHandler.isBlockSticky(this.world.getBlockState(blockPos).getBlock()) || this.canMoveAdjacentBlock(blockPos)) continue;
            return false;
        }
        return true;
    }

    private static boolean isBlockSticky(Block block) {
        return block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK;
    }

    private static boolean isAdjacentBlockStuck(Block block, Block block2) {
        if (block == Blocks.HONEY_BLOCK && block2 == Blocks.SLIME_BLOCK) {
            return false;
        }
        if (block == Blocks.SLIME_BLOCK && block2 == Blocks.HONEY_BLOCK) {
            return false;
        }
        return PistonHandler.isBlockSticky(block) || PistonHandler.isBlockSticky(block2);
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
        while (PistonHandler.isBlockSticky(block)) {
            BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
            Block block2 = block;
            blockState = this.world.getBlockState(blockPos);
            block = blockState.getBlock();
            if (blockState.isAir() || !PistonHandler.isAdjacentBlockStuck(block2, block) || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite()) || blockPos.equals(this.posFrom)) break;
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
                this.setMovedBlocks(j, l);
                for (int m = 0; m <= l + j; ++m) {
                    BlockPos blockPos3 = this.movedBlocks.get(m);
                    if (!PistonHandler.isBlockSticky(this.world.getBlockState(blockPos3).getBlock()) || this.canMoveAdjacentBlock(blockPos3)) continue;
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

    private void setMovedBlocks(int from, int to) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, to));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    private boolean canMoveAdjacentBlock(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            BlockPos blockPos;
            BlockState blockState2;
            if (direction.getAxis() == this.motionDirection.getAxis() || !PistonHandler.isAdjacentBlockStuck((blockState2 = this.world.getBlockState(blockPos = pos.offset(direction))).getBlock(), blockState.getBlock()) || this.tryMove(blockPos, direction)) continue;
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

