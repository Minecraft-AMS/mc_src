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
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public final FluidState state;

    public SpringFeatureConfig(FluidState state) {
        this.state = state;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("state"), (Object)FluidState.serialize(ops, this.state).getValue())));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
        FluidState fluidState = dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState());
        return new SpringFeatureConfig(fluidState);
    }
}

