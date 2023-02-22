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
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public class ShriekParticleEffect
implements ParticleEffect {
    public static final Codec<ShriekParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("delay").forGetter(shriekParticleEffect -> shriekParticleEffect.delay)).apply((Applicative)instance, ShriekParticleEffect::new));
    public static final ParticleEffect.Factory<ShriekParticleEffect> FACTORY = new ParticleEffect.Factory<ShriekParticleEffect>(){

        @Override
        public ShriekParticleEffect read(ParticleType<ShriekParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            int i = stringReader.readInt();
            return new ShriekParticleEffect(i);
        }

        @Override
        public ShriekParticleEffect read(ParticleType<ShriekParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new ShriekParticleEffect(packetByteBuf.readVarInt());
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
    private final int delay;

    public ShriekParticleEffect(int i) {
        this.delay = i;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.delay);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %d", Registry.PARTICLE_TYPE.getId(this.getType()), this.delay);
    }

    public ParticleType<ShriekParticleEffect> getType() {
        return ParticleTypes.SHRIEK;
    }

    public int getDelay() {
        return this.delay;
    }
}

