/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MusicTracker {
    private static final int field_33019 = 100;
    private final Random random = new Random();
    private final MinecraftClient client;
    @Nullable
    private SoundInstance current;
    private int timeUntilNextSong = 100;

    public MusicTracker(MinecraftClient client) {
        this.client = client;
    }

    public void tick() {
        MusicSound musicSound = this.client.getMusicType();
        if (this.current != null) {
            if (!musicSound.getSound().getId().equals(this.current.getId()) && musicSound.shouldReplaceCurrentMusic()) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicSound.getMinDelay() / 2);
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, musicSound.getMinDelay(), musicSound.getMaxDelay()));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicSound.getMaxDelay());
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(musicSound);
        }
    }

    public void play(MusicSound type) {
        this.current = PositionedSoundInstance.music(type.getSound());
        if (this.current.getSound() != SoundManager.MISSING_SOUND) {
            this.client.getSoundManager().play(this.current);
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
    }

    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
        }
        this.timeUntilNextSong += 100;
    }

    public boolean isPlayingType(MusicSound type) {
        if (this.current == null) {
            return false;
        }
        return type.getSound().getId().equals(this.current.getId());
    }
}

