/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class ContinentLayer
extends Enum<ContinentLayer>
implements InitLayer {
    public static final /* enum */ ContinentLayer INSTANCE = new ContinentLayer();
    private static final /* synthetic */ ContinentLayer[] field_16104;

    public static ContinentLayer[] values() {
        return (ContinentLayer[])field_16104.clone();
    }

    public static ContinentLayer valueOf(String string) {
        return Enum.valueOf(ContinentLayer.class, string);
    }

    @Override
    public int sample(LayerRandomnessSource context, int x, int y) {
        if (x == 0 && y == 0) {
            return 1;
        }
        return context.nextInt(10) == 0 ? 1 : 0;
    }

    private static /* synthetic */ ContinentLayer[] method_36774() {
        return new ContinentLayer[]{INSTANCE};
    }

    static {
        field_16104 = ContinentLayer.method_36774();
    }
}

