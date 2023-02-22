/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Enchantment> enchantments;

    private EnchantRandomlyLootFunction(LootCondition[] conditions, Collection<Enchantment> enchantments) {
        super(conditions);
        this.enchantments = ImmutableList.copyOf(enchantments);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Enchantment enchantment2;
        Random random = context.getRandom();
        if (this.enchantments.isEmpty()) {
            ArrayList list = Lists.newArrayList();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (stack.getItem() != Items.BOOK && !enchantment.isAcceptableItem(stack)) continue;
                list.add(enchantment);
            }
            if (list.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)stack);
                return stack;
            }
            enchantment2 = (Enchantment)list.get(random.nextInt(list.size()));
        } else {
            enchantment2 = this.enchantments.get(random.nextInt(this.enchantments.size()));
        }
        int i = MathHelper.nextInt(random, enchantment2.getMinimumLevel(), enchantment2.getMaximumLevel());
        if (stack.getItem() == Items.BOOK) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new InfoEnchantment(enchantment2, i));
        } else {
            stack.addEnchantment(enchantment2, i);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return EnchantRandomlyLootFunction.builder(conditions -> new EnchantRandomlyLootFunction((LootCondition[])conditions, (Collection<Enchantment>)ImmutableList.of()));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<EnchantRandomlyLootFunction> {
        public Factory() {
            super(new Identifier("enchant_randomly"), EnchantRandomlyLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, EnchantRandomlyLootFunction enchantRandomlyLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
            if (!enchantRandomlyLootFunction.enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment enchantment : enchantRandomlyLootFunction.enchantments) {
                    Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
                    if (identifier == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }
                    jsonArray.add((JsonElement)new JsonPrimitive(identifier.toString()));
                }
                jsonObject.add("enchantments", (JsonElement)jsonArray);
            }
        }

        @Override
        public EnchantRandomlyLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            ArrayList list = Lists.newArrayList();
            if (jsonObject.has("enchantments")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "enchantments");
                for (JsonElement jsonElement : jsonArray) {
                    String string = JsonHelper.asString(jsonElement, "enchantment");
                    Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    list.add(enchantment);
                }
            }
            return new EnchantRandomlyLootFunction(lootConditions, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}
