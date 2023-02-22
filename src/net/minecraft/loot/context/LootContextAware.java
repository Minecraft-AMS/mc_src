/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.loot.context;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

public interface LootContextAware {
    default public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }

    default public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        contextType.check(reporter, this);
    }
}

