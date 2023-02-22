/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry
extends CombinedEntry {
    AlternativeEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] children) {
        switch (children.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                return children[0].or(children[1]);
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                if (!entryCombiner.expand(context, lootChoiceExpander)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty((Object[])this.children[i].conditions)) continue;
            reporter.report("Unreachable entry!");
        }
    }

    public static Builder builder(LootEntry.Builder<?> ... children) {
        return new Builder(children);
    }

    public static class Builder
    extends LootEntry.Builder<Builder> {
        private final List<LootEntry> children = Lists.newArrayList();

        public Builder(LootEntry.Builder<?> ... children) {
            for (LootEntry.Builder<?> builder : children) {
                this.children.add(builder.build());
            }
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public Builder withChild(LootEntry.Builder<?> builder) {
            this.children.add(builder.build());
            return this;
        }

        @Override
        public LootEntry build() {
            return new AlternativeEntry(this.children.toArray(new LootEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

