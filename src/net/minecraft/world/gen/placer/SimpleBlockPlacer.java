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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class SimpleBlockPlacer
extends BlockPlacer {
    public SimpleBlockPlacer() {
        super(BlockPlacerType.SIMPLE_BLOCK_PLACER);
    }

    public <T> SimpleBlockPlacer(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
        iWorld.setBlockState(blockPos, blockState, 2);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("type"), (Object)ops.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString())))).getValue();
    }
}

