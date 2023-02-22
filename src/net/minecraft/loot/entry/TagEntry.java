/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TagEntry
extends LeafEntry {
    private final Tag<Item> name;
    private final boolean expand;

    private TagEntry(Tag<Item> name, boolean expand, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.name = name;
        this.expand = expand;
    }

    @Override
    public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
        this.name.values().forEach(item -> itemDropper.accept(new ItemStack((ItemConvertible)item)));
    }

    private boolean grow(LootContext context, Consumer<LootChoice> lootChoiceExpander) {
        if (this.test(context)) {
            for (final Item item : this.name.values()) {
                lootChoiceExpander.accept(new LeafEntry.Choice(){

                    @Override
                    public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
                        itemDropper.accept(new ItemStack(item));
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
        if (this.expand) {
            return this.grow(lootContext, consumer);
        }
        return super.expand(lootContext, consumer);
    }

    public static LeafEntry.Builder<?> builder(Tag<Item> name) {
        return TagEntry.builder((int weight, int quality, LootCondition[] conditions, LootFunction[] functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
    }

    public static class Serializer
    extends LeafEntry.Serializer<TagEntry> {
        public Serializer() {
            super(new Identifier("tag"), TagEntry.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, tagEntry, jsonSerializationContext);
            jsonObject.addProperty("name", tagEntry.name.getId().toString());
            jsonObject.addProperty("expand", Boolean.valueOf(tagEntry.expand));
        }

        @Override
        protected TagEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            Tag<Item> tag = ItemTags.getContainer().get(identifier);
            if (tag == null) {
                throw new JsonParseException("Can't find tag: " + identifier);
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "expand");
            return new TagEntry(tag, bl, i, j, lootConditions, lootFunctions);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
            return this.fromJson(entryJson, context, weight, quality, conditions, functions);
        }
    }
}

