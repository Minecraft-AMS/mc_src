/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.data.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.PropertiesMap;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public class VariantsBlockStateSupplier
implements BlockStateSupplier {
    private final Block block;
    private final List<BlockStateVariant> variants;
    private final Set<Property<?>> definedProperties = Sets.newHashSet();
    private final List<BlockStateVariantMap> variantMaps = Lists.newArrayList();

    private VariantsBlockStateSupplier(Block block, List<BlockStateVariant> variants) {
        this.block = block;
        this.variants = variants;
    }

    public VariantsBlockStateSupplier coordinate(BlockStateVariantMap map) {
        map.getProperties().forEach(property -> {
            if (this.block.getStateManager().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + property + " is not defined for block " + this.block);
            }
            if (!this.definedProperties.add((Property<?>)property)) {
                throw new IllegalStateException("Values of property " + property + " already defined for block " + this.block);
            }
        });
        this.variantMaps.add(map);
        return this;
    }

    @Override
    public JsonElement get() {
        Stream<Object> stream = Stream.of(Pair.of((Object)PropertiesMap.empty(), this.variants));
        for (BlockStateVariantMap blockStateVariantMap : this.variantMaps) {
            Map<PropertiesMap, List<BlockStateVariant>> map = blockStateVariantMap.getVariants();
            stream = stream.flatMap(pair -> map.entrySet().stream().map(entry -> {
                PropertiesMap propertiesMap = ((PropertiesMap)pair.getFirst()).copyOf((PropertiesMap)entry.getKey());
                List<BlockStateVariant> list = VariantsBlockStateSupplier.intersect((List)pair.getSecond(), (List)entry.getValue());
                return Pair.of((Object)propertiesMap, list);
            }));
        }
        TreeMap map2 = new TreeMap();
        stream.forEach(pair -> map2.put(((PropertiesMap)pair.getFirst()).asString(), BlockStateVariant.toJson((List)pair.getSecond())));
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("variants", (JsonElement)Util.make(new JsonObject(), jsonObject -> map2.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).add(arg_0, arg_1))));
        return jsonObject2;
    }

    private static List<BlockStateVariant> intersect(List<BlockStateVariant> left, List<BlockStateVariant> right) {
        ImmutableList.Builder builder = ImmutableList.builder();
        left.forEach(blockStateVariant -> right.forEach(blockStateVariant2 -> builder.add((Object)BlockStateVariant.union(blockStateVariant, blockStateVariant2))));
        return builder.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static VariantsBlockStateSupplier create(Block block) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.of((Object)BlockStateVariant.create()));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant variant) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.of((Object)variant));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant ... variants) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.copyOf((Object[])variants));
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

