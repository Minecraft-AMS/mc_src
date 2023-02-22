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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KilledByCrossbowCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("killed_by_crossbow");
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
        handler.add(conditionsContainer);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(manager);
        if (handler != null) {
            handler.remove(conditionsContainer);
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
        EntityPredicate[] entityPredicates = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
        return new Conditions(entityPredicates, intRange);
    }

    public void trigger(ServerPlayerEntity player, Collection<Entity> victims, int amount) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.trigger(player, victims, amount);
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

        public void add(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void remove(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void trigger(ServerPlayerEntity player, Collection<Entity> victims, int i) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(player, victims, i)) continue;
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
        private final EntityPredicate[] victims;
        private final NumberRange.IntRange uniqueEntityTypes;

        public Conditions(EntityPredicate[] victims, NumberRange.IntRange intRange) {
            super(ID);
            this.victims = victims;
            this.uniqueEntityTypes = intRange;
        }

        public static Conditions create(EntityPredicate.Builder ... builders) {
            EntityPredicate[] entityPredicates = new EntityPredicate[builders.length];
            for (int i = 0; i < builders.length; ++i) {
                EntityPredicate.Builder builder = builders[i];
                entityPredicates[i] = builder.build();
            }
            return new Conditions(entityPredicates, NumberRange.IntRange.ANY);
        }

        public static Conditions create(NumberRange.IntRange intRange) {
            EntityPredicate[] entityPredicates = new EntityPredicate[]{};
            return new Conditions(entityPredicates, intRange);
        }

        public boolean matches(ServerPlayerEntity player, Collection<Entity> victims, int i) {
            if (this.victims.length > 0) {
                ArrayList list = Lists.newArrayList(victims);
                for (EntityPredicate entityPredicate : this.victims) {
                    boolean bl = false;
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Entity entity = (Entity)iterator.next();
                        if (!entityPredicate.test(player, entity)) continue;
                        iterator.remove();
                        bl = true;
                        break;
                    }
                    if (bl) continue;
                    return false;
                }
            }
            if (this.uniqueEntityTypes != NumberRange.IntRange.ANY) {
                HashSet set = Sets.newHashSet();
                for (Entity entity2 : victims) {
                    set.add(entity2.getType());
                }
                return this.uniqueEntityTypes.test(set.size()) && this.uniqueEntityTypes.test(i);
            }
            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("victims", EntityPredicate.serializeAll(this.victims));
            jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
            return jsonObject;
        }
    }
}

