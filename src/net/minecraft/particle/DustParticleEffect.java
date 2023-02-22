/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class DustParticleEffect
extends AbstractDustParticleEffect {
    public static final Vec3f RED = new Vec3f(Vec3d.unpackRgb(0xFF0000));
    public static final DustParticleEffect DEFAULT = new DustParticleEffect(RED, 1.0f);
    public static final Codec<DustParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Vec3f.CODEC.fieldOf("color").forGetter(dustParticleEffect -> dustParticleEffect.color), (App)Codec.FLOAT.fieldOf("scale").forGetter(dustParticleEffect -> Float.valueOf(dustParticleEffect.scale))).apply((Applicative)instance, DustParticleEffect::new));
    public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>(){

        @Override
        public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            Vec3f vec3f = AbstractDustParticleEffect.readColor(stringReader);
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new DustParticleEffect(vec3f, f);
        }

        @Override
        public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new DustParticleEffect(AbstractDustParticleEffect.readColor(packetByteBuf), packetByteBuf.readFloat());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };

    public DustParticleEffect(Vec3f vec3f, float f) {
        super(vec3f, f);
    }

    public ParticleType<DustParticleEffect> getType() {
        return ParticleTypes.DUST;
    }
}

