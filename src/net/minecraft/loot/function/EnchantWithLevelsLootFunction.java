/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class EnchantWithLevelsLootFunction
extends ConditionalLootFunction {
    private final LootTableRange range;
    private final boolean treasureEnchantmentsAllowed;

    private EnchantWithLevelsLootFunction(LootCondition[] conditions, LootTableRange range, boolean treasureEnchantmentsAllowed) {
        super(conditions);
        this.range = range;
        this.treasureEnchantmentsAllowed = treasureEnchantmentsAllowed;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        return EnchantmentHelper.enchant(random, stack, this.range.next(random), this.treasureEnchantmentsAllowed);
    }

    public static Builder builder(LootTableRange range) {
        return new Builder(range);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<EnchantWithLevelsLootFunction> {
        public Factory() {
            super(new Identifier("enchant_with_levels"), EnchantWithLevelsLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, EnchantWithLevelsLootFunction enchantWithLevelsLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, enchantWithLevelsLootFunction, jsonSerializationContext);
            jsonObject.add("levels", LootTableRanges.toJson(enchantWithLevelsLootFunction.range, jsonSerializationContext));
            jsonObject.addProperty("treasure", Boolean.valueOf(enchantWithLevelsLootFunction.treasureEnchantmentsAllowed));
        }

        @Override
        public EnchantWithLevelsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootTableRange lootTableRange = LootTableRanges.fromJson(jsonObject.get("levels"), jsonDeserializationContext);
            boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
            return new EnchantWithLevelsLootFunction(lootConditions, lootTableRange, bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final LootTableRange range;
        private boolean treasureEnchantmentsAllowed;

        public Builder(LootTableRange range) {
            this.range = range;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder allowTreasureEnchantments() {
            this.treasureEnchantmentsAllowed = true;
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantWithLevelsLootFunction(this.getConditions(), this.range, this.treasureEnchantmentsAllowed);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

