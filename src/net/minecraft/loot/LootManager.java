/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
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
        JsonElement jsonElement = map.remove(LootTables.EMPTY);
        if (jsonElement != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((id, json) -> {
            try {
                LootTable lootTable = (LootTable)GSON.fromJson(json, LootTable.class);
                builder.put(id, (Object)lootTable);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", id, (Object)exception);
            }
        });
        builder.put((Object)LootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, arg_0 -> ((ImmutableMap)immutableMap).get(arg_0));
        immutableMap.forEach((id, lootTable) -> LootManager.validate(lootTableReporter, id, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in {}: {}", key, value));
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

