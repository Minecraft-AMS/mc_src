/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;

public class BlockStatePropertyLootCondition
implements LootCondition {
    private final Block block;
    private final Map<Property<?>, Object> properties;
    private final Predicate<BlockState> predicate;

    private BlockStatePropertyLootCondition(Block block, Map<Property<?>, Object> properties) {
        this.block = block;
        this.properties = ImmutableMap.copyOf(properties);
        this.predicate = BlockStatePropertyLootCondition.getBlockState(block, properties);
    }

    private static Predicate<BlockState> getBlockState(Block block, Map<Property<?>, Object> properties) {
        int i = properties.size();
        if (i == 0) {
            return blockState -> blockState.getBlock() == block;
        }
        if (i == 1) {
            Map.Entry<Property<?>, Object> entry = properties.entrySet().iterator().next();
            Property<?> property = entry.getKey();
            Object object = entry.getValue();
            return blockState -> blockState.getBlock() == block && object.equals(blockState.get(property));
        }
        Predicate<BlockState> predicate = blockState -> blockState.getBlock() == block;
        for (Map.Entry<Property<?>, Object> entry2 : properties.entrySet()) {
            Property<?> property2 = entry2.getKey();
            Object object2 = entry2.getValue();
            predicate = predicate.and(blockState -> object2.equals(blockState.get(property2)));
        }
        return predicate;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
        return blockState != null && this.predicate.test(blockState);
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<BlockStatePropertyLootCondition> {
        private static <T extends Comparable<T>> String getPropertyValueString(Property<T> property, Object value) {
            return property.name((Comparable)value);
        }

        protected Factory() {
            super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, BlockStatePropertyLootCondition blockStatePropertyLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registry.BLOCK.getId(blockStatePropertyLootCondition.block).toString());
            JsonObject jsonObject2 = new JsonObject();
            blockStatePropertyLootCondition.properties.forEach((property, value) -> jsonObject2.addProperty(property.getName(), Factory.getPropertyValueString(property, value)));
            jsonObject.add("properties", (JsonElement)jsonObject2);
        }

        @Override
        public BlockStatePropertyLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            Block block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Can't find block " + identifier));
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            HashMap map = Maps.newHashMap();
            if (jsonObject.has("properties")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "properties");
                jsonObject2.entrySet().forEach(entry -> {
                    String string = (String)entry.getKey();
                    Property<?> property = stateManager.getProperty(string);
                    if (property == null) {
                        throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(block) + " does not have property '" + string + "'");
                    }
                    String string2 = JsonHelper.asString((JsonElement)entry.getValue(), "value");
                    Object object = property.parse(string2).orElseThrow(() -> new IllegalArgumentException("Block " + Registry.BLOCK.getId(block) + " property '" + string + "' does not have value '" + string2 + "'"));
                    map.put(property, object);
                });
            }
            return new BlockStatePropertyLootCondition(block, map);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final Block block;
        private final Set<Property<?>> availableProperties;
        private final Map<Property<?>, Object> propertyValues = Maps.newHashMap();

        public Builder(Block block) {
            this.block = block;
            this.availableProperties = Sets.newIdentityHashSet();
            this.availableProperties.addAll(block.getStateManager().getProperties());
        }

        public <T extends Comparable<T>> Builder withBlockStateProperty(Property<T> property, T value) {
            if (!this.availableProperties.contains(property)) {
                throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(this.block) + " does not have property '" + property + "'");
            }
            if (!property.getValues().contains(value)) {
                throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(this.block) + " property '" + property + "' does not have value '" + value + "'");
            }
            this.propertyValues.put(property, value);
            return this;
        }

        @Override
        public LootCondition build() {
            return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
        }
    }
}

