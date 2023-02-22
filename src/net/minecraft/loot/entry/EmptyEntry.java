/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;

public class EmptyEntry
extends LeafEntry {
    private EmptyEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(weight, quality, conditions, functions);
    }

    @Override
    public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
    }

    public static LeafEntry.Builder<?> Serializer() {
        return EmptyEntry.builder(EmptyEntry::new);
    }

    public static class Serializer
    extends LeafEntry.Serializer<EmptyEntry> {
        public Serializer() {
            super(new Identifier("empty"), EmptyEntry.class);
        }

        @Override
        protected EmptyEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            return new EmptyEntry(i, j, lootConditions, lootFunctions);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
            return this.fromJson(entryJson, context, weight, quality, conditions, functions);
        }
    }
}

