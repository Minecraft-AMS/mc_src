/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.noise;

public class NoiseHelper {
    public static double method_35479(double d, double e) {
        return d + Math.sin(Math.PI * d) * e / Math.PI;
    }

    public static void appendDebugInfo(StringBuilder builder, double originX, double originY, double originZ, byte[] permutations) {
        builder.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", Float.valueOf((float)originX), Float.valueOf((float)originY), Float.valueOf((float)originZ), permutations[0], permutations[255]));
    }

    public static void appendDebugInfo(StringBuilder builder, double originX, double originY, double originZ, int[] permutations) {
        builder.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", Float.valueOf((float)originX), Float.valueOf((float)originY), Float.valueOf((float)originZ), permutations[0], permutations[255]));
    }
}

