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
 *  org.jetbrains.annotations.Nullable
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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BredAnimalsCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("bred_animals");
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
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("parent"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("partner"));
        EntityPredicate entityPredicate3 = EntityPredicate.fromJson(jsonObject.get("child"));
        return new Conditions(entityPredicate, entityPredicate2, entityPredicate3);
    }

    public void trigger(ServerPlayerEntity player, AnimalEntity parent, @Nullable AnimalEntity partner, @Nullable PassiveEntity child) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(player, parent, partner, child);
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

        public void handle(ServerPlayerEntity parent1, AnimalEntity parent2, @Nullable AnimalEntity child, @Nullable PassiveEntity passiveEntity) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(parent1, parent2, child, passiveEntity)) continue;
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
        private final EntityPredicate parent;
        private final EntityPredicate partner;
        private final EntityPredicate child;

        public Conditions(EntityPredicate parent, EntityPredicate partner, EntityPredicate child) {
            super(ID);
            this.parent = parent;
            this.partner = partner;
            this.child = child;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public static Conditions create(EntityPredicate.Builder builder) {
            return new Conditions(builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity child, AnimalEntity parent, @Nullable AnimalEntity partner, @Nullable PassiveEntity passiveEntity) {
            if (!this.child.test(child, passiveEntity)) {
                return false;
            }
            return this.parent.test(child, parent) && this.partner.test(child, partner) || this.parent.test(child, partner) && this.partner.test(child, parent);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("parent", this.parent.serialize());
            jsonObject.add("partner", this.partner.serialize());
            jsonObject.add("child", this.child.serialize());
            return jsonObject;
        }
    }
}

