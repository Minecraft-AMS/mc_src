/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Math
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.util.math;

import net.minecraft.util.math.GivensPair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class MatrixUtil {
    private static final float COT_PI_OVER_8 = 3.0f + 2.0f * Math.sqrt((float)2.0f);
    private static final GivensPair SIN_COS_PI_OVER_8 = GivensPair.fromAngle(0.7853982f);

    private MatrixUtil() {
    }

    public static Matrix4f scale(Matrix4f matrix, float scalar) {
        return matrix.set(matrix.m00() * scalar, matrix.m01() * scalar, matrix.m02() * scalar, matrix.m03() * scalar, matrix.m10() * scalar, matrix.m11() * scalar, matrix.m12() * scalar, matrix.m13() * scalar, matrix.m20() * scalar, matrix.m21() * scalar, matrix.m22() * scalar, matrix.m23() * scalar, matrix.m30() * scalar, matrix.m31() * scalar, matrix.m32() * scalar, matrix.m33() * scalar);
    }

    private static GivensPair approximateGivensQuaternion(float a11, float a12, float a22) {
        float g = a12;
        float f = 2.0f * (a11 - a22);
        if (COT_PI_OVER_8 * g * g < f * f) {
            return GivensPair.normalize(g, f);
        }
        return SIN_COS_PI_OVER_8;
    }

    private static GivensPair qrGivensQuaternion(float a1, float a2) {
        float f = (float)java.lang.Math.hypot(a1, a2);
        float g = f > 1.0E-6f ? a2 : 0.0f;
        float h = Math.abs((float)a1) + Math.max((float)f, (float)1.0E-6f);
        if (a1 < 0.0f) {
            float i = g;
            g = h;
            h = i;
        }
        return GivensPair.normalize(g, h);
    }

    private static void method_49742(Matrix3f matrix3f, Matrix3f matrix3f2) {
        matrix3f.mul((Matrix3fc)matrix3f2);
        matrix3f2.transpose();
        matrix3f2.mul((Matrix3fc)matrix3f);
        matrix3f.set((Matrix3fc)matrix3f2);
    }

    private static void applyJacobiIteration(Matrix3f matrix3f, Matrix3f matrix3f2, Quaternionf quaternionf, Quaternionf quaternionf2) {
        Quaternionf quaternionf3;
        GivensPair givensPair;
        if (matrix3f.m01 * matrix3f.m01 + matrix3f.m10 * matrix3f.m10 > 1.0E-6f) {
            givensPair = MatrixUtil.approximateGivensQuaternion(matrix3f.m00, 0.5f * (matrix3f.m01 + matrix3f.m10), matrix3f.m11);
            quaternionf3 = givensPair.method_49735(quaternionf);
            quaternionf2.mul((Quaternionfc)quaternionf3);
            givensPair.method_49734(matrix3f2);
            MatrixUtil.method_49742(matrix3f, matrix3f2);
        }
        if (matrix3f.m02 * matrix3f.m02 + matrix3f.m20 * matrix3f.m20 > 1.0E-6f) {
            givensPair = MatrixUtil.approximateGivensQuaternion(matrix3f.m00, 0.5f * (matrix3f.m02 + matrix3f.m20), matrix3f.m22).negateSin();
            quaternionf3 = givensPair.method_49732(quaternionf);
            quaternionf2.mul((Quaternionfc)quaternionf3);
            givensPair.method_49731(matrix3f2);
            MatrixUtil.method_49742(matrix3f, matrix3f2);
        }
        if (matrix3f.m12 * matrix3f.m12 + matrix3f.m21 * matrix3f.m21 > 1.0E-6f) {
            givensPair = MatrixUtil.approximateGivensQuaternion(matrix3f.m11, 0.5f * (matrix3f.m12 + matrix3f.m21), matrix3f.m22);
            quaternionf3 = givensPair.method_49729(quaternionf);
            quaternionf2.mul((Quaternionfc)quaternionf3);
            givensPair.method_49728(matrix3f2);
            MatrixUtil.method_49742(matrix3f, matrix3f2);
        }
    }

    public static Quaternionf method_49741(Matrix3f matrix3f, int i) {
        Quaternionf quaternionf = new Quaternionf();
        Matrix3f matrix3f2 = new Matrix3f();
        Quaternionf quaternionf2 = new Quaternionf();
        for (int j = 0; j < i; ++j) {
            MatrixUtil.applyJacobiIteration(matrix3f, matrix3f2, quaternionf2, quaternionf);
        }
        quaternionf.normalize();
        return quaternionf;
    }

    public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f A) {
        Matrix3f matrix3f = new Matrix3f((Matrix3fc)A);
        matrix3f.transpose();
        matrix3f.mul((Matrix3fc)A);
        Quaternionf quaternionf = MatrixUtil.method_49741(matrix3f, 5);
        float f = matrix3f.m00;
        float g = matrix3f.m11;
        boolean bl = (double)f < 1.0E-6;
        boolean bl2 = (double)g < 1.0E-6;
        Matrix3f matrix3f2 = matrix3f;
        Matrix3f matrix3f3 = A.rotate((Quaternionfc)quaternionf);
        Quaternionf quaternionf2 = new Quaternionf();
        Quaternionf quaternionf3 = new Quaternionf();
        GivensPair givensPair = bl ? MatrixUtil.qrGivensQuaternion(matrix3f3.m11, -matrix3f3.m10) : MatrixUtil.qrGivensQuaternion(matrix3f3.m00, matrix3f3.m01);
        Quaternionf quaternionf4 = givensPair.method_49735(quaternionf3);
        Matrix3f matrix3f4 = givensPair.method_49734(matrix3f2);
        quaternionf2.mul((Quaternionfc)quaternionf4);
        matrix3f4.transpose().mul((Matrix3fc)matrix3f3);
        matrix3f2 = matrix3f3;
        givensPair = bl ? MatrixUtil.qrGivensQuaternion(matrix3f4.m22, -matrix3f4.m20) : MatrixUtil.qrGivensQuaternion(matrix3f4.m00, matrix3f4.m02);
        givensPair = givensPair.negateSin();
        Quaternionf quaternionf5 = givensPair.method_49732(quaternionf3);
        Matrix3f matrix3f5 = givensPair.method_49731(matrix3f2);
        quaternionf2.mul((Quaternionfc)quaternionf5);
        matrix3f5.transpose().mul((Matrix3fc)matrix3f4);
        matrix3f2 = matrix3f4;
        givensPair = bl2 ? MatrixUtil.qrGivensQuaternion(matrix3f5.m22, -matrix3f5.m21) : MatrixUtil.qrGivensQuaternion(matrix3f5.m11, matrix3f5.m12);
        Quaternionf quaternionf6 = givensPair.method_49729(quaternionf3);
        Matrix3f matrix3f6 = givensPair.method_49728(matrix3f2);
        quaternionf2.mul((Quaternionfc)quaternionf6);
        matrix3f6.transpose().mul((Matrix3fc)matrix3f5);
        Vector3f vector3f = new Vector3f(matrix3f6.m00, matrix3f6.m11, matrix3f6.m22);
        return Triple.of((Object)quaternionf2, (Object)vector3f, (Object)quaternionf.conjugate());
    }
}

