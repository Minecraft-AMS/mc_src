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
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public TrunkVineTreeDecorator() {
        super(TreeDecoratorType.TRUNK_VINE);
    }

    public <T> TrunkVineTreeDecorator(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        logPositions.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, blockPos2 = blockPos.west())) {
                this.placeVine(world, blockPos2, VineBlock.EAST, set, box);
            }
            if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, blockPos2 = blockPos.east())) {
                this.placeVine(world, blockPos2, VineBlock.WEST, set, box);
            }
            if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, blockPos2 = blockPos.north())) {
                this.placeVine(world, blockPos2, VineBlock.SOUTH, set, box);
            }
            if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, blockPos2 = blockPos.south())) {
                this.placeVine(world, blockPos2, VineBlock.NORTH, set, box);
            }
        });
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("type"), (Object)ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString())))).getValue();
    }
}

