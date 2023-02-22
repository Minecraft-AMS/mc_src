/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LimitCountLootFunction
extends ConditionalLootFunction {
    private final BoundedIntUnaryOperator limit;

    private LimitCountLootFunction(LootCondition[] conditions, BoundedIntUnaryOperator limit) {
        super(conditions);
        this.limit = limit;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        int i = this.limit.applyAsInt(stack.getCount());
        stack.setCount(i);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator limit) {
        return LimitCountLootFunction.builder((LootCondition[] conditions) -> new LimitCountLootFunction((LootCondition[])conditions, limit));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<LimitCountLootFunction> {
        protected Factory() {
            super(new Identifier("limit_count"), LimitCountLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, LimitCountLootFunction limitCountLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, limitCountLootFunction, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize((Object)limitCountLootFunction.limit));
        }

        @Override
        public LimitCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "limit", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new LimitCountLootFunction(lootConditions, boundedIntUnaryOperator);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

