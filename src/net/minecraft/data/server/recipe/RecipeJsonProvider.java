/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public interface RecipeJsonProvider {
    public void serialize(JsonObject var1);

    default public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", Registry.RECIPE_SERIALIZER.getId(this.getSerializer()).toString());
        this.serialize(jsonObject);
        return jsonObject;
    }

    public Identifier getRecipeId();

    public RecipeSerializer<?> getSerializer();

    @Nullable
    public JsonObject toAdvancementJson();

    @Nullable
    public Identifier getAdvancementId();
}

