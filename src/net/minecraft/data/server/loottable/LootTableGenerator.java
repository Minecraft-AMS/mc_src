/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server.loottable;

import java.util.function.BiConsumer;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootTableGenerator {
    public void accept(BiConsumer<Identifier, LootTable.Builder> var1);
}

