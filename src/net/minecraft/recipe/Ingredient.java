/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public final class Ingredient
implements Predicate<ItemStack> {
    private static final Predicate<? super Entry> NON_EMPTY = entry -> !entry.getStacks().stream().allMatch(ItemStack::isEmpty);
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Entry[] entries;
    private ItemStack[] matchingStacks;
    private IntList ids;

    private Ingredient(Stream<? extends Entry> entries) {
        this.entries = (Entry[])entries.filter(NON_EMPTY).toArray(Entry[]::new);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack[] getMatchingStacksClient() {
        this.cacheMatchingStacks();
        return this.matchingStacks;
    }

    private void cacheMatchingStacks() {
        if (this.matchingStacks == null) {
            this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (this.entries.length == 0) {
            return itemStack.isEmpty();
        }
        this.cacheMatchingStacks();
        for (ItemStack itemStack2 : this.matchingStacks) {
            if (itemStack2.getItem() != itemStack.getItem()) continue;
            return true;
        }
        return false;
    }

    public IntList getIds() {
        if (this.ids == null) {
            this.cacheMatchingStacks();
            this.ids = new IntArrayList(this.matchingStacks.length);
            for (ItemStack itemStack : this.matchingStacks) {
                this.ids.add(RecipeFinder.getItemId(itemStack));
            }
            this.ids.sort((Comparator)IntComparators.NATURAL_COMPARATOR);
        }
        return this.ids;
    }

    public void write(PacketByteBuf buf) {
        this.cacheMatchingStacks();
        buf.writeVarInt(this.matchingStacks.length);
        for (int i = 0; i < this.matchingStacks.length; ++i) {
            buf.writeItemStack(this.matchingStacks[i]);
        }
    }

    public JsonElement toJson() {
        if (this.entries.length == 1) {
            return this.entries[0].toJson();
        }
        JsonArray jsonArray = new JsonArray();
        for (Entry entry : this.entries) {
            jsonArray.add((JsonElement)entry.toJson());
        }
        return jsonArray;
    }

    public boolean isEmpty() {
        return !(this.entries.length != 0 || this.matchingStacks != null && this.matchingStacks.length != 0 || this.ids != null && !this.ids.isEmpty());
    }

    private static Ingredient ofEntries(Stream<? extends Entry> entries) {
        Ingredient ingredient = new Ingredient(entries);
        return ingredient.entries.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient ofItems(ItemConvertible ... items) {
        return Ingredient.ofEntries(Arrays.stream(items).map(item -> new StackEntry(new ItemStack((ItemConvertible)item))));
    }

    @Environment(value=EnvType.CLIENT)
    public static Ingredient ofStacks(ItemStack ... stacks) {
        return Ingredient.ofEntries(Arrays.stream(stacks).map(stack -> new StackEntry((ItemStack)stack)));
    }

    public static Ingredient fromTag(Tag<Item> tag) {
        return Ingredient.ofEntries(Stream.of(new TagEntry(tag)));
    }

    public static Ingredient fromPacket(PacketByteBuf buf) {
        int i = buf.readVarInt();
        return Ingredient.ofEntries(Stream.generate(() -> new StackEntry(buf.readItemStack())).limit(i));
    }

    public static Ingredient fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (json.isJsonObject()) {
            return Ingredient.ofEntries(Stream.of(Ingredient.entryFromJson(json.getAsJsonObject())));
        }
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            return Ingredient.ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> Ingredient.entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    public static Entry entryFromJson(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (json.has("item")) {
            Identifier identifier = new Identifier(JsonHelper.getString(json, "item"));
            Item item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + identifier + "'"));
            return new StackEntry(new ItemStack(item));
        }
        if (json.has("tag")) {
            Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
            Tag<Item> tag = ItemTags.getContainer().get(identifier);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + identifier + "'");
            }
            return new TagEntry(tag);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object stack) {
        return this.test((ItemStack)stack);
    }

    static class TagEntry
    implements Entry {
        private final Tag<Item> tag;

        private TagEntry(Tag<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            ArrayList list = Lists.newArrayList();
            for (Item item : this.tag.values()) {
                list.add(new ItemStack(item));
            }
            return list;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tag", this.tag.getId().toString());
            return jsonObject;
        }
    }

    static class StackEntry
    implements Entry {
        private final ItemStack stack;

        private StackEntry(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getId(this.stack.getItem()).toString());
            return jsonObject;
        }
    }

    static interface Entry {
        public Collection<ItemStack> getStacks();

        public JsonObject toJson();
    }
}

