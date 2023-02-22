/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;

public record Instrument(SoundEvent soundEvent, int useDuration, float range) {
    public static final Codec<Instrument> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent), (App)Codecs.POSITIVE_INT.fieldOf("use_duration").forGetter(Instrument::useDuration), (App)Codecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range)).apply((Applicative)instance, Instrument::new));
}

