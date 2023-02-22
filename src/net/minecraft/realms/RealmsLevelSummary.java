/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.storage.LevelSummary;

@Environment(value=EnvType.CLIENT)
public class RealmsLevelSummary
implements Comparable<RealmsLevelSummary> {
    private final LevelSummary levelSummary;

    public RealmsLevelSummary(LevelSummary levelSummary) {
        this.levelSummary = levelSummary;
    }

    public int getGameMode() {
        return this.levelSummary.getGameMode().getId();
    }

    public String getLevelId() {
        return this.levelSummary.getName();
    }

    public boolean hasCheats() {
        return this.levelSummary.hasCheats();
    }

    public boolean isHardcore() {
        return this.levelSummary.isHardcore();
    }

    public boolean isRequiresConversion() {
        return this.levelSummary.requiresConversion();
    }

    public String getLevelName() {
        return this.levelSummary.getDisplayName();
    }

    public long getLastPlayed() {
        return this.levelSummary.getLastPlayed();
    }

    @Override
    public int compareTo(LevelSummary levelSummary) {
        return this.levelSummary.compareTo(levelSummary);
    }

    public long getSizeOnDisk() {
        return this.levelSummary.getSizeOnDisk();
    }

    @Override
    public int compareTo(RealmsLevelSummary realmsLevelSummary) {
        if (this.levelSummary.getLastPlayed() < realmsLevelSummary.getLastPlayed()) {
            return 1;
        }
        if (this.levelSummary.getLastPlayed() > realmsLevelSummary.getLastPlayed()) {
            return -1;
        }
        return this.levelSummary.getName().compareTo(realmsLevelSummary.getLevelId());
    }
}

