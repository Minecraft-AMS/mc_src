/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

public class MusicSound {
    public static final Codec<MusicSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(sound -> sound.sound), (App)Codec.INT.fieldOf("min_delay").forGetter(sound -> sound.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter(sound -> sound.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter(sound -> sound.replaceCurrentMusic)).apply((Applicative)instance, MusicSound::new));
    private final RegistryEntry<SoundEvent> sound;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public MusicSound(RegistryEntry<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        this.sound = sound;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.replaceCurrentMusic = replaceCurrentMusic;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getMaxDelay() {
        return this.maxDelay;
    }

    public boolean shouldReplaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}

