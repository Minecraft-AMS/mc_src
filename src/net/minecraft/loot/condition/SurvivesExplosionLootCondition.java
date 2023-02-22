/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.random.Random;

public class SurvivesExplosionLootCondition
implements LootCondition {
    static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();

    private SurvivesExplosionLootCondition() {
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.SURVIVES_EXPLOSION;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Float float_ = lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
        if (float_ != null) {
            Random random = lootContext.getRandom();
            float f = 1.0f / float_.floatValue();
            return random.nextFloat() <= f;
        }
        return true;
    }

    public static LootCondition.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<SurvivesExplosionLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, SurvivesExplosionLootCondition survivesExplosionLootCondition, JsonSerializationContext jsonSerializationContext) {
        }

        @Override
        public SurvivesExplosionLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

