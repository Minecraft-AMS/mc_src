/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SoundInstance {
    public Identifier getId();

    @Nullable
    public WeightedSoundSet getSoundSet(SoundManager var1);

    public Sound getSound();

    public SoundCategory getCategory();

    public boolean isRepeatable();

    public boolean isLooping();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public float getX();

    public float getY();

    public float getZ();

    public AttenuationType getAttenuationType();

    default public boolean shouldAlwaysPlay() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AttenuationType {
        NONE,
        LINEAR;

    }
}
