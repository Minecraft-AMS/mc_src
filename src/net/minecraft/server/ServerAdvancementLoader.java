/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapterFactory
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerAdvancementLoader
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Advancement.Task.class, (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
        return Advancement.Task.fromJson(jsonObject, jsonDeserializationContext);
    }).registerTypeAdapter(AdvancementRewards.class, (Object)new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(Text.class, (Object)new Text.Serializer()).registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new LowercaseEnumTypeAdapterFactory()).create();
    private AdvancementManager manager = new AdvancementManager();

    public ServerAdvancementLoader() {
        super(GSON, "advancements");
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
        HashMap map2 = Maps.newHashMap();
        map.forEach((identifier, jsonObject) -> {
            try {
                Advancement.Task task = (Advancement.Task)GSON.fromJson((JsonElement)jsonObject, Advancement.Task.class);
                map2.put(identifier, task);
            }
            catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", identifier, (Object)runtimeException.getMessage());
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
    public Advancement get(Identifier identifier) {
        return this.manager.get(identifier);
    }

    public Collection<Advancement> getAdvancements() {
        return this.manager.getAdvancements();
    }
}

