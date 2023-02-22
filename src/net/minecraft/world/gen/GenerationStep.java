/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class GenerationStep {

    public static final class Carver
    extends Enum<Carver>
    implements StringIdentifiable {
        public static final /* enum */ Carver AIR = new Carver("air");
        public static final /* enum */ Carver LIQUID = new Carver("liquid");
        public static final Codec<Carver> CODEC;
        private static final Map<String, Carver> BY_NAME;
        private final String name;
        private static final /* synthetic */ Carver[] field_13170;

        public static Carver[] values() {
            return (Carver[])field_13170.clone();
        }

        public static Carver valueOf(String string) {
            return Enum.valueOf(Carver.class, string);
        }

        private Carver(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Carver byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ Carver[] method_36750() {
            return new Carver[]{AIR, LIQUID};
        }

        static {
            field_13170 = Carver.method_36750();
            CODEC = StringIdentifiable.createCodec(Carver::values, Carver::byName);
            BY_NAME = Arrays.stream(Carver.values()).collect(Collectors.toMap(Carver::getName, carver -> carver));
        }
    }

    public static final class Feature
    extends Enum<Feature> {
        public static final /* enum */ Feature RAW_GENERATION = new Feature();
        public static final /* enum */ Feature LAKES = new Feature();
        public static final /* enum */ Feature LOCAL_MODIFICATIONS = new Feature();
        public static final /* enum */ Feature UNDERGROUND_STRUCTURES = new Feature();
        public static final /* enum */ Feature SURFACE_STRUCTURES = new Feature();
        public static final /* enum */ Feature STRONGHOLDS = new Feature();
        public static final /* enum */ Feature UNDERGROUND_ORES = new Feature();
        public static final /* enum */ Feature UNDERGROUND_DECORATION = new Feature();
        public static final /* enum */ Feature VEGETAL_DECORATION = new Feature();
        public static final /* enum */ Feature TOP_LAYER_MODIFICATION = new Feature();
        private static final /* synthetic */ Feature[] field_13181;

        public static Feature[] values() {
            return (Feature[])field_13181.clone();
        }

        public static Feature valueOf(String string) {
            return Enum.valueOf(Feature.class, string);
        }

        private static /* synthetic */ Feature[] method_36751() {
            return new Feature[]{RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION};
        }

        static {
            field_13181 = Feature.method_36751();
        }
    }
}

