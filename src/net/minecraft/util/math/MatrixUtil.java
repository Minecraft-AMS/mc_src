/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Matrix4x3f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class MatrixUtil {
    private static final float field_40746 = 3.0f + 2.0f * (float)Math.sqrt(2.0);
    private static final float COS_PI_OVER_8 = (float)Math.cos(0.39269908169872414);
    private static final float SIN_PI_OVER_8 = (float)Math.sin(0.39269908169872414);

    private MatrixUtil() {
    }

    public static Matrix4f scale(Matrix4f matrix, float scalar) {
        return matrix.set(matrix.m00() * scalar, matrix.m01() * scalar, matrix.m02() * scalar, matrix.m03() * scalar, matrix.m10() * scalar, matrix.m11() * scalar, matrix.m12() * scalar, matrix.m13() * scalar, matrix.m20() * scalar, matrix.m21() * scalar, matrix.m22() * scalar, matrix.m23() * scalar, matrix.m30() * scalar, matrix.m31() * scalar, matrix.m32() * scalar, matrix.m33() * scalar);
    }

    private static Pair<Float, Float> method_46411(float f, float g, float h) {
        float j = g;
        float i = 2.0f * (f - h);
        if (field_40746 * j * j < i * i) {
            float k = MathHelper.fastInverseSqrt(j * j + i * i);
            return Pair.of((Object)Float.valueOf(k * j), (Object)Float.valueOf(k * i));
        }
        return Pair.of((Object)Float.valueOf(SIN_PI_OVER_8), (Object)Float.valueOf(COS_PI_OVER_8));
    }

    private static Pair<Float, Float> method_46410(float f, float g) {
        float k;
        float h = (float)Math.hypot(f, g);
        float i = h > 1.0E-6f ? g : 0.0f;
        float j = Math.abs(f) + Math.max(h, 1.0E-6f);
        if (f < 0.0f) {
            k = i;
            i = j;
            j = k;
        }
        k = MathHelper.fastInverseSqrt(j * j + i * i);
        return Pair.of((Object)Float.valueOf(i *= k), (Object)Float.valueOf(j *= k));
    }

    private static Quaternionf method_46415(Matrix3f matrix3f) {
        float h;
        float g;
        float f;
        Quaternionf quaternionf2;
        Float float2;
        Float float_;
        Pair<Float, Float> pair;
        Matrix3f matrix3f2 = new Matrix3f();
        Quaternionf quaternionf = new Quaternionf();
        if (matrix3f.m01 * matrix3f.m01 + matrix3f.m10 * matrix3f.m10 > 1.0E-6f) {
            pair = MatrixUtil.method_46411(matrix3f.m00, 0.5f * (matrix3f.m01 + matrix3f.m10), matrix3f.m11);
            float_ = (Float)pair.getFirst();
            float2 = (Float)pair.getSecond();
            quaternionf2 = new Quaternionf(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternionf.mul((Quaternionfc)quaternionf2);
            matrix3f2.m00 = f;
            matrix3f2.m11 = f;
            matrix3f2.m01 = -g;
            matrix3f2.m10 = g;
            matrix3f2.m22 = h;
            matrix3f.mul((Matrix3fc)matrix3f2);
            matrix3f2.transpose();
            matrix3f2.mul((Matrix3fc)matrix3f);
            matrix3f.set((Matrix3fc)matrix3f2);
        }
        if (matrix3f.m02 * matrix3f.m02 + matrix3f.m20 * matrix3f.m20 > 1.0E-6f) {
            pair = MatrixUtil.method_46411(matrix3f.m00, 0.5f * (matrix3f.m02 + matrix3f.m20), matrix3f.m22);
            float i = -((Float)pair.getFirst()).floatValue();
            float2 = (Float)pair.getSecond();
            quaternionf2 = new Quaternionf(0.0f, i, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - i * i;
            g = -2.0f * i * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + i * i;
            quaternionf.mul((Quaternionfc)quaternionf2);
            matrix3f2.m00 = f;
            matrix3f2.m22 = f;
            matrix3f2.m02 = g;
            matrix3f2.m20 = -g;
            matrix3f2.m11 = h;
            matrix3f.mul((Matrix3fc)matrix3f2);
            matrix3f2.transpose();
            matrix3f2.mul((Matrix3fc)matrix3f);
            matrix3f.set((Matrix3fc)matrix3f2);
        }
        if (matrix3f.m12 * matrix3f.m12 + matrix3f.m21 * matrix3f.m21 > 1.0E-6f) {
            pair = MatrixUtil.method_46411(matrix3f.m11, 0.5f * (matrix3f.m12 + matrix3f.m21), matrix3f.m22);
            float_ = (Float)pair.getFirst();
            float2 = (Float)pair.getSecond();
            quaternionf2 = new Quaternionf(float_.floatValue(), 0.0f, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternionf.mul((Quaternionfc)quaternionf2);
            matrix3f2.m11 = f;
            matrix3f2.m22 = f;
            matrix3f2.m12 = -g;
            matrix3f2.m21 = g;
            matrix3f2.m00 = h;
            matrix3f.mul((Matrix3fc)matrix3f2);
            matrix3f2.transpose();
            matrix3f2.mul((Matrix3fc)matrix3f);
            matrix3f.set((Matrix3fc)matrix3f2);
        }
        return quaternionf;
    }

    public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f matrix3f) {
        Quaternionf quaternionf = new Quaternionf();
        Quaternionf quaternionf2 = new Quaternionf();
        Matrix3f matrix3f2 = new Matrix3f((Matrix3fc)matrix3f);
        matrix3f2.transpose();
        matrix3f2.mul((Matrix3fc)matrix3f);
        for (int i = 0; i < 5; ++i) {
            quaternionf2.mul((Quaternionfc)MatrixUtil.method_46415(matrix3f2));
        }
        quaternionf2.normalize();
        Matrix3f matrix3f3 = new Matrix3f((Matrix3fc)matrix3f);
        matrix3f3.rotate((Quaternionfc)quaternionf2);
        float f = 1.0f;
        Pair<Float, Float> pair = MatrixUtil.method_46410(matrix3f3.m00, matrix3f3.m01);
        Float float_ = (Float)pair.getFirst();
        Float float2 = (Float)pair.getSecond();
        float g = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
        float h = -2.0f * float_.floatValue() * float2.floatValue();
        float j = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
        Quaternionf quaternionf3 = new Quaternionf(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
        quaternionf.mul((Quaternionfc)quaternionf3);
        Matrix3f matrix3f4 = new Matrix3f();
        matrix3f4.m00 = g;
        matrix3f4.m11 = g;
        matrix3f4.m01 = h;
        matrix3f4.m10 = -h;
        matrix3f4.m22 = j;
        f *= j;
        matrix3f4.mul((Matrix3fc)matrix3f3);
        pair = MatrixUtil.method_46410(matrix3f4.m00, matrix3f4.m02);
        float k = -((Float)pair.getFirst()).floatValue();
        Float float3 = (Float)pair.getSecond();
        float l = float3.floatValue() * float3.floatValue() - k * k;
        float m = -2.0f * k * float3.floatValue();
        float n = float3.floatValue() * float3.floatValue() + k * k;
        Quaternionf quaternionf4 = new Quaternionf(0.0f, k, 0.0f, float3.floatValue());
        quaternionf.mul((Quaternionfc)quaternionf4);
        Matrix3f matrix3f5 = new Matrix3f();
        matrix3f5.m00 = l;
        matrix3f5.m22 = l;
        matrix3f5.m02 = -m;
        matrix3f5.m20 = m;
        matrix3f5.m11 = n;
        f *= n;
        matrix3f5.mul((Matrix3fc)matrix3f4);
        pair = MatrixUtil.method_46410(matrix3f5.m11, matrix3f5.m12);
        Float float4 = (Float)pair.getFirst();
        Float float5 = (Float)pair.getSecond();
        float o = float5.floatValue() * float5.floatValue() - float4.floatValue() * float4.floatValue();
        float p = -2.0f * float4.floatValue() * float5.floatValue();
        float q = float5.floatValue() * float5.floatValue() + float4.floatValue() * float4.floatValue();
        Quaternionf quaternionf5 = new Quaternionf(float4.floatValue(), 0.0f, 0.0f, float5.floatValue());
        quaternionf.mul((Quaternionfc)quaternionf5);
        Matrix3f matrix3f6 = new Matrix3f();
        matrix3f6.m11 = o;
        matrix3f6.m22 = o;
        matrix3f6.m12 = p;
        matrix3f6.m21 = -p;
        matrix3f6.m00 = q;
        f *= q;
        matrix3f6.mul((Matrix3fc)matrix3f5);
        f = 1.0f / f;
        quaternionf.mul((float)Math.sqrt(f));
        Vector3f vector3f = new Vector3f(matrix3f6.m00 * f, matrix3f6.m11 * f, matrix3f6.m22 * f);
        return Triple.of((Object)quaternionf, (Object)vector3f, (Object)quaternionf2);
    }

    public static Matrix4x3f affineTransform(Matrix4f matrix) {
        float f = 1.0f / matrix.m33();
        return new Matrix4x3f().set((Matrix4fc)matrix).scaleLocal(f, f, f);
    }
}

