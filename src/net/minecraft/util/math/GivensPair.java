/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Math
 *  org.joml.Matrix3f
 *  org.joml.Quaternionf
 */
package net.minecraft.util.math;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

public record GivensPair(float sinHalf, float cosHalf) {
    public static GivensPair normalize(float a, float b) {
        float f = Math.invsqrt((float)(a * a + b * b));
        return new GivensPair(f * a, f * b);
    }

    public static GivensPair fromAngle(float radians) {
        float f = Math.sin((float)(radians / 2.0f));
        float g = Math.cosFromSin((float)f, (float)(radians / 2.0f));
        return new GivensPair(f, g);
    }

    public GivensPair negateSin() {
        return new GivensPair(-this.sinHalf, this.cosHalf);
    }

    public Quaternionf method_49729(Quaternionf quaternionf) {
        return quaternionf.set(this.sinHalf, 0.0f, 0.0f, this.cosHalf);
    }

    public Quaternionf method_49732(Quaternionf quaternionf) {
        return quaternionf.set(0.0f, this.sinHalf, 0.0f, this.cosHalf);
    }

    public Quaternionf method_49735(Quaternionf quaternionf) {
        return quaternionf.set(0.0f, 0.0f, this.sinHalf, this.cosHalf);
    }

    public float cosDouble() {
        return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf;
    }

    public float sinDouble() {
        return 2.0f * this.sinHalf * this.cosHalf;
    }

    public Matrix3f method_49728(Matrix3f matrix3f) {
        matrix3f.m01 = 0.0f;
        matrix3f.m02 = 0.0f;
        matrix3f.m10 = 0.0f;
        matrix3f.m20 = 0.0f;
        float f = this.cosDouble();
        float g = this.sinDouble();
        matrix3f.m11 = f;
        matrix3f.m22 = f;
        matrix3f.m12 = g;
        matrix3f.m21 = -g;
        matrix3f.m00 = 1.0f;
        return matrix3f;
    }

    public Matrix3f method_49731(Matrix3f matrix3f) {
        matrix3f.m01 = 0.0f;
        matrix3f.m10 = 0.0f;
        matrix3f.m12 = 0.0f;
        matrix3f.m21 = 0.0f;
        float f = this.cosDouble();
        float g = this.sinDouble();
        matrix3f.m00 = f;
        matrix3f.m22 = f;
        matrix3f.m02 = -g;
        matrix3f.m20 = g;
        matrix3f.m11 = 1.0f;
        return matrix3f;
    }

    public Matrix3f method_49734(Matrix3f matrix3f) {
        matrix3f.m02 = 0.0f;
        matrix3f.m12 = 0.0f;
        matrix3f.m20 = 0.0f;
        matrix3f.m21 = 0.0f;
        float f = this.cosDouble();
        float g = this.sinDouble();
        matrix3f.m00 = f;
        matrix3f.m11 = f;
        matrix3f.m01 = g;
        matrix3f.m10 = -g;
        matrix3f.m22 = 1.0f;
        return matrix3f;
    }
}

