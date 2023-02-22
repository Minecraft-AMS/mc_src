/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UniformLootTableRange.class, (Object)new UniformLootTableRange.Serializer()).registerTypeAdapter(BinomialLootTableRange.class, (Object)new BinomialLootTableRange.Serializer()).registerTypeAdapter(ConstantLootTableRange.class, (Object)new ConstantLootTableRange.Serializer()).registerTypeAdapter(BoundedIntUnaryOperator.class, (Object)new BoundedIntUnaryOperator.Serializer()).registerTypeAdapter(LootPool.class, (Object)new LootPool.Serializer()).registerTypeAdapter(LootTable.class, (Object)new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, (Object)new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, (Object)new LootFunctions.Factory()).registerTypeHierarchyAdapter(LootCondition.class, (Object)new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, (Object)new LootContext.EntityTarget.Serializer()).create();
    private Map<Identifier, LootTable> suppliers = ImmutableMap.of();
    private final LootConditionManager conditionManager;

    public LootManager(LootConditionManager conditionManager) {
        super(GSON, "loot_tables");
        this.conditionManager = conditionManager;
    }

    public LootTable getSupplier(Identifier id) {
        return this.suppliers.getOrDefault(id, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        JsonObject jsonObject2 = map.remove(LootTables.EMPTY);
        if (jsonObject2 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((identifier, jsonObject) -> {
            try {
                LootTable lootTable = (LootTable)GSON.fromJson((JsonElement)jsonObject, LootTable.class);
                builder.put(identifier, (Object)lootTable);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", identifier, (Object)exception);
            }
        });
        builder.put((Object)LootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, arg_0 -> ((ImmutableMap)immutableMap).get(arg_0));
        immutableMap.forEach((identifier, lootTable) -> LootManager.check(lootTableReporter, identifier, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in " + key + ": " + value));
        this.suppliers = immutableMap;
    }

    public static void check(LootTableReporter reporter, Identifier id, LootTable table) {
        table.check(reporter.withContextType(table.getType()).withSupplier("{" + id + "}", id));
    }

    public static JsonElement toJson(LootTable supplier) {
        return GSON.toJsonTree((Object)supplier);
    }

    public Set<Identifier> getSupplierNames() {
        return this.suppliers.keySet();
    }
}

