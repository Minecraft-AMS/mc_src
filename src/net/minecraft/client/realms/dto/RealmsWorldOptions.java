/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldOptions
extends ValueObject {
    public final boolean pvp;
    public final boolean spawnAnimals;
    public final boolean spawnMonsters;
    public final boolean spawnNpcs;
    public final int spawnProtection;
    public final boolean commandBlocks;
    public final boolean forceGameMode;
    public final int difficulty;
    public final int gameMode;
    @Nullable
    private final String slotName;
    public long templateId;
    @Nullable
    public String templateImage;
    public boolean empty;
    private static final boolean field_32100 = false;
    private static final boolean field_32101 = true;
    private static final boolean field_32102 = true;
    private static final boolean field_32103 = true;
    private static final boolean field_32104 = true;
    private static final int field_32105 = 0;
    private static final boolean field_32106 = false;
    private static final int DEFAULT_DIFFICULTY = 2;
    private static final int field_32108 = 0;
    private static final String DEFAULT_SLOT_NAME = "";
    private static final long DEFAULT_WORLD_TEMPLATE_ID = -1L;
    private static final String DEFAULT_WORLD_TEMPLATE_IMAGE = null;

    public RealmsWorldOptions(boolean pvp, boolean spawnAnimals, boolean spawnMonsters, boolean spawnNpcs, int spawnProtection, boolean commandBlocks, int difficulty, int gameMode, boolean forceGameMode, @Nullable String slotName) {
        this.pvp = pvp;
        this.spawnAnimals = spawnAnimals;
        this.spawnMonsters = spawnMonsters;
        this.spawnNpcs = spawnNpcs;
        this.spawnProtection = spawnProtection;
        this.commandBlocks = commandBlocks;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.forceGameMode = forceGameMode;
        this.slotName = slotName;
    }

    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, DEFAULT_SLOT_NAME);
    }

    public static RealmsWorldOptions getEmptyDefaults() {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.getDefaults();
        realmsWorldOptions.setEmpty(true);
        return realmsWorldOptions;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static RealmsWorldOptions parse(JsonObject json) {
        RealmsWorldOptions realmsWorldOptions = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", json, true), JsonUtils.getBooleanOr("spawnAnimals", json, true), JsonUtils.getBooleanOr("spawnMonsters", json, true), JsonUtils.getBooleanOr("spawnNPCs", json, true), JsonUtils.getIntOr("spawnProtection", json, 0), JsonUtils.getBooleanOr("commandBlocks", json, false), JsonUtils.getIntOr("difficulty", json, 2), JsonUtils.getIntOr("gameMode", json, 0), JsonUtils.getBooleanOr("forceGameMode", json, false), JsonUtils.getStringOr("slotName", json, DEFAULT_SLOT_NAME));
        realmsWorldOptions.templateId = JsonUtils.getLongOr("worldTemplateId", json, -1L);
        realmsWorldOptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", json, DEFAULT_WORLD_TEMPLATE_IMAGE);
        return realmsWorldOptions;
    }

    public String getSlotName(int index) {
        if (this.slotName == null || this.slotName.isEmpty()) {
            if (this.empty) {
                return I18n.translate("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName(index);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int index) {
        return I18n.translate("mco.configure.world.slot", index);
    }

    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        if (!this.pvp) {
            jsonObject.addProperty("pvp", Boolean.valueOf(this.pvp));
        }
        if (!this.spawnAnimals) {
            jsonObject.addProperty("spawnAnimals", Boolean.valueOf(this.spawnAnimals));
        }
        if (!this.spawnMonsters) {
            jsonObject.addProperty("spawnMonsters", Boolean.valueOf(this.spawnMonsters));
        }
        if (!this.spawnNpcs) {
            jsonObject.addProperty("spawnNPCs", Boolean.valueOf(this.spawnNpcs));
        }
        if (this.spawnProtection != 0) {
            jsonObject.addProperty("spawnProtection", (Number)this.spawnProtection);
        }
        if (this.commandBlocks) {
            jsonObject.addProperty("commandBlocks", Boolean.valueOf(this.commandBlocks));
        }
        if (this.difficulty != 2) {
            jsonObject.addProperty("difficulty", (Number)this.difficulty);
        }
        if (this.gameMode != 0) {
            jsonObject.addProperty("gameMode", (Number)this.gameMode);
        }
        if (this.forceGameMode) {
            jsonObject.addProperty("forceGameMode", Boolean.valueOf(this.forceGameMode));
        }
        if (!Objects.equals(this.slotName, DEFAULT_SLOT_NAME)) {
            jsonObject.addProperty("slotName", this.slotName);
        }
        return jsonObject.toString();
    }

    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNpcs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }
}

