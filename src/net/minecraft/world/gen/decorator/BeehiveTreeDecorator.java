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
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class BeehiveTreeDecorator
extends TreeDecorator {
    private final float chance;

    public BeehiveTreeDecorator(float chance) {
        super(TreeDecoratorType.BEEHIVE);
        this.chance = chance;
    }

    public <T> BeehiveTreeDecorator(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0f));
    }

    @Override
    public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.chance) {
            return;
        }
        Direction direction = BeehiveBlock.GENERATE_DIRECTIONS[random.nextInt(BeehiveBlock.GENERATE_DIRECTIONS.length)];
        int i = !leavesPositions.isEmpty() ? Math.max(leavesPositions.get(0).getY() - 1, logPositions.get(0).getY()) : Math.min(logPositions.get(0).getY() + 1 + random.nextInt(3), logPositions.get(logPositions.size() - 1).getY());
        List list = logPositions.stream().filter(blockPos -> blockPos.getY() == i).collect(Collectors.toList());
        if (list.isEmpty()) {
            return;
        }
        BlockPos blockPos2 = (BlockPos)list.get(random.nextInt(list.size()));
        BlockPos blockPos22 = blockPos2.offset(direction);
        if (!AbstractTreeFeature.isAir(world, blockPos22) || !AbstractTreeFeature.isAir(world, blockPos22.offset(Direction.SOUTH))) {
            return;
        }
        BlockState blockState = (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
        this.setBlockStateAndEncompassPosition(world, blockPos22, blockState, set, box);
        BlockEntity blockEntity = world.getBlockEntity(blockPos22);
        if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            int j = 2 + random.nextInt(2);
            for (int k = 0; k < j; ++k) {
                BeeEntity beeEntity = new BeeEntity((EntityType<? extends BeeEntity>)EntityType.BEE, world.getWorld());
                beehiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
            }
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("type"), (Object)ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()), (Object)ops.createString("probability"), (Object)ops.createFloat(this.chance)))).getValue();
    }
}

