/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EmeraldOreFeatureConfig
implements FeatureConfig {
    public final BlockState target;
    public final BlockState state;

    public EmeraldOreFeatureConfig(BlockState target, BlockState state) {
        this.target = target;
        this.state = state;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("target"), (Object)BlockState.serialize(ops, this.target).getValue(), (Object)ops.createString("state"), (Object)BlockState.serialize(ops, this.state).getValue())));
    }

    public static <T> EmeraldOreFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState2 = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new EmeraldOreFeatureConfig(blockState, blockState2);
    }
}

