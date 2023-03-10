/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = ImmutableMap.of();
    private List<RecipeResultCollection> orderedResults = ImmutableList.of();

    public void reload(Iterable<Recipe<?>> recipes) {
        Map<RecipeBookGroup, List<List<Recipe<?>>>> map = ClientRecipeBook.toGroupedMap(recipes);
        HashMap map2 = Maps.newHashMap();
        ImmutableList.Builder builder = ImmutableList.builder();
        map.forEach((recipeBookGroup, list) -> map2.put(recipeBookGroup, (List)list.stream().map(RecipeResultCollection::new).peek(arg_0 -> ((ImmutableList.Builder)builder).add(arg_0)).collect(ImmutableList.toImmutableList())));
        RecipeBookGroup.SEARCH_MAP.forEach((recipeBookGroup2, list) -> map2.put(recipeBookGroup2, (List)list.stream().flatMap(recipeBookGroup -> ((List)map2.getOrDefault(recipeBookGroup, ImmutableList.of())).stream()).collect(ImmutableList.toImmutableList())));
        this.resultsByGroup = ImmutableMap.copyOf((Map)map2);
        this.orderedResults = builder.build();
    }

    private static Map<RecipeBookGroup, List<List<Recipe<?>>>> toGroupedMap(Iterable<Recipe<?>> recipes) {
        HashMap map = Maps.newHashMap();
        HashBasedTable table = HashBasedTable.create();
        for (Recipe<?> recipe : recipes) {
            if (recipe.isIgnoredInRecipeBook() || recipe.isEmpty()) continue;
            RecipeBookGroup recipeBookGroup = ClientRecipeBook.getGroupForRecipe(recipe);
            String string = recipe.getGroup();
            if (string.isEmpty()) {
                map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList()).add(ImmutableList.of(recipe));
                continue;
            }
            List list = (List)table.get((Object)recipeBookGroup, (Object)string);
            if (list == null) {
                list = Lists.newArrayList();
                table.put((Object)recipeBookGroup, (Object)string, (Object)list);
                map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList()).add(list);
            }
            list.add(recipe);
        }
        return map;
    }

    private static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
        RecipeType<?> recipeType = recipe.getType();
        if (recipeType == RecipeType.CRAFTING) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup itemGroup = itemStack.getItem().getGroup();
            if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
                return RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
            }
            if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
                return RecipeBookGroup.CRAFTING_EQUIPMENT;
            }
            if (itemGroup == ItemGroup.REDSTONE) {
                return RecipeBookGroup.CRAFTING_REDSTONE;
            }
            return RecipeBookGroup.CRAFTING_MISC;
        }
        if (recipeType == RecipeType.SMELTING) {
            if (recipe.getOutput().getItem().isFood()) {
                return RecipeBookGroup.FURNACE_FOOD;
            }
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.FURNACE_BLOCKS;
            }
            return RecipeBookGroup.FURNACE_MISC;
        }
        if (recipeType == RecipeType.BLASTING) {
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.BLAST_FURNACE_BLOCKS;
            }
            return RecipeBookGroup.BLAST_FURNACE_MISC;
        }
        if (recipeType == RecipeType.SMOKING) {
            return RecipeBookGroup.SMOKER_FOOD;
        }
        if (recipeType == RecipeType.STONECUTTING) {
            return RecipeBookGroup.STONECUTTER;
        }
        if (recipeType == RecipeType.CAMPFIRE_COOKING) {
            return RecipeBookGroup.CAMPFIRE;
        }
        if (recipeType == RecipeType.SMITHING) {
            return RecipeBookGroup.SMITHING;
        }
        LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> Registry.RECIPE_TYPE.getId(recipe.getType())), LogUtils.defer(recipe::getId));
        return RecipeBookGroup.UNKNOWN;
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
        return this.resultsByGroup.getOrDefault((Object)category, Collections.emptyList());
    }
}

