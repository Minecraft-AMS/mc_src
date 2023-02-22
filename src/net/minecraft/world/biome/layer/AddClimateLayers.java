/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class AddClimateLayers {

    public static final class AddSpecialBiomesLayer
    extends Enum<AddSpecialBiomesLayer>
    implements IdentitySamplingLayer {
        public static final /* enum */ AddSpecialBiomesLayer INSTANCE = new AddSpecialBiomesLayer();
        private static final /* synthetic */ AddSpecialBiomesLayer[] field_16050;

        public static AddSpecialBiomesLayer[] values() {
            return (AddSpecialBiomesLayer[])field_16050.clone();
        }

        public static AddSpecialBiomesLayer valueOf(String string) {
            return Enum.valueOf(AddSpecialBiomesLayer.class, string);
        }

        @Override
        public int sample(LayerRandomnessSource context, int value) {
            if (!BiomeLayers.isShallowOcean(value) && context.nextInt(13) == 0) {
                value |= 1 + context.nextInt(15) << 8 & 0xF00;
            }
            return value;
        }

        private static /* synthetic */ AddSpecialBiomesLayer[] method_36769() {
            return new AddSpecialBiomesLayer[]{INSTANCE};
        }

        static {
            field_16050 = AddSpecialBiomesLayer.method_36769();
        }
    }

    public static final class AddCoolBiomesLayer
    extends Enum<AddCoolBiomesLayer>
    implements CrossSamplingLayer {
        public static final /* enum */ AddCoolBiomesLayer INSTANCE = new AddCoolBiomesLayer();
        private static final /* synthetic */ AddCoolBiomesLayer[] field_17402;

        public static AddCoolBiomesLayer[] values() {
            return (AddCoolBiomesLayer[])field_17402.clone();
        }

        public static AddCoolBiomesLayer valueOf(String string) {
            return Enum.valueOf(AddCoolBiomesLayer.class, string);
        }

        @Override
        public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
            if (center == 4 && (n == 1 || e == 1 || w == 1 || s == 1 || n == 2 || e == 2 || w == 2 || s == 2)) {
                return 3;
            }
            return center;
        }

        private static /* synthetic */ AddCoolBiomesLayer[] method_36768() {
            return new AddCoolBiomesLayer[]{INSTANCE};
        }

        static {
            field_17402 = AddCoolBiomesLayer.method_36768();
        }
    }

    public static final class AddTemperateBiomesLayer
    extends Enum<AddTemperateBiomesLayer>
    implements CrossSamplingLayer {
        public static final /* enum */ AddTemperateBiomesLayer INSTANCE = new AddTemperateBiomesLayer();
        private static final /* synthetic */ AddTemperateBiomesLayer[] field_17400;

        public static AddTemperateBiomesLayer[] values() {
            return (AddTemperateBiomesLayer[])field_17400.clone();
        }

        public static AddTemperateBiomesLayer valueOf(String string) {
            return Enum.valueOf(AddTemperateBiomesLayer.class, string);
        }

        @Override
        public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
            if (center == 1 && (n == 3 || e == 3 || w == 3 || s == 3 || n == 4 || e == 4 || w == 4 || s == 4)) {
                return 2;
            }
            return center;
        }

        private static /* synthetic */ AddTemperateBiomesLayer[] method_36767() {
            return new AddTemperateBiomesLayer[]{INSTANCE};
        }

        static {
            field_17400 = AddTemperateBiomesLayer.method_36767();
        }
    }
}

