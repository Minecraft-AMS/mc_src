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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class BiomeMoodSound {
    public static final Codec<BiomeMoodSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter(biomeMoodSound -> biomeMoodSound.sound), (App)Codec.INT.fieldOf("tick_delay").forGetter(biomeMoodSound -> biomeMoodSound.cultivationTicks), (App)Codec.INT.fieldOf("block_search_extent").forGetter(biomeMoodSound -> biomeMoodSound.spawnRange), (App)Codec.DOUBLE.fieldOf("offset").forGetter(biomeMoodSound -> biomeMoodSound.extraDistance)).apply((Applicative)instance, BiomeMoodSound::new));
    public static final BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
    private final SoundEvent sound;
    private final int cultivationTicks;
    private final int spawnRange;
    private final double extraDistance;

    public BiomeMoodSound(SoundEvent sound, int cultivationTicks, int spawnRange, double extraDistance) {
        this.sound = sound;
        this.cultivationTicks = cultivationTicks;
        this.spawnRange = spawnRange;
        this.extraDistance = extraDistance;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public int getCultivationTicks() {
        return this.cultivationTicks;
    }

    public int getSpawnRange() {
        return this.spawnRange;
    }

    public double getExtraDistance() {
        return this.extraDistance;
    }
}

