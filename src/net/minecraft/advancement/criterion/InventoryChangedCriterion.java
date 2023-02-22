/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InventoryChangedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("inventory_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
        NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
        ItemPredicate[] itemPredicates = ItemPredicate.deserializeAll(jsonObject.get("items"));
        return new Conditions(intRange, intRange2, intRange3, itemPredicates);
    }

    public void trigger(ServerPlayerEntity player, PlayerInventory inventory) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(inventory));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange occupied;
        private final NumberRange.IntRange full;
        private final NumberRange.IntRange empty;
        private final ItemPredicate[] items;

        public Conditions(NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty, ItemPredicate[] items) {
            super(ID);
            this.occupied = occupied;
            this.full = full;
            this.empty = empty;
            this.items = items;
        }

        public static Conditions items(ItemPredicate ... items) {
            return new Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
        }

        public static Conditions items(ItemConvertible ... items) {
            ItemPredicate[] itemPredicates = new ItemPredicate[items.length];
            for (int i = 0; i < items.length; ++i) {
                itemPredicates[i] = new ItemPredicate(null, items[i].asItem(), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, EnchantmentPredicate.ARRAY_OF_ANY, EnchantmentPredicate.ARRAY_OF_ANY, null, NbtPredicate.ANY);
            }
            return Conditions.items(itemPredicates);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (!(this.occupied.isDummy() && this.full.isDummy() && this.empty.isDummy())) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.add("occupied", this.occupied.toJson());
                jsonObject2.add("full", this.full.toJson());
                jsonObject2.add("empty", this.empty.toJson());
                jsonObject.add("slots", (JsonElement)jsonObject2);
            }
            if (this.items.length > 0) {
                JsonArray jsonArray = new JsonArray();
                for (ItemPredicate itemPredicate : this.items) {
                    jsonArray.add(itemPredicate.toJson());
                }
                jsonObject.add("items", (JsonElement)jsonArray);
            }
            return jsonObject;
        }

        public boolean matches(PlayerInventory inventory) {
            int i = 0;
            int j = 0;
            int k = 0;
            ArrayList list = Lists.newArrayList((Object[])this.items);
            for (int l = 0; l < inventory.getInvSize(); ++l) {
                ItemStack itemStack = inventory.getInvStack(l);
                if (itemStack.isEmpty()) {
                    ++j;
                    continue;
                }
                ++k;
                if (itemStack.getCount() >= itemStack.getMaxCount()) {
                    ++i;
                }
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ItemPredicate itemPredicate = (ItemPredicate)iterator.next();
                    if (!itemPredicate.test(itemStack)) continue;
                    iterator.remove();
                }
            }
            if (!this.full.test(i)) {
                return false;
            }
            if (!this.empty.test(j)) {
                return false;
            }
            if (!this.occupied.test(k)) {
                return false;
            }
            return list.isEmpty();
        }
    }
}

