/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("enchanted_item");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
        return new Conditions(itemPredicate, intRange);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(stack, levels));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final NumberRange.IntRange levels;

        public Conditions(ItemPredicate item, NumberRange.IntRange intRange) {
            super(ID);
            this.item = item;
            this.levels = intRange;
        }

        public static Conditions any() {
            return new Conditions(ItemPredicate.ANY, NumberRange.IntRange.ANY);
        }

        public boolean matches(ItemStack stack, int levels) {
            if (!this.item.test(stack)) {
                return false;
            }
            return this.levels.test(levels);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("levels", this.levels.toJson());
            return jsonObject;
        }
    }
}

