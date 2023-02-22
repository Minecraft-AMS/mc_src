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
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public final Target target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(Target target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("size"), (Object)ops.createInt(this.size), (Object)ops.createString("target"), (Object)ops.createString(this.target.getName()), (Object)ops.createString("state"), (Object)BlockState.serialize(ops, this.state).getValue())));
    }

    public static OreFeatureConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("size").asInt(0);
        Target target = Target.byName(dynamic.get("target").asString(""));
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new OreFeatureConfig(target, blockState, i);
    }

    public static enum Target {
        NATURAL_STONE("natural_stone", blockState -> {
            if (blockState != null) {
                Block block = blockState.getBlock();
                return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
            }
            return false;
        }),
        NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK));

        private static final Map<String, Target> nameMap;
        private final String name;
        private final Predicate<BlockState> predicate;

        private Target(String name, Predicate<BlockState> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public static Target byName(String name) {
            return nameMap.get(name);
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        static {
            nameMap = Arrays.stream(Target.values()).collect(Collectors.toMap(Target::getName, target -> target));
        }
    }
}

