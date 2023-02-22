/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

public class MatchToolLootCondition
implements LootCondition {
    private final ItemPredicate predicate;

    public MatchToolLootCondition(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack itemStack = lootContext.get(LootContextParameters.TOOL);
        return itemStack != null && this.predicate.test(itemStack);
    }

    public static LootCondition.Builder builder(ItemPredicate.Builder predicate) {
        return () -> new MatchToolLootCondition(predicate.build());
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<MatchToolLootCondition> {
        protected Factory() {
            super(new Identifier("match_tool"), MatchToolLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, MatchToolLootCondition matchToolLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", matchToolLootCondition.predicate.toJson());
        }

        @Override
        public MatchToolLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("predicate"));
            return new MatchToolLootCondition(itemPredicate);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

