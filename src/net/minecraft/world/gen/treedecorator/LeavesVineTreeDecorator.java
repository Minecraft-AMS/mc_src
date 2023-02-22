/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class LeavesVineTreeDecorator
extends TreeDecorator {
    public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final LeavesVineTreeDecorator INSTANCE = new LeavesVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    @Override
    public void generate(StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box) {
        leavesPositions.forEach(pos -> {
            BlockPos blockPos;
            if (random.nextInt(4) == 0 && Feature.isAir(world, blockPos = pos.west())) {
                this.placeVines(world, blockPos, VineBlock.EAST, placedStates, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(world, blockPos = pos.east())) {
                this.placeVines(world, blockPos, VineBlock.WEST, placedStates, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(world, blockPos = pos.north())) {
                this.placeVines(world, blockPos, VineBlock.SOUTH, placedStates, box);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(world, blockPos = pos.south())) {
                this.placeVines(world, blockPos, VineBlock.NORTH, placedStates, box);
            }
        });
    }

    private void placeVines(ModifiableTestableWorld world, BlockPos pos, BooleanProperty side, Set<BlockPos> placedStates, BlockBox box) {
        this.placeVine(world, pos, side, placedStates, box);
        pos = pos.down();
        for (int i = 4; Feature.isAir(world, pos) && i > 0; --i) {
            this.placeVine(world, pos, side, placedStates, box);
            pos = pos.down();
        }
    }
}

