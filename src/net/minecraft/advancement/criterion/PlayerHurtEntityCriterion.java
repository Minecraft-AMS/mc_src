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
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("player_hurt_entity");
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
        DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new Conditions(damagePredicate, entityPredicate);
    }

    public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(player, entity, source, dealt, taken, blocked);
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

        public void handle(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(player, entity, source, dealt, taken, blocked)) continue;
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
        private final DamagePredicate damage;
        private final EntityPredicate entity;

        public Conditions(DamagePredicate damage, EntityPredicate entity) {
            super(ID);
            this.damage = damage;
            this.entity = entity;
        }

        public static Conditions create(DamagePredicate.Builder builder) {
            return new Conditions(builder.build(), EntityPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
            if (!this.damage.test(player, source, dealt, taken, blocked)) {
                return false;
            }
            return this.entity.test(player, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("damage", this.damage.serialize());
            jsonObject.add("entity", this.entity.serialize());
            return jsonObject;
        }
    }
}

