/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface PlayerSaveHandler {
    public void savePlayerData(PlayerEntity var1);

    @Nullable
    public CompoundTag loadPlayerData(PlayerEntity var1);
}

