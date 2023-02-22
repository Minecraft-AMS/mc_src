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
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EnterBlockCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("enter_block");
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
        handler.addConditon(conditionsContainer);
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
        Block block = null;
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        HashMap map = null;
        if (jsonObject.has("state")) {
            if (block == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (Map.Entry entry : JsonHelper.getObject(jsonObject, "state").entrySet()) {
                Property<?> property = stateManager.getProperty((String)entry.getKey());
                if (property == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + Registry.BLOCK.getId(block) + "'");
                }
                String string = JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey());
                Optional<?> optional = property.parse(string);
                if (optional.isPresent()) {
                    if (map == null) {
                        map = Maps.newHashMap();
                    }
                    map.put(property, optional.get());
                    continue;
                }
                throw new JsonSyntaxException("Invalid block state value '" + string + "' for property '" + (String)entry.getKey() + "' on block '" + Registry.BLOCK.getId(block) + "'");
            }
        }
        return new Conditions(block, map);
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        Handler handler = this.handlers.get(player.getAdvancementTracker());
        if (handler != null) {
            handler.handle(state);
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

        public void addConditon(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void removeCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void handle(BlockState state) {
            List list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(state)) continue;
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
        private final Block block;
        private final Map<Property<?>, Object> state;

        public Conditions(@Nullable Block block, @Nullable Map<Property<?>, Object> state) {
            super(ID);
            this.block = block;
            this.state = state;
        }

        public static Conditions block(Block block) {
            return new Conditions(block, null);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
                if (this.state != null && !this.state.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    for (Map.Entry<Property<?>, Object> entry : this.state.entrySet()) {
                        jsonObject2.addProperty(entry.getKey().getName(), Util.getValueAsString(entry.getKey(), entry.getValue()));
                    }
                    jsonObject.add("state", (JsonElement)jsonObject2);
                }
            }
            return jsonObject;
        }

        public boolean matches(BlockState state) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            }
            if (this.state != null) {
                for (Map.Entry<Property<?>, Object> entry : this.state.entrySet()) {
                    if (state.get(entry.getKey()) == entry.getValue()) continue;
                    return false;
                }
            }
            return true;
        }
    }
}

