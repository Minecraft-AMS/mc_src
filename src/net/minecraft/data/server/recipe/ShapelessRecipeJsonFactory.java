/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeJsonFactory {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item output;
    private final int outputCount;
    private final List<Ingredient> inputs = Lists.newArrayList();
    private final Advancement.Task builder = Advancement.Task.create();
    private String group;

    public ShapelessRecipeJsonFactory(ItemConvertible itemProvider, int outputCount) {
        this.output = itemProvider.asItem();
        this.outputCount = outputCount;
    }

    public static ShapelessRecipeJsonFactory create(ItemConvertible output) {
        return new ShapelessRecipeJsonFactory(output, 1);
    }

    public static ShapelessRecipeJsonFactory create(ItemConvertible output, int outputCount) {
        return new ShapelessRecipeJsonFactory(output, outputCount);
    }

    public ShapelessRecipeJsonFactory input(Tag<Item> tag) {
        return this.input(Ingredient.fromTag(tag));
    }

    public ShapelessRecipeJsonFactory input(ItemConvertible itemProvider) {
        return this.input(itemProvider, 1);
    }

    public ShapelessRecipeJsonFactory input(ItemConvertible itemProvider, int size) {
        for (int i = 0; i < size; ++i) {
            this.input(Ingredient.ofItems(itemProvider));
        }
        return this;
    }

    public ShapelessRecipeJsonFactory input(Ingredient ingredient) {
        return this.input(ingredient, 1);
    }

    public ShapelessRecipeJsonFactory input(Ingredient ingredient, int size) {
        for (int i = 0; i < size; ++i) {
            this.inputs.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }

    public ShapelessRecipeJsonFactory group(String group) {
        this.group = group;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, Registry.ITEM.getId(this.output));
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
        Identifier identifier = Registry.ITEM.getId(this.output);
        if (new Identifier(recipeIdStr).equals(identifier)) {
            throw new IllegalStateException("Shapeless Recipe " + recipeIdStr + " should remove its 'save' argument");
        }
        this.offerTo(exporter, new Identifier(recipeIdStr));
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
        exporter.accept(new ShapelessRecipeJsonProvider(recipeId, this.output, this.outputCount, this.group == null ? "" : this.group, this.inputs, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + recipeId.getPath())));
    }

    private void validate(Identifier recipeId) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static class ShapelessRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Item output;
        private final int count;
        private final String group;
        private final List<Ingredient> inputs;
        private final Advancement.Task builder;
        private final Identifier advancementId;

        public ShapelessRecipeJsonProvider(Identifier recipeId, Item output, int outputCount, String group, List<Ingredient> inputs, Advancement.Task builder, Identifier advancementId) {
            this.recipeId = recipeId;
            this.output = output;
            this.count = outputCount;
            this.group = group;
            this.inputs = inputs;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (Ingredient ingredient : this.inputs) {
                jsonArray.add(ingredient.toJson());
            }
            json.add("ingredients", (JsonElement)jsonArray);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getId(this.output).toString());
            if (this.count > 1) {
                jsonObject.addProperty("count", (Number)this.count);
            }
            json.add("result", (JsonElement)jsonObject);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.builder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}

