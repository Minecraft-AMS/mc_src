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
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.ValueObject;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServerList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<RealmsServer> servers;

    public static RealmsServerList parse(String json) {
        RealmsServerList realmsServerList = new RealmsServerList();
        realmsServerList.servers = Lists.newArrayList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            if (jsonObject.get("servers").isJsonArray()) {
                JsonArray jsonArray = jsonObject.get("servers").getAsJsonArray();
                Iterator iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    realmsServerList.servers.add(RealmsServer.parse(((JsonElement)iterator.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServerList: {}", (Object)exception.getMessage());
        }
        return realmsServerList;
    }
}

