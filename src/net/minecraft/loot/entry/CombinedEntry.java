/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class CombinedEntry
extends LootEntry {
    protected final LootEntry[] children;
    private final EntryCombiner predicate;

    protected CombinedEntry(LootEntry[] children, LootCondition[] conditions) {
        super(conditions);
        this.children = children;
        this.predicate = this.combine(children);
    }

    @Override
    public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        if (this.children.length == 0) {
            reporter.report("Empty children list");
        }
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].check(reporter.makeChild(".entry[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
    }

    protected abstract EntryCombiner combine(EntryCombiner[] var1);

    @Override
    public final boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
        if (!this.test(lootContext)) {
            return false;
        }
        return this.predicate.expand(lootContext, consumer);
    }

    public static <T extends CombinedEntry> Serializer<T> createSerializer(Identifier id, Class<T> type, final Factory<T> entry) {
        return new Serializer<T>(id, type){

            @Override
            protected T fromJson(JsonObject json, JsonDeserializationContext context, LootEntry[] children, LootCondition[] conditions) {
                return entry.create(children, conditions);
            }
        };
    }

    public static abstract class Serializer<T extends CombinedEntry>
    extends LootEntry.Serializer<T> {
        public Serializer(Identifier identifier, Class<T> class_) {
            super(identifier, class_);
        }

        @Override
        public void toJson(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("children", jsonSerializationContext.serialize((Object)((CombinedEntry)combinedEntry).children));
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lootEntrys, lootConditions);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootEntry[] var3, LootCondition[] var4);

        @Override
        public /* synthetic */ LootEntry fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    @FunctionalInterface
    public static interface Factory<T extends CombinedEntry> {
        public T create(LootEntry[] var1, LootCondition[] var2);
    }
}

