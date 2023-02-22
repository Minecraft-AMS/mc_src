/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class AddBambooJungleLayer
extends Enum<AddBambooJungleLayer>
implements SouthEastSamplingLayer {
    public static final /* enum */ AddBambooJungleLayer INSTANCE = new AddBambooJungleLayer();
    private static final /* synthetic */ AddBambooJungleLayer[] field_16117;

    public static AddBambooJungleLayer[] values() {
        return (AddBambooJungleLayer[])field_16117.clone();
    }

    public static AddBambooJungleLayer valueOf(String string) {
        return Enum.valueOf(AddBambooJungleLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (context.nextInt(10) == 0 && se == 21) {
            return 168;
        }
        return se;
    }

    private static /* synthetic */ AddBambooJungleLayer[] method_36778() {
        return new AddBambooJungleLayer[]{INSTANCE};
    }

    static {
        field_16117 = AddBambooJungleLayer.method_36778();
    }
}

