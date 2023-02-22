/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

public class MusicSound {
    public static final Codec<MusicSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter(musicSound -> musicSound.sound), (App)Codec.INT.fieldOf("min_delay").forGetter(musicSound -> musicSound.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter(musicSound -> musicSound.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter(musicSound -> musicSound.replaceCurrentMusic)).apply((Applicative)instance, MusicSound::new));
    private final SoundEvent sound;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public MusicSound(SoundEvent sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        this.sound = sound;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.replaceCurrentMusic = replaceCurrentMusic;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }

    @Environment(value=EnvType.CLIENT)
    public int getMinDelay() {
        return this.minDelay;
    }

    @Environment(value=EnvType.CLIENT)
    public int getMaxDelay() {
        return this.maxDelay;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldReplaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}

