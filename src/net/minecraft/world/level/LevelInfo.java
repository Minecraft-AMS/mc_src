/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public final class LevelInfo {
    private final long seed;
    private final GameMode gameMode;
    private final boolean structures;
    private final boolean hardcore;
    private final LevelGeneratorType generatorType;
    private boolean commands;
    private boolean bonusChest;
    private JsonElement generatorOptions = new JsonObject();

    public LevelInfo(long seed, GameMode gameMode, boolean structures, boolean hardcore, LevelGeneratorType generatorType) {
        this.seed = seed;
        this.gameMode = gameMode;
        this.structures = structures;
        this.hardcore = hardcore;
        this.generatorType = generatorType;
    }

    public LevelInfo(LevelProperties levelProperties) {
        this(levelProperties.getSeed(), levelProperties.getGameMode(), levelProperties.hasStructures(), levelProperties.isHardcore(), levelProperties.getGeneratorType());
    }

    public LevelInfo setBonusChest() {
        this.bonusChest = true;
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public LevelInfo enableCommands() {
        this.commands = true;
        return this;
    }

    public LevelInfo setGeneratorOptions(JsonElement generatorOptions) {
        this.generatorOptions = generatorOptions;
        return this;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public long getSeed() {
        return this.seed;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public boolean hasStructures() {
        return this.structures;
    }

    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public boolean allowCommands() {
        return this.commands;
    }

    public JsonElement getGeneratorOptions() {
        return this.generatorOptions;
    }
}

