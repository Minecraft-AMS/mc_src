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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class ChangedDimensionCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("changed_dimension");
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
        DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
        DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
        return new Conditions(dimensionType, dimensionType2);
    }

    public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(from, to);
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

        public void handle(DimensionType from, DimensionType to) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(from, to)) continue;
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
        @Nullable
        private final DimensionType from;
        @Nullable
        private final DimensionType to;

        public Conditions(@Nullable DimensionType from, @Nullable DimensionType to) {
            super(ID);
            this.from = from;
            this.to = to;
        }

        public static Conditions to(DimensionType dimensionType) {
            return new Conditions(null, dimensionType);
        }

        public boolean matches(DimensionType from, DimensionType to) {
            if (this.from != null && this.from != from) {
                return false;
            }
            return this.to == null || this.to == to;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.from != null) {
                jsonObject.addProperty("from", DimensionType.getId(this.from).toString());
            }
            if (this.to != null) {
                jsonObject.addProperty("to", DimensionType.getId(this.to).toString());
            }
            return jsonObject;
        }
    }
}

