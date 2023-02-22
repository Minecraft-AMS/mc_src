/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
    private Map<Identifier, LootTable> tables = ImmutableMap.of();
    private final LootConditionManager conditionManager;

    public LootManager(LootConditionManager conditionManager) {
        super(GSON, "loot_tables");
        this.conditionManager = conditionManager;
    }

    public LootTable getTable(Identifier id) {
        return this.tables.getOrDefault(id, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        JsonElement jsonElement2 = map.remove(LootTables.EMPTY);
        if (jsonElement2 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((identifier, jsonElement) -> {
            try {
                LootTable lootTable = (LootTable)GSON.fromJson(jsonElement, LootTable.class);
                builder.put(identifier, (Object)lootTable);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", identifier, (Object)exception);
            }
        });
        builder.put((Object)LootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, arg_0 -> ((ImmutableMap)immutableMap).get(arg_0));
        immutableMap.forEach((identifier, lootTable) -> LootManager.validate(lootTableReporter, identifier, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in " + key + ": " + value));
        this.tables = immutableMap;
    }

    public static void validate(LootTableReporter reporter, Identifier id, LootTable table) {
        table.validate(reporter.withContextType(table.getType()).withTable("{" + id + "}", id));
    }

    public static JsonElement toJson(LootTable table) {
        return GSON.toJsonTree((Object)table);
    }

    public Set<Identifier> getTableIds() {
        return this.tables.keySet();
    }
}

