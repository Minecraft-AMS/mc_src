/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;

public abstract class ParticleType<T extends ParticleEffect> {
    private final boolean alwaysShow;
    private final ParticleEffect.Factory<T> parametersFactory;

    protected ParticleType(boolean alwaysShow, ParticleEffect.Factory<T> parametersFactory) {
        this.alwaysShow = alwaysShow;
        this.parametersFactory = parametersFactory;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAlwaysSpawn() {
        return this.alwaysShow;
    }

    public ParticleEffect.Factory<T> getParametersFactory() {
        return this.parametersFactory;
    }

    public abstract Codec<T> getCodec();
}

