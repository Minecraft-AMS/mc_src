/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataOutput.PathResolver pathResolver;
    private final Set<Identifier> lootTableIds;
    private final List<LootTypeGenerator> lootTypeGenerators;

    public LootTableProvider(DataOutput output, Set<Identifier> lootTableIds, List<LootTypeGenerator> lootTypeGenerators) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
        this.lootTypeGenerators = lootTypeGenerators;
        this.lootTableIds = lootTableIds;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashMap map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(lootTypeGenerator -> lootTypeGenerator.provider().get().accept((id, builder) -> {
            if (map.put(id, builder.type(lootTypeGenerator.paramSet).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + id);
            }
        }));
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);
        Sets.SetView set = Sets.difference(this.lootTableIds, map.keySet());
        for (Identifier identifier : set) {
            lootTableReporter.report("Missing built-in table: " + identifier);
        }
        map.forEach((id, table) -> LootManager.validate(lootTableReporter, id, table));
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        return CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map(entry -> {
            Identifier identifier = (Identifier)entry.getKey();
            LootTable lootTable = (LootTable)entry.getValue();
            Path path = this.pathResolver.resolveJson(identifier);
            return DataProvider.writeToPath(writer, LootManager.toJson(lootTable), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public final String getName() {
        return "Loot Tables";
    }

    public static final class LootTypeGenerator
    extends Record {
        private final Supplier<LootTableGenerator> provider;
        final LootContextType paramSet;

        public LootTypeGenerator(Supplier<LootTableGenerator> supplier, LootContextType lootContextType) {
            this.provider = supplier;
            this.paramSet = lootContextType;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this, object);
        }

        public Supplier<LootTableGenerator> provider() {
            return this.provider;
        }

        public LootContextType paramSet() {
            return this.paramSet;
        }
    }
}

