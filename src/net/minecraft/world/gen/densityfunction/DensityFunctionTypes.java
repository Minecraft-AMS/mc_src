/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.doubles.Double2DoubleFunction
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen.densityfunction;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
    private static final Codec<DensityFunction> CODEC = Registry.DENSITY_FUNCTION_TYPE.getCodec().dispatch(DensityFunction::getCodec, Function.identity());
    protected static final double field_37060 = 1000000.0;
    static final Codec<Double> CONSTANT_RANGE = Codec.doubleRange((double)-1000000.0, (double)1000000.0);
    public static final Codec<DensityFunction> field_37061 = Codec.either(CONSTANT_RANGE, CODEC).xmap(either -> (DensityFunction)either.map(DensityFunctionTypes::constant, Function.identity()), densityFunction -> {
        if (densityFunction instanceof Constant) {
            Constant constant = (Constant)densityFunction;
            return Either.left((Object)constant.value());
        }
        return Either.right((Object)densityFunction);
    });

    public static Codec<? extends DensityFunction> registerAndGetDefault(Registry<Codec<? extends DensityFunction>> registry) {
        DensityFunctionTypes.register(registry, "blend_alpha", BlendAlpha.CODEC);
        DensityFunctionTypes.register(registry, "blend_offset", BlendOffset.CODEC);
        DensityFunctionTypes.register(registry, "beardifier", (Codec<? extends DensityFunction>)Beardifier.CODEC);
        DensityFunctionTypes.register(registry, "old_blended_noise", InterpolatedNoiseSampler.CODEC);
        for (class_6927.Type type : class_6927.Type.values()) {
            DensityFunctionTypes.register(registry, type.asString(), type.codec);
        }
        DensityFunctionTypes.register(registry, "noise", Noise.CODEC);
        DensityFunctionTypes.register(registry, "end_islands", EndIslands.CODEC);
        DensityFunctionTypes.register(registry, "weird_scaled_sampler", WeirdScaledSampler.CODEC);
        DensityFunctionTypes.register(registry, "shifted_noise", ShiftedNoise.CODEC);
        DensityFunctionTypes.register(registry, "range_choice", RangeChoice.CODEC);
        DensityFunctionTypes.register(registry, "shift_a", ShiftA.CODEC);
        DensityFunctionTypes.register(registry, "shift_b", ShiftB.CODEC);
        DensityFunctionTypes.register(registry, "shift", Shift.CODEC);
        DensityFunctionTypes.register(registry, "blend_density", BlendDensity.CODEC);
        DensityFunctionTypes.register(registry, "clamp", Clamp.CODEC);
        for (Enum enum_ : class_6925.Type.values()) {
            DensityFunctionTypes.register(registry, ((class_6925.Type)enum_).asString(), ((class_6925.Type)enum_).codec);
        }
        DensityFunctionTypes.register(registry, "slide", Slide.CODEC);
        for (Enum enum_ : Operation.Type.values()) {
            DensityFunctionTypes.register(registry, ((Operation.Type)enum_).asString(), ((Operation.Type)enum_).codec);
        }
        DensityFunctionTypes.register(registry, "spline", Spline.CODEC);
        DensityFunctionTypes.register(registry, "terrain_shaper_spline", TerrainShaperSpline.CODEC);
        DensityFunctionTypes.register(registry, "constant", Constant.CODEC);
        return DensityFunctionTypes.register(registry, "y_clamped_gradient", YClampedGradient.CODEC);
    }

    private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> registry, String id, Codec<? extends DensityFunction> codec) {
        return Registry.register(registry, id, codec);
    }

    static <A, O> Codec<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
        return codec.fieldOf("argument").xmap(function, function2).codec();
    }

    static <O> Codec<O> method_41069(Function<DensityFunction, O> function, Function<O, DensityFunction> function2) {
        return DensityFunctionTypes.method_41064(DensityFunction.field_37059, function, function2);
    }

    static <O> Codec<O> method_41068(BiFunction<DensityFunction, DensityFunction, O> biFunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function2) {
        return RecordCodecBuilder.create(instance -> instance.group((App)DensityFunction.field_37059.fieldOf("argument1").forGetter(function), (App)DensityFunction.field_37059.fieldOf("argument2").forGetter(function2)).apply((Applicative)instance, biFunction));
    }

    static <O> Codec<O> method_41065(MapCodec<O> mapCodec) {
        return mapCodec.codec();
    }

    private DensityFunctionTypes() {
    }

    public static DensityFunction interpolated(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.INTERPOLATED, inputFunction);
    }

    public static DensityFunction flatCache(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.FLAT_CACHE, inputFunction);
    }

    public static DensityFunction cache2d(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE2D, inputFunction);
    }

    public static DensityFunction cacheOnce(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE_ONCE, inputFunction);
    }

    public static DensityFunction cacheAllInCell(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE_ALL_IN_CELL, inputFunction);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double xzScale, double yScale, double d, double e) {
        return DensityFunctionTypes.method_40484(new Noise(noiseParameters, null, xzScale, yScale), d, e);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale, double d, double e) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, yScale, d, e);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, 1.0, d, e);
    }

    public static DensityFunction shiftedNoise(DensityFunction densityFunction, DensityFunction densityFunction2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftedNoise(densityFunction, DensityFunctionTypes.zero(), densityFunction2, d, 0.0, noiseParameters, null);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, 1.0);
    }

    public static DensityFunction method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double xzScale, double yScale) {
        return new Noise(noiseParameters, null, xzScale, yScale);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, yScale);
    }

    public static DensityFunction rangeChoice(DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        return new RangeChoice(densityFunction, d, e, densityFunction2, densityFunction3);
    }

    public static DensityFunction shiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftA(noiseParameters, null);
    }

    public static DensityFunction shiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftB(noiseParameters, null);
    }

    public static DensityFunction shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new Shift(noiseParameters, null);
    }

    public static DensityFunction blendDensity(DensityFunction densityFunction) {
        return new BlendDensity(densityFunction);
    }

    public static DensityFunction endIslands(long seed) {
        return new EndIslands(seed);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction densityFunction, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, WeirdScaledSampler.RarityValueMapper rarityValueMapper) {
        return new WeirdScaledSampler(densityFunction, registryEntry, null, rarityValueMapper);
    }

    public static DensityFunction slide(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
        return new Slide(generationShapeConfig, densityFunction);
    }

    public static DensityFunction method_40486(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.ADD, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40500(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MUL, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40505(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MIN, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40508(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MAX, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40489(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, TerrainShaperSpline.Spline spline, double d, double e) {
        return new TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, spline, d, e);
    }

    public static DensityFunction zero() {
        return Constant.ZERO;
    }

    public static DensityFunction constant(double density) {
        return new Constant(density);
    }

    public static DensityFunction yClampedGradient(int i, int j, double d, double e) {
        return new YClampedGradient(i, j, d, e);
    }

    public static DensityFunction method_40490(DensityFunction densityFunction, class_6925.Type type) {
        return class_6925.method_41079(type, densityFunction);
    }

    private static DensityFunction method_40484(DensityFunction densityFunction, double d, double e) {
        double f = (d + e) * 0.5;
        double g = (e - d) * 0.5;
        return DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(f), DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(g), densityFunction));
    }

    public static DensityFunction blendAlpha() {
        return BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return BlendOffset.INSTANCE;
    }

    public static DensityFunction method_40488(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        DensityFunction densityFunction4 = DensityFunctionTypes.cacheOnce(densityFunction);
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40500(densityFunction4, DensityFunctionTypes.constant(-1.0)), DensityFunctionTypes.constant(1.0));
        return DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40500(densityFunction2, densityFunction5), DensityFunctionTypes.method_40500(densityFunction3, densityFunction4));
    }

    protected static final class BlendAlpha
    extends Enum<BlendAlpha>
    implements DensityFunction.class_6913 {
        public static final /* enum */ BlendAlpha INSTANCE = new BlendAlpha();
        public static final Codec<DensityFunction> CODEC;
        private static final /* synthetic */ BlendAlpha[] field_36550;

        public static BlendAlpha[] values() {
            return (BlendAlpha[])field_36550.clone();
        }

        public static BlendAlpha valueOf(String string) {
            return Enum.valueOf(BlendAlpha.class, string);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return 1.0;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            Arrays.fill(ds, 1.0);
        }

        @Override
        public double minValue() {
            return 1.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        private static /* synthetic */ BlendAlpha[] method_40517() {
            return new BlendAlpha[]{INSTANCE};
        }

        static {
            field_36550 = BlendAlpha.method_40517();
            CODEC = Codec.unit((Object)INSTANCE);
        }
    }

    protected static final class BlendOffset
    extends Enum<BlendOffset>
    implements DensityFunction.class_6913 {
        public static final /* enum */ BlendOffset INSTANCE = new BlendOffset();
        public static final Codec<DensityFunction> CODEC;
        private static final /* synthetic */ BlendOffset[] field_36552;

        public static BlendOffset[] values() {
            return (BlendOffset[])field_36552.clone();
        }

        public static BlendOffset valueOf(String string) {
            return Enum.valueOf(BlendOffset.class, string);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return 0.0;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            Arrays.fill(ds, 0.0);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 0.0;
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        private static /* synthetic */ BlendOffset[] method_40519() {
            return new BlendOffset[]{INSTANCE};
        }

        static {
            field_36552 = BlendOffset.method_40519();
            CODEC = Codec.unit((Object)INSTANCE);
        }
    }

    protected static final class Beardifier
    extends Enum<Beardifier>
    implements class_7050 {
        public static final /* enum */ Beardifier INSTANCE = new Beardifier();
        private static final /* synthetic */ Beardifier[] field_37077;

        public static Beardifier[] values() {
            return (Beardifier[])field_37077.clone();
        }

        public static Beardifier valueOf(String string) {
            return Enum.valueOf(Beardifier.class, string);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return 0.0;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            Arrays.fill(ds, 0.0);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 0.0;
        }

        private static /* synthetic */ Beardifier[] method_41077() {
            return new Beardifier[]{INSTANCE};
        }

        static {
            field_37077 = Beardifier.method_41077();
        }
    }

    protected record class_6927(Type type, DensityFunction wrapped) implements Wrapper
    {
        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.wrapped.sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.wrapped.method_40470(ds, arg);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new class_6927(this.type, this.wrapped.apply(visitor)));
        }

        @Override
        public double minValue() {
            return this.wrapped.minValue();
        }

        @Override
        public double maxValue() {
            return this.wrapped.maxValue();
        }

        static final class Type
        extends Enum<Type>
        implements StringIdentifiable {
            public static final /* enum */ Type INTERPOLATED = new Type("interpolated");
            public static final /* enum */ Type FLAT_CACHE = new Type("flat_cache");
            public static final /* enum */ Type CACHE2D = new Type("cache_2d");
            public static final /* enum */ Type CACHE_ONCE = new Type("cache_once");
            public static final /* enum */ Type CACHE_ALL_IN_CELL = new Type("cache_all_in_cell");
            private final String name;
            final Codec<Wrapper> codec = DensityFunctionTypes.method_41069(densityFunction -> new class_6927(this, (DensityFunction)densityFunction), Wrapper::wrapped);
            private static final /* synthetic */ Type[] field_36567;

            public static Type[] values() {
                return (Type[])field_36567.clone();
            }

            public static Type valueOf(String string) {
                return Enum.valueOf(Type.class, string);
            }

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static /* synthetic */ Type[] method_40523() {
                return new Type[]{INTERPOLATED, FLAT_CACHE, CACHE2D, CACHE_ONCE, CACHE_ALL_IN_CELL};
            }

            static {
                field_36567 = Type.method_40523();
            }
        }
    }

    protected record Noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, @Deprecated double xzScale, double yScale) implements DensityFunction.class_6913
    {
        public static final MapCodec<Noise> field_37090 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(Noise::noiseData), (App)Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), (App)Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply((Applicative)instance, Noise::of));
        public static final Codec<Noise> CODEC = DensityFunctionTypes.method_41065(field_37090);

        public static Noise of(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Deprecated double xzScale, double yScale) {
            return new Noise(noiseData, null, xzScale, yScale);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.noise == null ? 0.0 : this.noise.sample((double)pos.blockX() * this.xzScale, (double)pos.blockY() * this.yScale, (double)pos.blockZ() * this.xzScale);
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise == null ? 2.0 : this.noise.method_40554();
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected static final class EndIslands
    implements DensityFunction.class_6913 {
        public static final Codec<EndIslands> CODEC = Codec.unit((Object)new EndIslands(0L));
        final SimplexNoiseSampler field_36554;

        public EndIslands(long seed) {
            AtomicSimpleRandom abstractRandom = new AtomicSimpleRandom(seed);
            abstractRandom.skip(17292);
            this.field_36554 = new SimplexNoiseSampler(abstractRandom);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return ((double)TheEndBiomeSource.getNoiseAt(this.field_36554, pos.blockX() / 8, pos.blockZ() / 8) - 8.0) / 128.0;
        }

        @Override
        public double minValue() {
            return -0.84375;
        }

        @Override
        public double maxValue() {
            return 0.5625;
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record WeirdScaledSampler(DensityFunction input, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, RarityValueMapper rarityValueMapper) implements class_6943
    {
        private static final MapCodec<WeirdScaledSampler> field_37065 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DensityFunction.field_37059.fieldOf("input").forGetter(WeirdScaledSampler::input), (App)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noiseData), (App)RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply((Applicative)instance, WeirdScaledSampler::create));
        public static final Codec<WeirdScaledSampler> CODEC = DensityFunctionTypes.method_41065(field_37065);

        public static WeirdScaledSampler create(DensityFunction input, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, RarityValueMapper rarityValueMapper) {
            return new WeirdScaledSampler(input, noiseData, null, rarityValueMapper);
        }

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            if (this.noise == null) {
                return 0.0;
            }
            double e = this.rarityValueMapper.scaleFunction.get(d);
            return e * Math.abs(this.noise.sample((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            this.input.apply(visitor);
            return (DensityFunction)visitor.apply(new WeirdScaledSampler(this.input.apply(visitor), this.noiseData, this.noise, this.rarityValueMapper));
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return this.rarityValueMapper.field_37072 * (this.noise == null ? 2.0 : this.noise.method_40554());
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        public static final class RarityValueMapper
        extends Enum<RarityValueMapper>
        implements StringIdentifiable {
            public static final /* enum */ RarityValueMapper TYPE1 = new RarityValueMapper("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0);
            public static final /* enum */ RarityValueMapper TYPE2 = new RarityValueMapper("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);
            private static final Map<String, RarityValueMapper> TYPES_MAP;
            public static final Codec<RarityValueMapper> CODEC;
            private final String name;
            final Double2DoubleFunction scaleFunction;
            final double field_37072;
            private static final /* synthetic */ RarityValueMapper[] field_37073;

            public static RarityValueMapper[] values() {
                return (RarityValueMapper[])field_37073.clone();
            }

            public static RarityValueMapper valueOf(String string) {
                return Enum.valueOf(RarityValueMapper.class, string);
            }

            private RarityValueMapper(String name, Double2DoubleFunction scaleFunction, double d) {
                this.name = name;
                this.scaleFunction = scaleFunction;
                this.field_37072 = d;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static /* synthetic */ RarityValueMapper[] method_41074() {
                return new RarityValueMapper[]{TYPE1, TYPE2};
            }

            static {
                field_37073 = RarityValueMapper.method_41074();
                TYPES_MAP = Arrays.stream(RarityValueMapper.values()).collect(Collectors.toMap(RarityValueMapper::asString, rarityValueMapper -> rarityValueMapper));
                CODEC = StringIdentifiable.createCodec(RarityValueMapper::values, TYPES_MAP::get);
            }
        }
    }

    protected record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise) implements DensityFunction
    {
        private static final MapCodec<ShiftedNoise> field_37098 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DensityFunction.field_37059.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), (App)DensityFunction.field_37059.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), (App)DensityFunction.field_37059.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), (App)Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), (App)Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), (App)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noiseData)).apply((Applicative)instance, ShiftedNoise::create));
        public static final Codec<ShiftedNoise> CODEC = DensityFunctionTypes.method_41065(field_37098);

        public static ShiftedNoise create(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData) {
            return new ShiftedNoise(shiftX, shiftY, shiftZ, xzScale, yScale, noiseData, null);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (this.noise == null) {
                return 0.0;
            }
            double d = (double)pos.blockX() * this.xzScale + this.shiftX.sample(pos);
            double e = (double)pos.blockY() * this.yScale + this.shiftY.sample(pos);
            double f = (double)pos.blockZ() * this.xzScale + this.shiftZ.sample(pos);
            return this.noise.sample(d, e, f);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new ShiftedNoise(this.shiftX.apply(visitor), this.shiftY.apply(visitor), this.shiftZ.apply(visitor), this.xzScale, this.yScale, this.noiseData, this.noise));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise == null ? 2.0 : this.noise.method_40554();
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction
    {
        public static final MapCodec<RangeChoice> field_37092 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DensityFunction.field_37059.fieldOf("input").forGetter(RangeChoice::input), (App)CONSTANT_RANGE.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), (App)CONSTANT_RANGE.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), (App)DensityFunction.field_37059.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), (App)DensityFunction.field_37059.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply((Applicative)instance, RangeChoice::new));
        public static final Codec<RangeChoice> CODEC = DensityFunctionTypes.method_41065(field_37092);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d = this.input.sample(pos);
            if (d >= this.minInclusive && d < this.maxExclusive) {
                return this.whenInRange.sample(pos);
            }
            return this.whenOutOfRange.sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input.method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                double d = ds[i];
                ds[i] = d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.sample(arg.method_40477(i)) : this.whenOutOfRange.sample(arg.method_40477(i));
            }
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new RangeChoice(this.input.apply(visitor), this.minInclusive, this.maxExclusive, this.whenInRange.apply(visitor), this.whenOutOfRange.apply(visitor)));
        }

        @Override
        public double minValue() {
            return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
        }

        @Override
        public double maxValue() {
            return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record ShiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<ShiftA> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new ShiftA((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), ShiftA::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), 0.0, pos.blockZ());
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new ShiftA(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record ShiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<ShiftB> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new ShiftB((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), ShiftB::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockZ(), pos.blockX(), 0.0);
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new ShiftB(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record Shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<Shift> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new Shift((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), Shift::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), pos.blockY(), pos.blockZ());
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new Shift(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record BlendDensity(DensityFunction input) implements class_6943
    {
        static final Codec<BlendDensity> CODEC = DensityFunctionTypes.method_41069(BlendDensity::new, BlendDensity::input);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            return noisePos.getBlender().method_39338(noisePos, d);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new BlendDensity(this.input.apply(visitor)));
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record Clamp(DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        private static final MapCodec<Clamp> field_37083 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DensityFunction.field_37057.fieldOf("input").forGetter(Clamp::input), (App)CONSTANT_RANGE.fieldOf("min").forGetter(Clamp::minValue), (App)CONSTANT_RANGE.fieldOf("max").forGetter(Clamp::maxValue)).apply((Applicative)instance, Clamp::new));
        public static final Codec<Clamp> CODEC = DensityFunctionTypes.method_41065(field_37083);

        @Override
        public double apply(double d) {
            return MathHelper.clamp(d, this.minValue, this.maxValue);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return new Clamp(this.input.apply(visitor), this.minValue, this.maxValue);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record class_6925(Type type, DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        public static class_6925 method_41079(Type type, DensityFunction densityFunction) {
            double d = densityFunction.minValue();
            double e = class_6925.method_40521(type, d);
            double f = class_6925.method_40521(type, densityFunction.maxValue());
            if (type == Type.ABS || type == Type.SQUARE) {
                return new class_6925(type, densityFunction, Math.max(0.0, d), Math.max(e, f));
            }
            return new class_6925(type, densityFunction, e, f);
        }

        private static double method_40521(Type type, double d) {
            return switch (type) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ABS -> Math.abs(d);
                case Type.SQUARE -> d * d;
                case Type.CUBE -> d * d * d;
                case Type.HALF_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.5;
                }
                case Type.QUARTER_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.25;
                }
                case Type.SQUEEZE -> {
                    double e = MathHelper.clamp(d, -1.0, 1.0);
                    yield e / 2.0 - e * e * e / 24.0;
                }
            };
        }

        @Override
        public double apply(double d) {
            return class_6925.method_40521(this.type, d);
        }

        @Override
        public class_6925 apply(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return class_6925.method_41079(this.type, this.input.apply(densityFunctionVisitor));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return this.type.codec;
        }

        @Override
        public /* synthetic */ DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return this.apply(visitor);
        }

        static final class Type
        extends Enum<Type>
        implements StringIdentifiable {
            public static final /* enum */ Type ABS = new Type("abs");
            public static final /* enum */ Type SQUARE = new Type("square");
            public static final /* enum */ Type CUBE = new Type("cube");
            public static final /* enum */ Type HALF_NEGATIVE = new Type("half_negative");
            public static final /* enum */ Type QUARTER_NEGATIVE = new Type("quarter_negative");
            public static final /* enum */ Type SQUEEZE = new Type("squeeze");
            private final String name;
            final Codec<class_6925> codec = DensityFunctionTypes.method_41069(densityFunction -> class_6925.method_41079(this, densityFunction), class_6925::input);
            private static final /* synthetic */ Type[] field_36561;

            public static Type[] values() {
                return (Type[])field_36561.clone();
            }

            public static Type valueOf(String string) {
                return Enum.valueOf(Type.class, string);
            }

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static /* synthetic */ Type[] method_40522() {
                return new Type[]{ABS, SQUARE, CUBE, HALF_NEGATIVE, QUARTER_NEGATIVE, SQUEEZE};
            }

            static {
                field_36561 = Type.method_40522();
            }
        }
    }

    protected record Slide(@Nullable GenerationShapeConfig settings, DensityFunction input) implements class_6943
    {
        public static final Codec<Slide> CODEC = DensityFunctionTypes.method_41069(densityFunction -> new Slide(null, (DensityFunction)densityFunction), Slide::input);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            if (this.settings == null) {
                return d;
            }
            return DensityFunctions.method_40542(this.settings, d, noisePos.blockY());
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new Slide(this.settings, this.input.apply(visitor)));
        }

        @Override
        public double minValue() {
            if (this.settings == null) {
                return this.input.minValue();
            }
            return Math.min(this.input.minValue(), Math.min(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }

        @Override
        public double maxValue() {
            if (this.settings == null) {
                return this.input.maxValue();
            }
            return Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    static interface Operation
    extends DensityFunction {
        public static final Logger LOGGER = LogUtils.getLogger();

        public static Operation create(Type type, DensityFunction argument1, DensityFunction argument2) {
            double i;
            double d = argument1.minValue();
            double e = argument2.minValue();
            double f = argument1.maxValue();
            double g = argument2.maxValue();
            if (type == Type.MIN || type == Type.MAX) {
                boolean bl2;
                boolean bl = d >= g;
                boolean bl3 = bl2 = e >= f;
                if (bl || bl2) {
                    LOGGER.warn("Creating a " + type + " function between two non-overlapping inputs: " + argument1 + " and " + argument2);
                }
            }
            double h = switch (type) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ADD -> d + e;
                case Type.MAX -> Math.max(d, e);
                case Type.MIN -> Math.min(d, e);
                case Type.MUL -> d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
            };
            switch (type) {
                default: {
                    throw new IncompatibleClassChangeError();
                }
                case ADD: {
                    double d2 = f + g;
                    break;
                }
                case MAX: {
                    double d2 = Math.max(f, g);
                    break;
                }
                case MIN: {
                    double d2 = Math.min(f, g);
                    break;
                }
                case MUL: {
                    double d2 = d > 0.0 && e > 0.0 ? f * g : (i = f < 0.0 && g < 0.0 ? d * e : Math.max(d * e, f * g));
                }
            }
            if (type == Type.MUL || type == Type.ADD) {
                if (argument1 instanceof Constant) {
                    Constant constant = (Constant)argument1;
                    return new class_6929(type == Type.ADD ? class_6929.SpecificType.ADD : class_6929.SpecificType.MUL, argument2, h, i, constant.value);
                }
                if (argument2 instanceof Constant) {
                    Constant constant = (Constant)argument2;
                    return new class_6929(type == Type.ADD ? class_6929.SpecificType.ADD : class_6929.SpecificType.MUL, argument1, h, i, constant.value);
                }
            }
            return new class_6917(type, argument1, argument2, h, i);
        }

        public Type type();

        public DensityFunction argument1();

        public DensityFunction argument2();

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }

        public static final class Type
        extends Enum<Type>
        implements StringIdentifiable {
            public static final /* enum */ Type ADD = new Type("add");
            public static final /* enum */ Type MUL = new Type("mul");
            public static final /* enum */ Type MIN = new Type("min");
            public static final /* enum */ Type MAX = new Type("max");
            final Codec<Operation> codec = DensityFunctionTypes.method_41068((densityFunction, densityFunction2) -> Operation.create(this, densityFunction, densityFunction2), Operation::argument1, Operation::argument2);
            private final String name;
            private static final /* synthetic */ Type[] field_36548;

            public static Type[] values() {
                return (Type[])field_36548.clone();
            }

            public static Type valueOf(String string) {
                return Enum.valueOf(Type.class, string);
            }

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static /* synthetic */ Type[] method_40516() {
                return new Type[]{ADD, MUL, MIN, MAX};
            }

            static {
                field_36548 = Type.method_40516();
            }
        }
    }

    public record Spline(net.minecraft.util.math.Spline<VanillaTerrainParameters.class_7075> spline, double minValue, double maxValue) implements DensityFunction
    {
        private static final MapCodec<Spline> field_37256 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)VanillaTerrainParameters.field_37252.fieldOf("spline").forGetter(Spline::spline), (App)CONSTANT_RANGE.fieldOf("min_value").forGetter(Spline::minValue), (App)CONSTANT_RANGE.fieldOf("max_value").forGetter(Spline::maxValue)).apply((Applicative)instance, Spline::new));
        public static final Codec<Spline> CODEC = DensityFunctionTypes.method_41065(field_37256);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return MathHelper.clamp((double)this.spline.apply(VanillaTerrainParameters.method_41191(pos)), this.minValue, this.maxValue);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new Spline(this.spline.method_41187(toFloatFunction -> {
                ToFloatFunction toFloatFunction2;
                if (toFloatFunction instanceof VanillaTerrainParameters.class_7074) {
                    VanillaTerrainParameters.class_7074 lv = (VanillaTerrainParameters.class_7074)toFloatFunction;
                    toFloatFunction2 = lv.method_41194(visitor);
                } else {
                    toFloatFunction2 = toFloatFunction;
                }
                return toFloatFunction2;
            }), this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    @Deprecated
    public record TerrainShaperSpline(DensityFunction continentalness, DensityFunction erosion, DensityFunction weirdness, @Nullable VanillaTerrainParameters shaper, Spline spline, double minValue, double maxValue) implements DensityFunction
    {
        private static final MapCodec<TerrainShaperSpline> field_37101 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DensityFunction.field_37059.fieldOf("continentalness").forGetter(TerrainShaperSpline::continentalness), (App)DensityFunction.field_37059.fieldOf("erosion").forGetter(TerrainShaperSpline::erosion), (App)DensityFunction.field_37059.fieldOf("weirdness").forGetter(TerrainShaperSpline::weirdness), (App)Spline.CODEC.fieldOf("spline").forGetter(TerrainShaperSpline::spline), (App)CONSTANT_RANGE.fieldOf("min_value").forGetter(TerrainShaperSpline::minValue), (App)CONSTANT_RANGE.fieldOf("max_value").forGetter(TerrainShaperSpline::maxValue)).apply((Applicative)instance, TerrainShaperSpline::method_41094));
        public static final Codec<TerrainShaperSpline> CODEC = DensityFunctionTypes.method_41065(field_37101);

        public static TerrainShaperSpline method_41094(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, Spline spline, double d, double e) {
            return new TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, spline, d, e);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (this.shaper == null) {
                return 0.0;
            }
            return MathHelper.clamp((double)this.spline.field_37108.apply(this.shaper, VanillaTerrainParameters.createNoisePoint((float)this.continentalness.sample(pos), (float)this.erosion.sample(pos), (float)this.weirdness.sample(pos))), this.minValue, this.maxValue);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.sample(arg.method_40477(i));
            }
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new TerrainShaperSpline(this.continentalness.apply(visitor), this.erosion.apply(visitor), this.weirdness.apply(visitor), this.shaper, this.spline, this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        public static final class Spline
        extends Enum<Spline>
        implements StringIdentifiable {
            public static final /* enum */ Spline OFFSET = new Spline("offset", VanillaTerrainParameters::getOffset);
            public static final /* enum */ Spline FACTOR = new Spline("factor", VanillaTerrainParameters::getFactor);
            public static final /* enum */ Spline JAGGEDNESS = new Spline("jaggedness", VanillaTerrainParameters::getPeak);
            private static final Map<String, Spline> field_37106;
            public static final Codec<Spline> CODEC;
            private final String name;
            final class_7053 field_37108;
            private static final /* synthetic */ Spline[] field_37109;

            public static Spline[] values() {
                return (Spline[])field_37109.clone();
            }

            public static Spline valueOf(String string) {
                return Enum.valueOf(Spline.class, string);
            }

            private Spline(String name, class_7053 arg) {
                this.name = name;
                this.field_37108 = arg;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static /* synthetic */ Spline[] method_41095() {
                return new Spline[]{OFFSET, FACTOR, JAGGEDNESS};
            }

            static {
                field_37109 = Spline.method_41095();
                field_37106 = Arrays.stream(Spline.values()).collect(Collectors.toMap(Spline::asString, spline -> spline));
                CODEC = StringIdentifiable.createCodec(Spline::values, field_37106::get);
            }
        }

        static interface class_7053 {
            public float apply(VanillaTerrainParameters var1, VanillaTerrainParameters.NoisePoint var2);
        }
    }

    static final class Constant
    extends Record
    implements DensityFunction.class_6913 {
        final double value;
        static final Codec<Constant> CODEC = DensityFunctionTypes.method_41064(CONSTANT_RANGE, Constant::new, Constant::value);
        static final Constant ZERO = new Constant(0.0);

        Constant(double d) {
            this.value = d;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.value;
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            Arrays.fill(ds, this.value);
        }

        @Override
        public double minValue() {
            return this.value;
        }

        @Override
        public double maxValue() {
            return this.value;
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Constant.class, "value", "value"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Constant.class, "value", "value"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Constant.class, "value", "value"}, this, object);
        }

        public double value() {
            return this.value;
        }
    }

    record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.class_6913
    {
        private static final MapCodec<YClampedGradient> field_37075 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.intRange((int)(DimensionType.MIN_HEIGHT * 2), (int)(DimensionType.MAX_COLUMN_HEIGHT * 2)).fieldOf("from_y").forGetter(YClampedGradient::fromY), (App)Codec.intRange((int)(DimensionType.MIN_HEIGHT * 2), (int)(DimensionType.MAX_COLUMN_HEIGHT * 2)).fieldOf("to_y").forGetter(YClampedGradient::toY), (App)CONSTANT_RANGE.fieldOf("from_value").forGetter(YClampedGradient::fromValue), (App)CONSTANT_RANGE.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply((Applicative)instance, YClampedGradient::new));
        public static final Codec<YClampedGradient> CODEC = DensityFunctionTypes.method_41065(field_37075);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return MathHelper.clampedLerpFromProgress((double)pos.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
        }

        @Override
        public double minValue() {
            return Math.min(this.fromValue, this.toValue);
        }

        @Override
        public double maxValue() {
            return Math.max(this.fromValue, this.toValue);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record class_6917(Operation.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements Operation
    {
        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d = this.argument1.sample(pos);
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case Operation.Type.ADD -> d + this.argument2.sample(pos);
                case Operation.Type.MUL -> {
                    if (d == 0.0) {
                        yield 0.0;
                    }
                    yield d * this.argument2.sample(pos);
                }
                case Operation.Type.MIN -> {
                    if (d < this.argument2.minValue()) {
                        yield d;
                    }
                    yield Math.min(d, this.argument2.sample(pos));
                }
                case Operation.Type.MAX -> d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
            };
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.argument1.method_40470(ds, arg);
            switch (this.type) {
                case ADD: {
                    double[] es = new double[ds.length];
                    this.argument2.method_40470(es, arg);
                    for (int i = 0; i < ds.length; ++i) {
                        ds[i] = ds[i] + es[i];
                    }
                    break;
                }
                case MUL: {
                    for (int j = 0; j < ds.length; ++j) {
                        double d = ds[j];
                        ds[j] = d == 0.0 ? 0.0 : d * this.argument2.sample(arg.method_40477(j));
                    }
                    break;
                }
                case MIN: {
                    double e = this.argument2.minValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f < e ? f : Math.min(f, this.argument2.sample(arg.method_40477(k)));
                    }
                    break;
                }
                case MAX: {
                    double e = this.argument2.maxValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f > e ? f : Math.max(f, this.argument2.sample(arg.method_40477(k)));
                    }
                    break;
                }
            }
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(Operation.create(this.type, this.argument1.apply(visitor), this.argument2.apply(visitor)));
        }
    }

    record class_6929(SpecificType specificType, DensityFunction input, double minValue, double maxValue, double argument) implements Operation,
    class_6932
    {
        @Override
        public Operation.Type type() {
            return this.specificType == SpecificType.MUL ? Operation.Type.MUL : Operation.Type.ADD;
        }

        @Override
        public DensityFunction argument1() {
            return DensityFunctionTypes.constant(this.argument);
        }

        @Override
        public DensityFunction argument2() {
            return this.input;
        }

        @Override
        public double apply(double d) {
            return switch (this.specificType) {
                default -> throw new IncompatibleClassChangeError();
                case SpecificType.MUL -> d * this.argument;
                case SpecificType.ADD -> d + this.argument;
            };
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            double g;
            double f;
            DensityFunction densityFunction = this.input.apply(visitor);
            double d = densityFunction.minValue();
            double e = densityFunction.maxValue();
            if (this.specificType == SpecificType.ADD) {
                f = d + this.argument;
                g = e + this.argument;
            } else if (this.argument >= 0.0) {
                f = d * this.argument;
                g = e * this.argument;
            } else {
                f = e * this.argument;
                g = d * this.argument;
            }
            return new class_6929(this.specificType, densityFunction, f, g, this.argument);
        }

        static final class SpecificType
        extends Enum<SpecificType> {
            public static final /* enum */ SpecificType MUL = new SpecificType();
            public static final /* enum */ SpecificType ADD = new SpecificType();
            private static final /* synthetic */ SpecificType[] field_36570;

            public static SpecificType[] values() {
                return (SpecificType[])field_36570.clone();
            }

            public static SpecificType valueOf(String string) {
                return Enum.valueOf(SpecificType.class, string);
            }

            private static /* synthetic */ SpecificType[] method_40524() {
                return new SpecificType[]{MUL, ADD};
            }

            static {
                field_36570 = SpecificType.method_40524();
            }
        }
    }

    static interface class_6939
    extends DensityFunction.class_6913 {
        public RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData();

        @Nullable
        public DoublePerlinNoiseSampler offsetNoise();

        @Override
        default public double minValue() {
            return -this.maxValue();
        }

        @Override
        default public double maxValue() {
            DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
            return (doublePerlinNoiseSampler == null ? 2.0 : doublePerlinNoiseSampler.method_40554()) * 4.0;
        }

        default public double method_40525(double d, double e, double f) {
            DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
            return doublePerlinNoiseSampler == null ? 0.0 : doublePerlinNoiseSampler.sample(d * 0.25, e * 0.25, f * 0.25) * 4.0;
        }

        public class_6939 method_41086(DoublePerlinNoiseSampler var1);
    }

    public static interface Wrapper
    extends DensityFunction {
        public class_6927.Type type();

        public DensityFunction wrapped();

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }
    }

    protected record class_7051(RegistryEntry<DensityFunction> function) implements DensityFunction
    {
        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.function.value().sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.function.value().method_40470(ds, arg);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return (DensityFunction)visitor.apply(new class_7051(new RegistryEntry.Direct<DensityFunction>(this.function.value().apply(visitor))));
        }

        @Override
        public double minValue() {
            return this.function.value().minValue();
        }

        @Override
        public double maxValue() {
            return this.function.value().maxValue();
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
        }
    }

    public static interface class_7050
    extends DensityFunction.class_6913 {
        public static final Codec<DensityFunction> CODEC = Codec.unit((Object)Beardifier.INSTANCE);

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    static interface class_6932
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double sample(DensityFunction.NoisePos pos) {
            return this.apply(this.input().sample(pos));
        }

        @Override
        default public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.apply(ds[i]);
            }
        }

        public double apply(double var1);
    }

    static interface class_6943
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double sample(DensityFunction.NoisePos pos) {
            return this.method_40518(pos, this.input().sample(pos));
        }

        @Override
        default public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.method_40518(arg.method_40477(i), ds[i]);
            }
        }

        public double method_40518(DensityFunction.NoisePos var1, double var2);
    }
}

