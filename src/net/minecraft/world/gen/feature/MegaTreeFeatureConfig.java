/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MegaTreeFeatureConfig
extends TreeFeatureConfig {
    public final int heightInterval;
    public final int crownHeight;

    protected MegaTreeFeatureConfig(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2, List<TreeDecorator> list, int i, int heightInterval, int crownHeight) {
        super(blockStateProvider, blockStateProvider2, list, i);
        this.heightInterval = heightInterval;
        this.crownHeight = crownHeight;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        Dynamic dynamic = new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("height_interval"), (Object)ops.createInt(this.heightInterval), (Object)ops.createString("crown_height"), (Object)ops.createInt(this.crownHeight))));
        return dynamic.merge(super.serialize(ops));
    }

    public static <T> MegaTreeFeatureConfig deserialize(Dynamic<T> dynamic) {
        TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
        return new MegaTreeFeatureConfig(treeFeatureConfig.trunkProvider, treeFeatureConfig.leavesProvider, treeFeatureConfig.decorators, treeFeatureConfig.baseHeight, dynamic.get("height_interval").asInt(0), dynamic.get("crown_height").asInt(0));
    }

    public static class Builder
    extends TreeFeatureConfig.Builder {
        private List<TreeDecorator> field_21234 = ImmutableList.of();
        private int field_21235;
        private int heightInterval;
        private int crownHeight;

        public Builder(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2) {
            super(blockStateProvider, blockStateProvider2);
        }

        public Builder treeDecorators(List<TreeDecorator> list) {
            this.field_21234 = list;
            return this;
        }

        @Override
        public Builder baseHeight(int i) {
            this.field_21235 = i;
            return this;
        }

        public Builder heightInterval(int heightInterval) {
            this.heightInterval = heightInterval;
            return this;
        }

        public Builder crownHeight(int crownHeight) {
            this.crownHeight = crownHeight;
            return this;
        }

        @Override
        public MegaTreeFeatureConfig build() {
            return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_21234, this.field_21235, this.heightInterval, this.crownHeight);
        }

        @Override
        public /* synthetic */ TreeFeatureConfig build() {
            return this.build();
        }

        @Override
        public /* synthetic */ TreeFeatureConfig.Builder baseHeight(int baseHeight) {
            return this.baseHeight(baseHeight);
        }
    }
}

