/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public abstract class CuttingRecipe
implements Recipe<Inventory> {
    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final Identifier id;
    protected final String group;

    public CuttingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, Identifier id, String group, Ingredient input, ItemStack output) {
        this.type = type;
        this.serializer = serializer;
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return this.output.copy();
    }

    public static class Serializer<T extends CuttingRecipe>
    implements RecipeSerializer<T> {
        final class_3974<T> field_17648;

        protected Serializer(class_3974<T> arg) {
            this.field_17648 = arg;
        }

        @Override
        public T read(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            Ingredient ingredient = JsonHelper.hasArray(jsonObject, "ingredient") ? Ingredient.fromJson((JsonElement)JsonHelper.getArray(jsonObject, "ingredient")) : Ingredient.fromJson((JsonElement)JsonHelper.getObject(jsonObject, "ingredient"));
            String string2 = JsonHelper.getString(jsonObject, "result");
            int i = JsonHelper.getInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(string2)), i);
            return this.field_17648.create(identifier, string, ingredient, itemStack);
        }

        @Override
        public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString(Short.MAX_VALUE);
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return this.field_17648.create(identifier, string, ingredient, itemStack);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, T cuttingRecipe) {
            packetByteBuf.writeString(((CuttingRecipe)cuttingRecipe).group);
            ((CuttingRecipe)cuttingRecipe).input.write(packetByteBuf);
            packetByteBuf.writeItemStack(((CuttingRecipe)cuttingRecipe).output);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
            return this.read(id, buf);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
            return this.read(id, json);
        }

        static interface class_3974<T extends CuttingRecipe> {
            public T create(Identifier var1, String var2, Ingredient var3, ItemStack var4);
        }
    }
}
