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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class RandomChanceWithLootingLootCondition
implements LootCondition {
    private final float chance;
    private final float lootingMultiplier;

    private RandomChanceWithLootingLootCondition(float chance, float lootingMultiplier) {
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.get(LootContextParameters.KILLER_ENTITY);
        int i = 0;
        if (entity instanceof LivingEntity) {
            i = EnchantmentHelper.getLooting((LivingEntity)entity);
        }
        return lootContext.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
    }

    public static LootCondition.Builder builder(float chance, float lootingMultiplier) {
        return () -> new RandomChanceWithLootingLootCondition(chance, lootingMultiplier);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<RandomChanceWithLootingLootCondition> {
        protected Factory() {
            super(new Identifier("random_chance_with_looting"), RandomChanceWithLootingLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, RandomChanceWithLootingLootCondition randomChanceWithLootingLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(randomChanceWithLootingLootCondition.chance));
            jsonObject.addProperty("looting_multiplier", (Number)Float.valueOf(randomChanceWithLootingLootCondition.lootingMultiplier));
        }

        @Override
        public RandomChanceWithLootingLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceWithLootingLootCondition(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "looting_multiplier"));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

