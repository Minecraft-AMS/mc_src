/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.ValueObject;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServerPlayerLists
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<RealmsServerPlayerList> servers;

    public static RealmsServerPlayerLists parse(String json) {
        RealmsServerPlayerLists realmsServerPlayerLists = new RealmsServerPlayerLists();
        realmsServerPlayerLists.servers = Lists.newArrayList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            if (jsonObject.get("lists").isJsonArray()) {
                JsonArray jsonArray = jsonObject.get("lists").getAsJsonArray();
                Iterator iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    realmsServerPlayerLists.servers.add(RealmsServerPlayerList.parse(((JsonElement)iterator.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerPlayerLists: {}", (Object)exception.getMessage());
        }
        return realmsServerPlayerLists;
    }
}

