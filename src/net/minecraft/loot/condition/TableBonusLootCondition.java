/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class TableBonusLootCondition
implements LootCondition {
    private final Enchantment enchantment;
    private final float[] chances;

    private TableBonusLootCondition(Enchantment enchantment, float[] chances) {
        this.enchantment = enchantment;
        this.chances = chances;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack itemStack = lootContext.get(LootContextParameters.TOOL);
        int i = itemStack != null ? EnchantmentHelper.getLevel(this.enchantment, itemStack) : 0;
        float f = this.chances[Math.min(i, this.chances.length - 1)];
        return lootContext.getRandom().nextFloat() < f;
    }

    public static LootCondition.Builder builder(Enchantment enchantment, float ... chances) {
        return () -> new TableBonusLootCondition(enchantment, chances);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<TableBonusLootCondition> {
        public Factory() {
            super(new Identifier("table_bonus"), TableBonusLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, TableBonusLootCondition tableBonusLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(tableBonusLootCondition.enchantment).toString());
            jsonObject.add("chances", jsonSerializationContext.serialize((Object)tableBonusLootCondition.chances));
        }

        @Override
        public TableBonusLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
            Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(identifier).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + identifier));
            float[] fs = JsonHelper.deserialize(jsonObject, "chances", jsonDeserializationContext, float[].class);
            return new TableBonusLootCondition(enchantment, fs);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

