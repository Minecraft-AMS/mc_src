/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldTemplate
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type = WorldTemplateType.WORLD_TEMPLATE;

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
            LOGGER.error("Could not parse WorldTemplate: {}", (Object)exception.getMessage());
        }
        return worldTemplate;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldTemplateType
    extends Enum<WorldTemplateType> {
        public static final /* enum */ WorldTemplateType WORLD_TEMPLATE = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType MINIGAME = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType ADVENTUREMAP = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType EXPERIENCE = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType INSPIRATION = new WorldTemplateType();
        private static final /* synthetic */ WorldTemplateType[] field_19452;

        public static WorldTemplateType[] values() {
            return (WorldTemplateType[])field_19452.clone();
        }

        public static WorldTemplateType valueOf(String name) {
            return Enum.valueOf(WorldTemplateType.class, name);
        }

        private static /* synthetic */ WorldTemplateType[] method_36851() {
            return new WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            field_19452 = WorldTemplateType.method_36851();
        }
    }
}

