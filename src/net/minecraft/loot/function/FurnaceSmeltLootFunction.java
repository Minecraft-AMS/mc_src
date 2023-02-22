/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    private FurnaceSmeltLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ItemStack itemStack;
        if (stack.isEmpty()) {
            return stack;
        }
        Optional<SmeltingRecipe> optional = context.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, new BasicInventory(stack), context.getWorld());
        if (optional.isPresent() && !(itemStack = optional.get().getOutput()).isEmpty()) {
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(stack.getCount());
            return itemStack2;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)stack);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return FurnaceSmeltLootFunction.builder(FurnaceSmeltLootFunction::new);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<FurnaceSmeltLootFunction> {
        protected Factory() {
            super(new Identifier("furnace_smelt"), FurnaceSmeltLootFunction.class);
        }

        @Override
        public FurnaceSmeltLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new FurnaceSmeltLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

