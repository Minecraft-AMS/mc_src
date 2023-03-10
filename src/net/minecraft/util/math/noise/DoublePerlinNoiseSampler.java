/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleListIterator
 */
package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.random.AbstractRandom;

public class DoublePerlinNoiseSampler {
    private static final double DOMAIN_SCALE = 1.0181268882175227;
    private static final double field_31703 = 0.3333333333333333;
    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;
    private final double field_36631;
    private final NoiseParameters field_37207;

    @Deprecated
    public static DoublePerlinNoiseSampler createLegacy(AbstractRandom random, NoiseParameters parameters) {
        return new DoublePerlinNoiseSampler(random, parameters, false);
    }

    public static DoublePerlinNoiseSampler create(AbstractRandom random, int offset, double ... octaves) {
        return DoublePerlinNoiseSampler.create(random, new NoiseParameters(offset, (DoubleList)new DoubleArrayList(octaves)));
    }

    public static DoublePerlinNoiseSampler create(AbstractRandom random, NoiseParameters parameters) {
        return new DoublePerlinNoiseSampler(random, parameters, true);
    }

    private DoublePerlinNoiseSampler(AbstractRandom random, NoiseParameters noiseParameters, boolean bl) {
        int i = noiseParameters.firstOctave;
        DoubleList doubleList = noiseParameters.amplitudes;
        this.field_37207 = noiseParameters;
        if (bl) {
            this.firstSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
            this.secondSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
        } else {
            this.firstSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
            this.secondSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
        }
        int j = Integer.MAX_VALUE;
        int k = Integer.MIN_VALUE;
        DoubleListIterator doubleListIterator = doubleList.iterator();
        while (doubleListIterator.hasNext()) {
            int l = doubleListIterator.nextIndex();
            double d = doubleListIterator.nextDouble();
            if (d == 0.0) continue;
            j = Math.min(j, l);
            k = Math.max(k, l);
        }
        this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(k - j);
        this.field_36631 = (this.firstSampler.method_40555() + this.secondSampler.method_40555()) * this.amplitude;
    }

    public double method_40554() {
        return this.field_36631;
    }

    private static double createAmplitude(int octaves) {
        return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double d = x * 1.0181268882175227;
        double e = y * 1.0181268882175227;
        double f = z * 1.0181268882175227;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
    }

    public NoiseParameters copy() {
        return this.field_37207;
    }

    @VisibleForTesting
    public void addDebugInfo(StringBuilder info) {
        info.append("NormalNoise {");
        info.append("first: ");
        this.firstSampler.addDebugInfo(info);
        info.append(", second: ");
        this.secondSampler.addDebugInfo(info);
        info.append("}");
    }

    public static final class NoiseParameters
    extends Record {
        final int firstOctave;
        final DoubleList amplitudes;
        public static final Codec<NoiseParameters> field_35424 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("firstOctave").forGetter(NoiseParameters::firstOctave), (App)Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseParameters::amplitudes)).apply((Applicative)instance, NoiseParameters::new));
        public static final Codec<RegistryEntry<NoiseParameters>> CODEC = RegistryElementCodec.of(Registry.NOISE_WORLDGEN, field_35424);

        public NoiseParameters(int firstOctave, List<Double> amplitudes) {
            this(firstOctave, (DoubleList)new DoubleArrayList(amplitudes));
        }

        public NoiseParameters(int firstOctave, double firstAmplitude, double ... amplitudes) {
            this(firstOctave, (DoubleList)Util.make(new DoubleArrayList(amplitudes), doubleArrayList -> doubleArrayList.add(0, firstAmplitude)));
        }

        public NoiseParameters(int i, DoubleList doubleList) {
            this.firstOctave = i;
            this.amplitudes = doubleList;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{NoiseParameters.class, "firstOctave;amplitudes", "firstOctave", "amplitudes"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NoiseParameters.class, "firstOctave;amplitudes", "firstOctave", "amplitudes"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NoiseParameters.class, "firstOctave;amplitudes", "firstOctave", "amplitudes"}, this, object);
        }

        public int firstOctave() {
            return this.firstOctave;
        }

        public DoubleList amplitudes() {
            return this.amplitudes;
        }
    }
}

