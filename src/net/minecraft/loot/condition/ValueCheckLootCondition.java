/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class ValueCheckLootCondition
implements LootCondition {
    final LootNumberProvider value;
    final BoundedIntUnaryOperator range;

    ValueCheckLootCondition(LootNumberProvider value, BoundedIntUnaryOperator range) {
        this.value = value;
        this.range = range;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.VALUE_CHECK;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Sets.union(this.value.getRequiredParameters(), this.range.getRequiredParameters());
    }

    @Override
    public boolean test(LootContext lootContext) {
        return this.range.test(lootContext, this.value.nextInt(lootContext));
    }

    public static LootCondition.Builder builder(LootNumberProvider value, BoundedIntUnaryOperator range) {
        return () -> new ValueCheckLootCondition(value, range);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<ValueCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, ValueCheckLootCondition valueCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("value", jsonSerializationContext.serialize((Object)valueCheckLootCondition.value));
            jsonObject.add("range", jsonSerializationContext.serialize((Object)valueCheckLootCondition.range));
        }

        @Override
        public ValueCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, LootNumberProvider.class);
            BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "range", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new ValueCheckLootCondition(lootNumberProvider, boundedIntUnaryOperator);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

