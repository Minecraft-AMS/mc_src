/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.world.loot.condition.LootCondition;

public class SequenceEntry
extends CombinedEntry {
    SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
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
                return children[0].and(children[1]);
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                if (entryCombiner.expand(context, lootChoiceExpander)) continue;
                return false;
            }
            return true;
        };
    }
}

