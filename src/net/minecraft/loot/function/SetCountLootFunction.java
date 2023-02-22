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
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;

public class SetCountLootFunction
extends ConditionalLootFunction {
    private final LootTableRange countRange;

    private SetCountLootFunction(LootCondition[] conditions, LootTableRange countRange) {
        super(conditions);
        this.countRange = countRange;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        stack.setCount(this.countRange.next(context.getRandom()));
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(LootTableRange countRange) {
        return SetCountLootFunction.builder((LootCondition[] conditions) -> new SetCountLootFunction((LootCondition[])conditions, countRange));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetCountLootFunction> {
        protected Factory() {
            super(new Identifier("set_count"), SetCountLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setCountLootFunction, jsonSerializationContext);
            jsonObject.add("count", LootTableRanges.toJson(setCountLootFunction.countRange, jsonSerializationContext));
        }

        @Override
        public SetCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootTableRange lootTableRange = LootTableRanges.fromJson(jsonObject.get("count"), jsonDeserializationContext);
            return new SetCountLootFunction(lootConditions, lootTableRange);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

