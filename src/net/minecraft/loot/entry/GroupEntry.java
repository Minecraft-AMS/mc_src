/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
        super(lootPoolEntrys, lootConditions);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.GROUP;
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] children) {
        switch (children.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                EntryCombiner entryCombiner = children[0];
                EntryCombiner entryCombiner2 = children[1];
                return (context, choiceConsumer) -> {
                    entryCombiner.expand(context, choiceConsumer);
                    entryCombiner2.expand(context, choiceConsumer);
                    return true;
                };
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                entryCombiner.expand(context, lootChoiceExpander);
            }
            return true;
        };
    }

    public static Builder create(LootPoolEntry.Builder<?> ... entries) {
        return new Builder(entries);
    }

    public static class Builder
    extends LootPoolEntry.Builder<Builder> {
        private final List<LootPoolEntry> entries = Lists.newArrayList();

        public Builder(LootPoolEntry.Builder<?> ... entries) {
            for (LootPoolEntry.Builder<?> builder : entries) {
                this.entries.add(builder.build());
            }
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
            this.entries.add(entry.build());
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return new GroupEntry(this.entries.toArray(new LootPoolEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

