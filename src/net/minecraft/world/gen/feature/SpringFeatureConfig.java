/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, Set<Block> validBlocks) {
        this.state = state;
        this.requiresBlockBelow = requiresBlockBelow;
        this.rockCount = rockCount;
        this.holeCount = holeCount;
        this.validBlocks = validBlocks;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("state"), (Object)FluidState.serialize(ops, this.state).getValue(), (Object)ops.createString("requires_block_below"), (Object)ops.createBoolean(this.requiresBlockBelow), (Object)ops.createString("rock_count"), (Object)ops.createInt(this.rockCount), (Object)ops.createString("hole_count"), (Object)ops.createInt(this.holeCount), (Object)ops.createString("valid_blocks"), (Object)ops.createList(this.validBlocks.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(arg_0 -> ops.createString(arg_0))))));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic2) {
        return new SpringFeatureConfig(dynamic2.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), dynamic2.get("requires_block_below").asBoolean(true), dynamic2.get("rock_count").asInt(4), dynamic2.get("hole_count").asInt(1), (Set<Block>)ImmutableSet.copyOf((Collection)dynamic2.get("valid_blocks").asList(dynamic -> Registry.BLOCK.get(new Identifier(dynamic.asString("minecraft:air"))))));
    }
}

