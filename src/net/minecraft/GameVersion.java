/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameVersion
 */
package net.minecraft;

import net.minecraft.SaveVersion;

public interface GameVersion
extends com.mojang.bridge.game.GameVersion {
    @Deprecated
    default public int getWorldVersion() {
        return this.getSaveVersion().getId();
    }

    @Deprecated
    default public String getSeriesId() {
        return this.getSaveVersion().getSeries();
    }

    public SaveVersion getSaveVersion();
}

