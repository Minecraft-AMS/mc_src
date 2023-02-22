/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerAdvancementLoader
extends JsonDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private AdvancementManager manager = new AdvancementManager();
    private final LootConditionManager conditionManager;

    public ServerAdvancementLoader(LootConditionManager conditionManager) {
        super(GSON, "advancements");
        this.conditionManager = conditionManager;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        HashMap map2 = Maps.newHashMap();
        map.forEach((id, json) -> {
            try {
                JsonObject jsonObject = JsonHelper.asObject(json, "advancement");
                Advancement.Builder builder = Advancement.Builder.fromJson(jsonObject, new AdvancementEntityPredicateDeserializer((Identifier)id, this.conditionManager));
                map2.put(id, builder);
            }
            catch (Exception exception) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", id, (Object)exception.getMessage());
            }
        });
        AdvancementManager advancementManager = new AdvancementManager();
        advancementManager.load(map2);
        for (Advancement advancement : advancementManager.getRoots()) {
            if (advancement.getDisplay() == null) continue;
            AdvancementPositioner.arrangeForTree(advancement);
        }
        this.manager = advancementManager;
    }

    @Nullable
    public Advancement get(Identifier id) {
        return this.manager.get(id);
    }

    public Collection<Advancement> getAdvancements() {
        return this.manager.getAdvancements();
    }
}

