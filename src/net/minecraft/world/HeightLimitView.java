/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface HeightLimitView {
    public int getHeight();

    public int getBottomY();

    default public int getTopY() {
        return this.getBottomY() + this.getHeight();
    }

    default public int countVerticalSections() {
        return this.getTopSectionCoord() - this.getBottomSectionCoord();
    }

    default public int getBottomSectionCoord() {
        return ChunkSectionPos.getSectionCoord(this.getBottomY());
    }

    default public int getTopSectionCoord() {
        return ChunkSectionPos.getSectionCoord(this.getTopY() - 1) + 1;
    }

    default public boolean isOutOfHeightLimit(BlockPos pos) {
        return this.isOutOfHeightLimit(pos.getY());
    }

    default public boolean isOutOfHeightLimit(int y) {
        return y < this.getBottomY() || y >= this.getTopY();
    }

    default public int getSectionIndex(int y) {
        return this.sectionCoordToIndex(ChunkSectionPos.getSectionCoord(y));
    }

    default public int sectionCoordToIndex(int coord) {
        return coord - this.getBottomSectionCoord();
    }

    default public int sectionIndexToCoord(int index) {
        return index + this.getBottomSectionCoord();
    }
}

