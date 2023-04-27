/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.joml.Math
 */
package net.minecraft.util.math;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
    private static final long field_29852 = 61440L;
    private static final long HALF_PI_RADIANS_SINE_TABLE_INDEX = 16384L;
    private static final long field_29854 = -4611686018427387904L;
    private static final long field_29855 = Long.MIN_VALUE;
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = 1.5707964f;
    public static final float TAU = (float)Math.PI * 2;
    public static final float RADIANS_PER_DEGREE = (float)Math.PI / 180;
    public static final float DEGREES_PER_RADIAN = 57.295776f;
    public static final float EPSILON = 1.0E-5f;
    public static final float SQUARE_ROOT_OF_TWO = MathHelper.sqrt(2.0f);
    private static final float DEGREES_TO_SINE_TABLE_INDEX = 10430.378f;
    private static final float[] SINE_TABLE = Util.make(new float[65536], sineTable -> {
        for (int i = 0; i < ((float[])sineTable).length; ++i) {
            sineTable[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
    });
    private static final Random RANDOM = Random.createThreadSafe();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double field_29857 = 0.16666666666666666;
    private static final int field_29858 = 8;
    private static final int field_29859 = 257;
    private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ARCSINE_TABLE = new double[257];
    private static final double[] COSINE_TABLE = new double[257];

    public static float sin(float value) {
        return SINE_TABLE[(int)(value * 10430.378f) & 0xFFFF];
    }

    public static float cos(float value) {
        return SINE_TABLE[(int)(value * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float value) {
        return (float)Math.sqrt(value);
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static long lfloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        if (delta > 1.0) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
    }

    public static float clampedLerp(float start, float end, float delta) {
        if (delta < 0.0f) {
            return start;
        }
        if (delta > 1.0f) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
    }

    public static double absMax(double a, double b) {
        if (a < 0.0) {
            a = -a;
        }
        if (b < 0.0) {
            b = -b;
        }
        return Math.max(a, b);
    }

    public static int floorDiv(int dividend, int divisor) {
        return Math.floorDiv(dividend, divisor);
    }

    public static int nextInt(Random random, int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        if (min >= max) {
            return min;
        }
        return random.nextFloat() * (max - min) + min;
    }

    public static double nextDouble(Random random, double min, double max) {
        if (min >= max) {
            return min;
        }
        return random.nextDouble() * (max - min) + min;
    }

    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < 1.0E-5f;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < (double)1.0E-5f;
    }

    public static int floorMod(int dividend, int divisor) {
        return Math.floorMod(dividend, divisor);
    }

    public static float floorMod(float dividend, float divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    public static double floorMod(double dividend, double divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    public static boolean isMultipleOf(int a, int b) {
        return a % b == 0;
    }

    public static int wrapDegrees(int degrees) {
        int i = degrees % 360;
        if (i >= 180) {
            i -= 360;
        }
        if (i < -180) {
            i += 360;
        }
        return i;
    }

    public static float wrapDegrees(float degrees) {
        float f = degrees % 360.0f;
        if (f >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    public static double wrapDegrees(double degrees) {
        double d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static float subtractAngles(float start, float end) {
        return MathHelper.wrapDegrees(end - start);
    }

    public static float angleBetween(float first, float second) {
        return MathHelper.abs(MathHelper.subtractAngles(first, second));
    }

    public static float clampAngle(float value, float mean, float delta) {
        float f = MathHelper.subtractAngles(value, mean);
        float g = MathHelper.clamp(f, -delta, delta);
        return mean - g;
    }

    public static float stepTowards(float from, float to, float step) {
        step = MathHelper.abs(step);
        if (from < to) {
            return MathHelper.clamp(from + step, from, to);
        }
        return MathHelper.clamp(from - step, to, from);
    }

    public static float stepUnwrappedAngleTowards(float from, float to, float step) {
        float f = MathHelper.subtractAngles(from, to);
        return MathHelper.stepTowards(from, from + f, step);
    }

    public static int parseInt(String string, int fallback) {
        return NumberUtils.toInt((String)string, (int)fallback);
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int ceilLog2(int value) {
        value = MathHelper.isPowerOfTwo(value) ? value : MathHelper.smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 0x1F];
    }

    public static int floorLog2(int value) {
        return MathHelper.ceilLog2(value) - (MathHelper.isPowerOfTwo(value) ? 0 : 1);
    }

    public static int packRgb(float r, float g, float b) {
        return ColorHelper.Argb.getArgb(0, MathHelper.floor(r * 255.0f), MathHelper.floor(g * 255.0f), MathHelper.floor(b * 255.0f));
    }

    public static float fractionalPart(float value) {
        return value - (float)MathHelper.floor(value);
    }

    public static double fractionalPart(double value) {
        return value - (double)MathHelper.lfloor(value);
    }

    @Deprecated
    public static long hashCode(Vec3i vec) {
        return MathHelper.hashCode(vec.getX(), vec.getY(), vec.getZ());
    }

    @Deprecated
    public static long hashCode(int x, int y, int z) {
        long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID randomUuid(Random random) {
        long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long m = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(l, m);
    }

    public static UUID randomUuid() {
        return MathHelper.randomUuid(RANDOM);
    }

    public static double getLerpProgress(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public static float getLerpProgress(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    public static boolean method_34945(Vec3d vec3d, Vec3d vec3d2, Box box) {
        double d = (box.minX + box.maxX) * 0.5;
        double e = (box.maxX - box.minX) * 0.5;
        double f = vec3d.x - d;
        if (Math.abs(f) > e && f * vec3d2.x >= 0.0) {
            return false;
        }
        double g = (box.minY + box.maxY) * 0.5;
        double h = (box.maxY - box.minY) * 0.5;
        double i = vec3d.y - g;
        if (Math.abs(i) > h && i * vec3d2.y >= 0.0) {
            return false;
        }
        double j = (box.minZ + box.maxZ) * 0.5;
        double k = (box.maxZ - box.minZ) * 0.5;
        double l = vec3d.z - j;
        if (Math.abs(l) > k && l * vec3d2.z >= 0.0) {
            return false;
        }
        double m = Math.abs(vec3d2.x);
        double n = Math.abs(vec3d2.y);
        double o = Math.abs(vec3d2.z);
        double p = vec3d2.y * l - vec3d2.z * i;
        if (Math.abs(p) > h * o + k * n) {
            return false;
        }
        p = vec3d2.z * f - vec3d2.x * l;
        if (Math.abs(p) > e * o + k * m) {
            return false;
        }
        p = vec3d2.x * i - vec3d2.y * f;
        return Math.abs(p) < e * n + h * m;
    }

    public static double atan2(double y, double x) {
        double e;
        boolean bl3;
        boolean bl2;
        boolean bl;
        double d = x * x + y * y;
        if (Double.isNaN(d)) {
            return Double.NaN;
        }
        boolean bl4 = bl = y < 0.0;
        if (bl) {
            y = -y;
        }
        boolean bl5 = bl2 = x < 0.0;
        if (bl2) {
            x = -x;
        }
        boolean bl6 = bl3 = y > x;
        if (bl3) {
            e = x;
            x = y;
            y = e;
        }
        e = MathHelper.fastInverseSqrt(d);
        x *= e;
        double f = SMALLEST_FRACTION_FREE_DOUBLE + (y *= e);
        int i = (int)Double.doubleToRawLongBits(f);
        double g = ARCSINE_TABLE[i];
        double h = COSINE_TABLE[i];
        double j = f - SMALLEST_FRACTION_FREE_DOUBLE;
        double k = y * h - x * j;
        double l = (6.0 + k * k) * k * 0.16666666666666666;
        double m = g + l;
        if (bl3) {
            m = 1.5707963267948966 - m;
        }
        if (bl2) {
            m = Math.PI - m;
        }
        if (bl) {
            m = -m;
        }
        return m;
    }

    public static float inverseSqrt(float x) {
        return org.joml.Math.invsqrt((float)x);
    }

    public static double inverseSqrt(double x) {
        return org.joml.Math.invsqrt((double)x);
    }

    @Deprecated
    public static double fastInverseSqrt(double x) {
        double d = 0.5 * x;
        long l = Double.doubleToRawLongBits(x);
        l = 6910469410427058090L - (l >> 1);
        x = Double.longBitsToDouble(l);
        x *= 1.5 - d * x * x;
        return x;
    }

    public static float fastInverseCbrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        return f;
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        float l;
        float k;
        int i = (int)(hue * 6.0f) % 6;
        float f = hue * 6.0f - (float)i;
        float g = value * (1.0f - saturation);
        float h = value * (1.0f - f * saturation);
        float j = value * (1.0f - (1.0f - f) * saturation);
        return ColorHelper.Argb.getArgb(0, MathHelper.clamp((int)(k * 255.0f), 0, 255), MathHelper.clamp((int)(l * 255.0f), 0, 255), MathHelper.clamp((int)((switch (i) {
            case 0 -> {
                k = value;
                l = j;
                yield g;
            }
            case 1 -> {
                k = h;
                l = value;
                yield g;
            }
            case 2 -> {
                k = g;
                l = value;
                yield j;
            }
            case 3 -> {
                k = g;
                l = h;
                yield value;
            }
            case 4 -> {
                k = j;
                l = g;
                yield value;
            }
            case 5 -> {
                k = value;
                l = g;
                yield h;
            }
            default -> throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }) * 255.0f), 0, 255));
    }

    public static int idealHash(int value) {
        value ^= value >>> 16;
        value *= -2048144789;
        value ^= value >>> 13;
        value *= -1028477387;
        value ^= value >>> 16;
        return value;
    }

    public static int binarySearch(int min, int max, IntPredicate predicate) {
        int i = max - min;
        while (i > 0) {
            int j = i / 2;
            int k = min + j;
            if (predicate.test(k)) {
                i = j;
                continue;
            }
            min = k + 1;
            i -= j + 1;
        }
        return min;
    }

    public static int lerp(float delta, int start, int end) {
        return start + MathHelper.floor(delta * (float)(end - start));
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return MathHelper.lerp(deltaY, MathHelper.lerp(deltaX, x0y0, x1y0), MathHelper.lerp(deltaX, x0y1, x1y1));
    }

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return MathHelper.lerp(deltaZ, MathHelper.lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), MathHelper.lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    public static float catmullRom(float delta, float p0, float p1, float p2, float p3) {
        return 0.5f * (2.0f * p1 + (p2 - p0) * delta + (2.0f * p0 - 5.0f * p1 + 4.0f * p2 - p3) * delta * delta + (3.0f * p1 - p0 - 3.0f * p2 + p3) * delta * delta * delta);
    }

    public static double perlinFade(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

    public static double perlinFadeDerivative(double value) {
        return 30.0 * value * value * (value - 1.0) * (value - 1.0);
    }

    public static int sign(double value) {
        if (value == 0.0) {
            return 0;
        }
        return value > 0.0 ? 1 : -1;
    }

    public static float lerpAngleDegrees(float delta, float start, float end) {
        return start + delta * MathHelper.wrapDegrees(end - start);
    }

    public static float wrap(float value, float maxDeviation) {
        return (Math.abs(value % maxDeviation - maxDeviation * 0.5f) - maxDeviation * 0.25f) / (maxDeviation * 0.25f);
    }

    public static float square(float n) {
        return n * n;
    }

    public static double square(double n) {
        return n * n;
    }

    public static int square(int n) {
        return n * n;
    }

    public static long square(long n) {
        return n * n;
    }

    public static double clampedMap(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
        return MathHelper.clampedLerp(newStart, newEnd, MathHelper.getLerpProgress(value, oldStart, oldEnd));
    }

    public static float clampedMap(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
        return MathHelper.clampedLerp(newStart, newEnd, MathHelper.getLerpProgress(value, oldStart, oldEnd));
    }

    public static double map(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
        return MathHelper.lerp(MathHelper.getLerpProgress(value, oldStart, oldEnd), newStart, newEnd);
    }

    public static float map(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
        return MathHelper.lerp(MathHelper.getLerpProgress(value, oldStart, oldEnd), newStart, newEnd);
    }

    public static double method_34957(double d) {
        return d + (2.0 * Random.create(MathHelper.floor(d * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
    }

    public static int roundUpToMultiple(int value, int divisor) {
        return MathHelper.ceilDiv(value, divisor) * divisor;
    }

    public static int ceilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    public static int nextBetween(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static float nextGaussian(Random random, float mean, float deviation) {
        return mean + (float)random.nextGaussian() * deviation;
    }

    public static double squaredHypot(double a, double b) {
        return a * a + b * b;
    }

    public static double hypot(double a, double b) {
        return Math.sqrt(MathHelper.squaredHypot(a, b));
    }

    public static double squaredMagnitude(double a, double b, double c) {
        return a * a + b * b + c * c;
    }

    public static double magnitude(double a, double b, double c) {
        return Math.sqrt(MathHelper.squaredMagnitude(a, b, c));
    }

    public static int roundDownToMultiple(double a, int b) {
        return MathHelper.floor(a / (double)b) * b;
    }

    public static IntStream stream(int seed, int lowerBound, int upperBound) {
        return MathHelper.stream(seed, lowerBound, upperBound, 1);
    }

    public static IntStream stream(int seed, int lowerBound, int upperBound, int steps) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", upperBound, lowerBound));
        }
        if (steps < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", steps));
        }
        if (seed < lowerBound || seed > upperBound) {
            return IntStream.empty();
        }
        return IntStream.iterate(seed, i -> {
            int m = Math.abs(seed - i);
            return seed - m >= lowerBound || seed + m <= upperBound;
        }, i -> {
            int o;
            boolean bl2;
            boolean bl = i <= seed;
            int n = Math.abs(seed - i);
            boolean bl3 = bl2 = seed + n + steps <= upperBound;
            if (!(bl && bl2 || (o = seed - n - (bl ? steps : 0)) < lowerBound)) {
                return o;
            }
            return seed + n + steps;
        });
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double)i / 256.0;
            double e = Math.asin(d);
            MathHelper.COSINE_TABLE[i] = Math.cos(e);
            MathHelper.ARCSINE_TABLE[i] = e;
        }
    }
}

