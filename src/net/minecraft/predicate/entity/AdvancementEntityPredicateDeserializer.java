/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.predicate.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class AdvancementEntityPredicateDeserializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier advancementId;
    private final LootConditionManager conditionManager;
    private final Gson gson = LootGsons.getConditionGsonBuilder().create();

    public AdvancementEntityPredicateDeserializer(Identifier advancementId, LootConditionManager conditionManager) {
        this.advancementId = advancementId;
        this.conditionManager = conditionManager;
    }

    public final LootCondition[] loadConditions(JsonArray array, String key, LootContextType contextType) {
        LootCondition[] lootConditions = (LootCondition[])this.gson.fromJson((JsonElement)array, LootCondition[].class);
        LootTableReporter lootTableReporter = new LootTableReporter(contextType, this.conditionManager::get, identifier -> null);
        for (LootCondition lootCondition : lootConditions) {
            lootCondition.validate(lootTableReporter);
            lootTableReporter.getMessages().forEach((string2, string3) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", new Object[]{key, string2, string3}));
        }
        return lootConditions;
    }

    public Identifier getAdvancementId() {
        return this.advancementId;
    }
}

