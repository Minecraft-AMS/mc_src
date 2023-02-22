/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.screen.RealmsWorldGeneratorType;

@Environment(value=EnvType.CLIENT)
public class ResetWorldInfo {
    private final String seed;
    private final RealmsWorldGeneratorType levelType;
    private final boolean generateStructures;

    public ResetWorldInfo(String seed, RealmsWorldGeneratorType levelType, boolean generateStructures) {
        this.seed = seed;
        this.levelType = levelType;
        this.generateStructures = generateStructures;
    }

    public String getSeed() {
        return this.seed;
    }

    public RealmsWorldGeneratorType getLevelType() {
        return this.levelType;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }
}

