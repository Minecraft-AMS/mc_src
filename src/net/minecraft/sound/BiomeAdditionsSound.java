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

public class BiomeAdditionsSound {
    public static final Codec<BiomeAdditionsSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter(biomeAdditionsSound -> biomeAdditionsSound.sound), (App)Codec.DOUBLE.fieldOf("tick_chance").forGetter(biomeAdditionsSound -> biomeAdditionsSound.chance)).apply((Applicative)instance, BiomeAdditionsSound::new));
    private SoundEvent sound;
    private double chance;

    public BiomeAdditionsSound(SoundEvent sound, double chance) {
        this.sound = sound;
        this.chance = chance;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }

    @Environment(value=EnvType.CLIENT)
    public double getChance() {
        return this.chance;
    }
}

