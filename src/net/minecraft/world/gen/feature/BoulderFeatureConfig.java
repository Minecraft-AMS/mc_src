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

public class BoulderFeatureConfig
implements FeatureConfig {
    public final BlockState state;
    public final int startRadius;

    public BoulderFeatureConfig(BlockState state, int startRadius) {
        this.state = state;
        this.startRadius = startRadius;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("state"), (Object)BlockState.serialize(ops, this.state).getValue(), (Object)ops.createString("start_radius"), (Object)ops.createInt(this.startRadius))));
    }

    public static <T> BoulderFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int i = dynamic.get("start_radius").asInt(0);
        return new BoulderFeatureConfig(blockState, i);
    }
}

