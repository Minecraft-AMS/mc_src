/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemDurabilityChangedCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("item_durability_changed");
    private final Map<PlayerAdvancementTracker, Handler> handlers = Maps.newHashMap();

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(manager);
        if (handler == null) {
            handler = new Handler(manager);
            this.handlers.put(manager, handler);
        }
        handler.addCondition(conditionsContainer);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(manager);
        if (handler != null) {
            handler.removeCondition(conditionsContainer);
            if (handler.isEmpty()) {
                this.handlers.remove(manager);
            }
        }
    }

    @Override
    public void endTracking(PlayerAdvancementTracker tracker) {
        this.handlers.remove(tracker);
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
        return new Conditions(itemPredicate, intRange, intRange2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, int damage) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(stack, damage);
        }
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    static class Handler {
        private final PlayerAdvancementTracker manager;
        private final Set<Criterion.ConditionsContainer<Conditions>> conditions = Sets.newHashSet();

        public Handler(PlayerAdvancementTracker manager) {
            this.manager = manager;
        }

        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }

        public void addCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void removeCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void handle(ItemStack stack, int durability) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(stack, durability)) continue;
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(conditionsContainer);
            }
            if (list != null) {
                for (Criterion.ConditionsContainer<Conditions> conditionsContainer : list) {
                    conditionsContainer.apply(this.manager);
                }
            }
        }
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final NumberRange.IntRange durability;
        private final NumberRange.IntRange delta;

        public Conditions(ItemPredicate item, NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
            super(ID);
            this.item = item;
            this.durability = intRange;
            this.delta = intRange2;
        }

        public static Conditions create(ItemPredicate item, NumberRange.IntRange intRange) {
            return new Conditions(item, intRange, NumberRange.IntRange.ANY);
        }

        public boolean matches(ItemStack item, int durability) {
            if (!this.item.test(item)) {
                return false;
            }
            if (!this.durability.test(item.getMaxDamage() - durability)) {
                return false;
            }
            return this.delta.test(item.getDamage() - durability);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("durability", this.durability.toJson());
            jsonObject.add("delta", this.delta.toJson());
            return jsonObject;
        }
    }
}

