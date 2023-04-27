/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.slf4j.Logger;

public class ReferenceLootCondition
implements LootCondition {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Identifier id;

    ReferenceLootCondition(Identifier id) {
        this.id = id;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.REFERENCE;
    }

    @Override
    public void validate(LootTableReporter reporter) {
        LootDataKey<LootCondition> lootDataKey = new LootDataKey<LootCondition>(LootDataType.PREDICATES, this.id);
        if (reporter.isInStack(lootDataKey)) {
            reporter.report("Condition " + this.id + " is recursively called");
            return;
        }
        LootCondition.super.validate(reporter);
        reporter.getDataLookup().getElementOptional(lootDataKey).ifPresentOrElse(predicate -> predicate.validate(reporter.makeChild(".{" + this.id + "}", lootDataKey)), () -> reporter.report("Unknown condition table called " + this.id));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean test(LootContext lootContext) {
        LootCondition lootCondition = lootContext.getDataLookup().getElement(LootDataType.PREDICATES, this.id);
        if (lootCondition == null) {
            LOGGER.warn("Tried using unknown condition table called {}", (Object)this.id);
            return false;
        }
        LootContext.Entry<LootCondition> entry = LootContext.predicate(lootCondition);
        if (lootContext.markActive(entry)) {
            try {
                boolean bl = lootCondition.test(lootContext);
                return bl;
            }
            finally {
                lootContext.markInactive(entry);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return false;
    }

    public static LootCondition.Builder builder(Identifier id) {
        return () -> new ReferenceLootCondition(id);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<ReferenceLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, ReferenceLootCondition referenceLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("name", referenceLootCondition.id.toString());
        }

        @Override
        public ReferenceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new ReferenceLootCondition(identifier);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

