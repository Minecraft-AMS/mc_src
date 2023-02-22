/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class ColumnPlacer
extends BlockPlacer {
    private final int minSize;
    private final int extraSize;

    public ColumnPlacer(int minSize, int extraSize) {
        super(BlockPlacerType.COLUMN_PLACER);
        this.minSize = minSize;
        this.extraSize = extraSize;
    }

    public <T> ColumnPlacer(Dynamic<T> dynamic) {
        this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
    }

    @Override
    public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
        int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);
        for (int j = 0; j < i; ++j) {
            iWorld.setBlockState(mutable, blockState, 2);
            mutable.setOffset(Direction.UP);
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("type"), (Object)ops.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString()), (Object)ops.createString("min_size"), (Object)ops.createInt(this.minSize), (Object)ops.createString("extra_size"), (Object)ops.createInt(this.extraSize)))).getValue();
    }
}

