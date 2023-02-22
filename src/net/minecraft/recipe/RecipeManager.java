/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class RecipeManager
extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = ImmutableMap.of();
    private Map<Identifier, Recipe<?>> recipesById = ImmutableMap.of();
    private boolean errored;

    public RecipeManager() {
        super(GSON, "recipes");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        this.errored = false;
        HashMap map2 = Maps.newHashMap();
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> entry2 : map.entrySet()) {
            Identifier identifier = entry2.getKey();
            try {
                Recipe<?> recipe = RecipeManager.deserialize(identifier, JsonHelper.asObject(entry2.getValue(), "top element"));
                map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder()).put((Object)identifier, recipe);
                builder.put((Object)identifier, recipe);
            }
            catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading recipe {}", (Object)identifier, (Object)runtimeException);
            }
        }
        this.recipes = (Map)map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((ImmutableMap.Builder)entry.getValue()).build()));
        this.recipesById = builder.build();
        LOGGER.info("Loaded {} recipes", (Object)map2.size());
    }

    public boolean isErrored() {
        return this.errored;
    }

    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> type, C inventory, World world) {
        return this.getAllOfType(type).values().stream().flatMap(recipe -> type.match(recipe, world, inventory).stream()).findFirst();
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> listAllOfType(RecipeType<T> type) {
        return this.getAllOfType(type).values().stream().map(recipe -> recipe).collect(Collectors.toList());
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> type, C inventory, World world) {
        return this.getAllOfType(type).values().stream().flatMap(recipe -> type.match(recipe, world, inventory).stream()).sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey())).collect(Collectors.toList());
    }

    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> type) {
        return this.recipes.getOrDefault(type, Collections.emptyMap());
    }

    public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> type, C inventory, World world) {
        Optional<T> optional = this.getFirstMatch(type, inventory, world);
        if (optional.isPresent()) {
            return ((Recipe)optional.get()).getRemainder(inventory);
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            defaultedList.set(i, inventory.getStack(i));
        }
        return defaultedList;
    }

    public Optional<? extends Recipe<?>> get(Identifier id) {
        return Optional.ofNullable(this.recipesById.get(id));
    }

    public Collection<Recipe<?>> values() {
        return this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.recipes.values().stream().flatMap(map -> map.keySet().stream());
    }

    public static Recipe<?> deserialize(Identifier id, JsonObject json) {
        String string = JsonHelper.getString(json, "type");
        return Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")).read(id, json);
    }

    public void setRecipes(Iterable<Recipe<?>> recipes) {
        this.errored = false;
        HashMap map = Maps.newHashMap();
        ImmutableMap.Builder builder = ImmutableMap.builder();
        recipes.forEach(recipe -> {
            Map map2 = map.computeIfAbsent(recipe.getType(), t -> Maps.newHashMap());
            Identifier identifier = recipe.getId();
            Recipe recipe2 = map2.put(identifier, recipe);
            builder.put((Object)identifier, recipe);
            if (recipe2 != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + identifier);
            }
        });
        this.recipes = ImmutableMap.copyOf((Map)map);
        this.recipesById = builder.build();
    }
}

