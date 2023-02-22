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
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion
implements Criterion<Conditions> {
    private final Map<PlayerAdvancementTracker, Handler> handlers = Maps.newHashMap();
    private final Identifier id;

    public OnKilledCriterion(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Identifier getId() {
        return this.id;
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
        return new Conditions(this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow")));
    }

    public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(player, entity, source);
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

        public void handle(ServerPlayerEntity player, Entity entity, DamageSource killingBlow) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().test(player, entity, killingBlow)) continue;
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
        private final EntityPredicate entity;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier identifier, EntityPredicate entity, DamageSourcePredicate killingBlow) {
            super(identifier);
            this.entity = entity;
            this.killingBlow = killingBlow;
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder builder) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity() {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
        }

        public static Conditions createEntityKilledPlayer() {
            return new Conditions(Criterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }

        public boolean test(ServerPlayerEntity player, Entity entity, DamageSource killingBlow) {
            if (!this.killingBlow.test(player, killingBlow)) {
                return false;
            }
            return this.entity.test(player, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.serialize());
            jsonObject.add("killing_blow", this.killingBlow.serialize());
            return jsonObject;
        }
    }
}

