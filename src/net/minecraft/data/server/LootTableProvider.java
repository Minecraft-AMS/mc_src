/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.BarterLootTableGenerator;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.ChestLootTableGenerator;
import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.data.server.FishingLootTableGenerator;
import net.minecraft.data.server.GiftLootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators = ImmutableList.of((Object)Pair.of(FishingLootTableGenerator::new, (Object)LootContextTypes.FISHING), (Object)Pair.of(ChestLootTableGenerator::new, (Object)LootContextTypes.CHEST), (Object)Pair.of(EntityLootTableGenerator::new, (Object)LootContextTypes.ENTITY), (Object)Pair.of(BlockLootTableGenerator::new, (Object)LootContextTypes.BLOCK), (Object)Pair.of(BarterLootTableGenerator::new, (Object)LootContextTypes.BARTER), (Object)Pair.of(GiftLootTableGenerator::new, (Object)LootContextTypes.GIFT));

    public LootTableProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.root.getOutput();
        HashMap map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(generator -> ((Consumer)((Supplier)generator.getFirst()).get()).accept((id, builder) -> {
            if (map.put(id, builder.type((LootContextType)generator.getSecond()).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + id);
            }
        }));
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);
        Sets.SetView set = Sets.difference(LootTables.getAll(), map.keySet());
        for (Identifier identifier : set) {
            lootTableReporter.report("Missing built-in table: " + identifier);
        }
        map.forEach((id, table) -> LootManager.validate(lootTableReporter, id, table));
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        map.forEach((id, table) -> {
            Path path2 = LootTableProvider.getOutput(path, id);
            try {
                DataProvider.writeToPath(GSON, cache, LootManager.toJson(table), path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save loot table {}", (Object)path2, (Object)iOException);
            }
        });
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}

