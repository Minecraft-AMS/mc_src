/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("recipe_unlocked");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
        return new Conditions(identifier);
    }

    public void trigger(ServerPlayerEntity player, Recipe<?> recipe) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(recipe));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Identifier recipe;

        public Conditions(Identifier identifier) {
            super(ID);
            this.recipe = identifier;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("recipe", this.recipe.toString());
            return jsonObject;
        }

        public boolean matches(Recipe<?> recipe) {
            return this.recipe.equals(recipe.getId());
        }
    }
}

