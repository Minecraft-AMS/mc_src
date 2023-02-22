/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldTemplate
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String id;
    public String name;
    public String version;
    public String author;
    public String link;
    public String image;
    public String trailer;
    public String recommendedPlayers;
    public WorldTemplateType type;

    public static WorldTemplate parse(JsonObject node) {
        WorldTemplate worldTemplate = new WorldTemplate();
        try {
            worldTemplate.id = JsonUtils.getStringOr("id", node, "");
            worldTemplate.name = JsonUtils.getStringOr("name", node, "");
            worldTemplate.version = JsonUtils.getStringOr("version", node, "");
            worldTemplate.author = JsonUtils.getStringOr("author", node, "");
            worldTemplate.link = JsonUtils.getStringOr("link", node, "");
            worldTemplate.image = JsonUtils.getStringOr("image", node, null);
            worldTemplate.trailer = JsonUtils.getStringOr("trailer", node, "");
            worldTemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", node, "");
            worldTemplate.type = WorldTemplateType.valueOf(JsonUtils.getStringOr("type", node, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
        }
        return worldTemplate;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WorldTemplateType {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }
}

