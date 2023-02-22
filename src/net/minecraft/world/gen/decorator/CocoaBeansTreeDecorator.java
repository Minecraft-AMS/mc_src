/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    private final float field_21318;

    public CocoaBeansTreeDecorator(float f) {
        super(TreeDecoratorType.COCOA);
        this.field_21318 = f;
    }

    public <T> CocoaBeansTreeDecorator(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0f));
    }

    @Override
    public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.field_21318) {
            return;
        }
        int i = logPositions.get(0).getY();
        logPositions.stream().filter(blockPos -> blockPos.getY() - i <= 2).forEach(blockPos -> {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                Direction direction2;
                BlockPos blockPos2;
                if (!(random.nextFloat() <= 0.25f) || !AbstractTreeFeature.isAir(world, blockPos2 = blockPos.add((direction2 = direction.getOpposite()).getOffsetX(), 0, direction2.getOffsetZ()))) continue;
                BlockState blockState = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, direction);
                this.setBlockStateAndEncompassPosition(world, blockPos2, blockState, set, box);
            }
        });
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("type"), (Object)ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()), (Object)ops.createString("probability"), (Object)ops.createFloat(this.field_21318)))).getValue();
    }
}

