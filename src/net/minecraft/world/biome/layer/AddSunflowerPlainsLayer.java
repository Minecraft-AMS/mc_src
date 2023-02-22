/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddSunflowerPlainsLayer
extends Enum<AddSunflowerPlainsLayer>
implements SouthEastSamplingLayer {
    public static final /* enum */ AddSunflowerPlainsLayer INSTANCE = new AddSunflowerPlainsLayer();
    private static final /* synthetic */ AddSunflowerPlainsLayer[] field_16152;

    public static AddSunflowerPlainsLayer[] values() {
        return (AddSunflowerPlainsLayer[])field_16152.clone();
    }

    public static AddSunflowerPlainsLayer valueOf(String string) {
        return Enum.valueOf(AddSunflowerPlainsLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (context.nextInt(57) == 0 && se == 1) {
            return 129;
        }
        return se;
    }

    private static /* synthetic */ AddSunflowerPlainsLayer[] method_36779() {
        return new AddSunflowerPlainsLayer[]{INSTANCE};
    }

    static {
        field_16152 = AddSunflowerPlainsLayer.method_36779();
    }
}

