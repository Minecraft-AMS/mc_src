/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class TreeFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final List<TreeDecorator> decorators;
    public final int baseHeight;
    public transient boolean field_21593;

    protected TreeFeatureConfig(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, List<TreeDecorator> decorators, int baseHeight) {
        this.trunkProvider = trunkProvider;
        this.leavesProvider = leavesProvider;
        this.decorators = decorators;
        this.baseHeight = baseHeight;
    }

    public void method_23916() {
        this.field_21593 = true;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(ops.createString("trunk_provider"), this.trunkProvider.serialize(ops)).put(ops.createString("leaves_provider"), this.leavesProvider.serialize(ops)).put(ops.createString("decorators"), ops.createList(this.decorators.stream().map(treeDecorator -> treeDecorator.serialize(ops)))).put(ops.createString("base_height"), ops.createInt(this.baseHeight));
        return new Dynamic(ops, ops.createMap((Map)builder.build()));
    }

    public static <T> TreeFeatureConfig deserialize(Dynamic<T> configDeserializer) {
        BlockStateProviderType<?> blockStateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)configDeserializer.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        BlockStateProviderType<?> blockStateProviderType2 = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)configDeserializer.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new TreeFeatureConfig((BlockStateProvider)blockStateProviderType.deserialize(configDeserializer.get("trunk_provider").orElseEmptyMap()), (BlockStateProvider)blockStateProviderType2.deserialize(configDeserializer.get("leaves_provider").orElseEmptyMap()), configDeserializer.get("decorators").asList(dynamic -> Registry.TREE_DECORATOR_TYPE.get(new Identifier((String)dynamic.get("type").asString().orElseThrow(RuntimeException::new))).method_23472((Dynamic<?>)dynamic)), configDeserializer.get("base_height").asInt(0));
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider leavesProvider;
        private List<TreeDecorator> decorators = Lists.newArrayList();
        private int baseHeight = 0;

        public Builder(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider) {
            this.trunkProvider = trunkProvider;
            this.leavesProvider = leavesProvider;
        }

        public Builder baseHeight(int baseHeight) {
            this.baseHeight = baseHeight;
            return this;
        }

        public TreeFeatureConfig build() {
            return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight);
        }
    }
}

