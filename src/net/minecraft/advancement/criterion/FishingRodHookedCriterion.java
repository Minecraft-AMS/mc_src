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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("fishing_rod_hooked");
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
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(itemPredicate, entityPredicate, itemPredicate2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(player, rodStack, bobber, fishingLoots);
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

        public void handle(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity entity, Collection<ItemStack> collection) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(player, rodStack, entity, collection)) continue;
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
        private final ItemPredicate rod;
        private final EntityPredicate bobber;
        private final ItemPredicate item;

        public Conditions(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
            super(ID);
            this.rod = rod;
            this.bobber = bobber;
            this.item = item;
        }

        public static Conditions create(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
            return new Conditions(rod, bobber, item);
        }

        public boolean matches(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity bobber, Collection<ItemStack> collection) {
            if (!this.rod.test(rodStack)) {
                return false;
            }
            if (!this.bobber.test(player, bobber.hookedEntity)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                ItemEntity itemEntity;
                boolean bl = false;
                if (bobber.hookedEntity instanceof ItemEntity && this.item.test((itemEntity = (ItemEntity)bobber.hookedEntity).getStack())) {
                    bl = true;
                }
                for (ItemStack itemStack : collection) {
                    if (!this.item.test(itemStack)) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rod", this.rod.toJson());
            jsonObject.add("entity", this.bobber.serialize());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

