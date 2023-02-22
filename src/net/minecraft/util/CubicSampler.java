/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class CubicSampler {
    private static final double[] DENSITY_CURVE = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

    @NotNull
    @Environment(value=EnvType.CLIENT)
    public static Vec3d sampleColor(Vec3d pos, RgbFetcher rgbFetcher) {
        int i = MathHelper.floor(pos.getX());
        int j = MathHelper.floor(pos.getY());
        int k = MathHelper.floor(pos.getZ());
        double d = pos.getX() - (double)i;
        double e = pos.getY() - (double)j;
        double f = pos.getZ() - (double)k;
        double g = 0.0;
        Vec3d vec3d = Vec3d.ZERO;
        for (int l = 0; l < 6; ++l) {
            double h = MathHelper.lerp(d, DENSITY_CURVE[l + 1], DENSITY_CURVE[l]);
            int m = i - 2 + l;
            for (int n = 0; n < 6; ++n) {
                double o = MathHelper.lerp(e, DENSITY_CURVE[n + 1], DENSITY_CURVE[n]);
                int p = j - 2 + n;
                for (int q = 0; q < 6; ++q) {
                    double r = MathHelper.lerp(f, DENSITY_CURVE[q + 1], DENSITY_CURVE[q]);
                    int s = k - 2 + q;
                    double t = h * o * r;
                    g += t;
                    vec3d = vec3d.add(rgbFetcher.fetch(m, p, s).multiply(t));
                }
            }
        }
        vec3d = vec3d.multiply(1.0 / g);
        return vec3d;
    }

    public static interface RgbFetcher {
        public Vec3d fetch(int var1, int var2, int var3);
    }
}
