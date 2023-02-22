/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SpecialRecipeSerializer<T extends CraftingRecipe>
implements RecipeSerializer<T> {
    private final Factory<T> factory;

    public SpecialRecipeSerializer(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(Identifier identifier, JsonObject jsonObject) {
        CraftingRecipeCategory craftingRecipeCategory = CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    @Override
    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
        return this.factory.create(identifier, craftingRecipeCategory);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T craftingRecipe) {
        packetByteBuf.writeEnumConstant(craftingRecipe.getCategory());
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
        return this.read(id, buf);
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
        return this.read(id, json);
    }

    @FunctionalInterface
    public static interface Factory<T extends CraftingRecipe> {
        public T create(Identifier var1, CraftingRecipeCategory var2);
    }
}

