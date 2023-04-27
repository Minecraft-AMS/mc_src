/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.decoration;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record Brightness(int block, int sky) {
    public static final Codec<Integer> LIGHT_LEVEL_CODEC = Codecs.rangedInt(0, 15);
    public static final Codec<Brightness> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)LIGHT_LEVEL_CODEC.fieldOf("block").forGetter(Brightness::block), (App)LIGHT_LEVEL_CODEC.fieldOf("sky").forGetter(Brightness::sky)).apply((Applicative)instance, Brightness::new));
    public static Brightness FULL = new Brightness(15, 15);

    public int pack() {
        return this.block << 4 | this.sky << 20;
    }

    public static Brightness unpack(int packed) {
        int i = packed >> 4 & 0xFFFF;
        int j = packed >> 20 & 0xFFFF;
        return new Brightness(i, j);
    }
}

