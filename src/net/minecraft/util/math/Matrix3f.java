/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.apache.commons.lang3.tuple.Triple
 */
package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import java.nio.FloatBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.tuple.Triple;

public final class Matrix3f {
    private static final int ORDER = 3;
    private static final float THREE_PLUS_TWO_SQRT_TWO = 3.0f + 2.0f * (float)Math.sqrt(2.0);
    private static final float COS_PI_OVER_EIGHT = (float)Math.cos(0.39269908169872414);
    private static final float SIN_PI_OVER_EIGHT = (float)Math.sin(0.39269908169872414);
    private static final float SQRT_HALF = 1.0f / (float)Math.sqrt(2.0);
    protected float a00;
    protected float a01;
    protected float a02;
    protected float a10;
    protected float a11;
    protected float a12;
    protected float a20;
    protected float a21;
    protected float a22;

    public Matrix3f() {
    }

    public Matrix3f(Quaternion quaternion) {
        float f = quaternion.getX();
        float g = quaternion.getY();
        float h = quaternion.getZ();
        float i = quaternion.getW();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        this.a00 = 1.0f - k - l;
        this.a11 = 1.0f - l - j;
        this.a22 = 1.0f - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.a10 = 2.0f * (m + r);
        this.a01 = 2.0f * (m - r);
        this.a20 = 2.0f * (o - q);
        this.a02 = 2.0f * (o + q);
        this.a21 = 2.0f * (n + p);
        this.a12 = 2.0f * (n - p);
    }

    public static Matrix3f scale(float x, float y, float z) {
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.a00 = x;
        matrix3f.a11 = y;
        matrix3f.a22 = z;
        return matrix3f;
    }

    public Matrix3f(Matrix4f matrix) {
        this.a00 = matrix.a00;
        this.a01 = matrix.a01;
        this.a02 = matrix.a02;
        this.a10 = matrix.a10;
        this.a11 = matrix.a11;
        this.a12 = matrix.a12;
        this.a20 = matrix.a20;
        this.a21 = matrix.a21;
        this.a22 = matrix.a22;
    }

    public Matrix3f(Matrix3f source) {
        this.a00 = source.a00;
        this.a01 = source.a01;
        this.a02 = source.a02;
        this.a10 = source.a10;
        this.a11 = source.a11;
        this.a12 = source.a12;
        this.a20 = source.a20;
        this.a21 = source.a21;
        this.a22 = source.a22;
    }

    private static Pair<Float, Float> getSinAndCosOfRotation(float upperLeft, float diagonalAverage, float lowerRight) {
        float g = diagonalAverage;
        float f = 2.0f * (upperLeft - lowerRight);
        if (THREE_PLUS_TWO_SQRT_TWO * g * g < f * f) {
            float h = MathHelper.fastInverseSqrt(g * g + f * f);
            return Pair.of((Object)Float.valueOf(h * g), (Object)Float.valueOf(h * f));
        }
        return Pair.of((Object)Float.valueOf(SIN_PI_OVER_EIGHT), (Object)Float.valueOf(COS_PI_OVER_EIGHT));
    }

    private static Pair<Float, Float> method_22848(float f, float g) {
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

    private static Quaternion method_22857(Matrix3f matrix) {
        float h;
        float g;
        float f;
        Quaternion quaternion2;
        Float float2;
        Float float_;
        Pair<Float, Float> pair;
        Matrix3f matrix3f = new Matrix3f();
        Quaternion quaternion = Quaternion.IDENTITY.copy();
        if (matrix.a01 * matrix.a01 + matrix.a10 * matrix.a10 > 1.0E-6f) {
            pair = Matrix3f.getSinAndCosOfRotation(matrix.a00, 0.5f * (matrix.a01 + matrix.a10), matrix.a11);
            float_ = (Float)pair.getFirst();
            float2 = (Float)pair.getSecond();
            quaternion2 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.hamiltonProduct(quaternion2);
            matrix3f.loadIdentity();
            matrix3f.a00 = f;
            matrix3f.a11 = f;
            matrix3f.a10 = -g;
            matrix3f.a01 = g;
            matrix3f.a22 = h;
            matrix.multiply(matrix3f);
            matrix3f.transpose();
            matrix3f.multiply(matrix);
            matrix.load(matrix3f);
        }
        if (matrix.a02 * matrix.a02 + matrix.a20 * matrix.a20 > 1.0E-6f) {
            pair = Matrix3f.getSinAndCosOfRotation(matrix.a00, 0.5f * (matrix.a02 + matrix.a20), matrix.a22);
            float i = -((Float)pair.getFirst()).floatValue();
            float2 = (Float)pair.getSecond();
            quaternion2 = new Quaternion(0.0f, i, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - i * i;
            g = -2.0f * i * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + i * i;
            quaternion.hamiltonProduct(quaternion2);
            matrix3f.loadIdentity();
            matrix3f.a00 = f;
            matrix3f.a22 = f;
            matrix3f.a20 = g;
            matrix3f.a02 = -g;
            matrix3f.a11 = h;
            matrix.multiply(matrix3f);
            matrix3f.transpose();
            matrix3f.multiply(matrix);
            matrix.load(matrix3f);
        }
        if (matrix.a12 * matrix.a12 + matrix.a21 * matrix.a21 > 1.0E-6f) {
            pair = Matrix3f.getSinAndCosOfRotation(matrix.a11, 0.5f * (matrix.a12 + matrix.a21), matrix.a22);
            float_ = (Float)pair.getFirst();
            float2 = (Float)pair.getSecond();
            quaternion2 = new Quaternion(float_.floatValue(), 0.0f, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.hamiltonProduct(quaternion2);
            matrix3f.loadIdentity();
            matrix3f.a11 = f;
            matrix3f.a22 = f;
            matrix3f.a21 = -g;
            matrix3f.a12 = g;
            matrix3f.a00 = h;
            matrix.multiply(matrix3f);
            matrix3f.transpose();
            matrix3f.multiply(matrix);
            matrix.load(matrix3f);
        }
        return quaternion;
    }

    private static void method_35260(Matrix3f matrix, Quaternion quaternion) {
        Quaternion quaternion2;
        float i;
        float f = matrix.a00 * matrix.a00 + matrix.a10 * matrix.a10 + matrix.a20 * matrix.a20;
        float g = matrix.a01 * matrix.a01 + matrix.a11 * matrix.a11 + matrix.a21 * matrix.a21;
        float h = matrix.a02 * matrix.a02 + matrix.a12 * matrix.a12 + matrix.a22 * matrix.a22;
        if (f < g) {
            i = matrix.a10;
            matrix.a10 = -matrix.a00;
            matrix.a00 = i;
            i = matrix.a11;
            matrix.a11 = -matrix.a01;
            matrix.a01 = i;
            i = matrix.a12;
            matrix.a12 = -matrix.a02;
            matrix.a02 = i;
            quaternion2 = new Quaternion(0.0f, 0.0f, SQRT_HALF, SQRT_HALF);
            quaternion.hamiltonProduct(quaternion2);
            i = f;
            f = g;
            g = i;
        }
        if (f < h) {
            i = matrix.a20;
            matrix.a20 = -matrix.a00;
            matrix.a00 = i;
            i = matrix.a21;
            matrix.a21 = -matrix.a01;
            matrix.a01 = i;
            i = matrix.a22;
            matrix.a22 = -matrix.a02;
            matrix.a02 = i;
            quaternion2 = new Quaternion(0.0f, SQRT_HALF, 0.0f, SQRT_HALF);
            quaternion.hamiltonProduct(quaternion2);
            h = f;
        }
        if (g < h) {
            i = matrix.a20;
            matrix.a20 = -matrix.a10;
            matrix.a10 = i;
            i = matrix.a21;
            matrix.a21 = -matrix.a11;
            matrix.a11 = i;
            i = matrix.a22;
            matrix.a22 = -matrix.a12;
            matrix.a12 = i;
            quaternion2 = new Quaternion(SQRT_HALF, 0.0f, 0.0f, SQRT_HALF);
            quaternion.hamiltonProduct(quaternion2);
        }
    }

    public void transpose() {
        float f = this.a01;
        this.a01 = this.a10;
        this.a10 = f;
        f = this.a02;
        this.a02 = this.a20;
        this.a20 = f;
        f = this.a12;
        this.a12 = this.a21;
        this.a21 = f;
    }

    public Triple<Quaternion, Vec3f, Quaternion> decomposeLinearTransformation() {
        Quaternion quaternion = Quaternion.IDENTITY.copy();
        Quaternion quaternion2 = Quaternion.IDENTITY.copy();
        Matrix3f matrix3f = this.copy();
        matrix3f.transpose();
        matrix3f.multiply(this);
        for (int i = 0; i < 5; ++i) {
            quaternion2.hamiltonProduct(Matrix3f.method_22857(matrix3f));
        }
        quaternion2.normalize();
        Matrix3f matrix3f2 = new Matrix3f(this);
        matrix3f2.multiply(new Matrix3f(quaternion2));
        float f = 1.0f;
        Pair<Float, Float> pair = Matrix3f.method_22848(matrix3f2.a00, matrix3f2.a10);
        Float float_ = (Float)pair.getFirst();
        Float float2 = (Float)pair.getSecond();
        float g = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
        float h = -2.0f * float_.floatValue() * float2.floatValue();
        float j = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
        Quaternion quaternion3 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
        quaternion.hamiltonProduct(quaternion3);
        Matrix3f matrix3f3 = new Matrix3f();
        matrix3f3.loadIdentity();
        matrix3f3.a00 = g;
        matrix3f3.a11 = g;
        matrix3f3.a10 = h;
        matrix3f3.a01 = -h;
        matrix3f3.a22 = j;
        f *= j;
        matrix3f3.multiply(matrix3f2);
        pair = Matrix3f.method_22848(matrix3f3.a00, matrix3f3.a20);
        float k = -((Float)pair.getFirst()).floatValue();
        Float float3 = (Float)pair.getSecond();
        float l = float3.floatValue() * float3.floatValue() - k * k;
        float m = -2.0f * k * float3.floatValue();
        float n = float3.floatValue() * float3.floatValue() + k * k;
        Quaternion quaternion4 = new Quaternion(0.0f, k, 0.0f, float3.floatValue());
        quaternion.hamiltonProduct(quaternion4);
        Matrix3f matrix3f4 = new Matrix3f();
        matrix3f4.loadIdentity();
        matrix3f4.a00 = l;
        matrix3f4.a22 = l;
        matrix3f4.a20 = -m;
        matrix3f4.a02 = m;
        matrix3f4.a11 = n;
        f *= n;
        matrix3f4.multiply(matrix3f3);
        pair = Matrix3f.method_22848(matrix3f4.a11, matrix3f4.a21);
        Float float4 = (Float)pair.getFirst();
        Float float5 = (Float)pair.getSecond();
        float o = float5.floatValue() * float5.floatValue() - float4.floatValue() * float4.floatValue();
        float p = -2.0f * float4.floatValue() * float5.floatValue();
        float q = float5.floatValue() * float5.floatValue() + float4.floatValue() * float4.floatValue();
        Quaternion quaternion5 = new Quaternion(float4.floatValue(), 0.0f, 0.0f, float5.floatValue());
        quaternion.hamiltonProduct(quaternion5);
        Matrix3f matrix3f5 = new Matrix3f();
        matrix3f5.loadIdentity();
        matrix3f5.a11 = o;
        matrix3f5.a22 = o;
        matrix3f5.a21 = p;
        matrix3f5.a12 = -p;
        matrix3f5.a00 = q;
        f *= q;
        matrix3f5.multiply(matrix3f4);
        f = 1.0f / f;
        quaternion.scale((float)Math.sqrt(f));
        Vec3f vec3f = new Vec3f(matrix3f5.a00 * f, matrix3f5.a11 * f, matrix3f5.a22 * f);
        return Triple.of((Object)quaternion, (Object)vec3f, (Object)quaternion2);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Matrix3f matrix3f = (Matrix3f)o;
        return Float.compare(matrix3f.a00, this.a00) == 0 && Float.compare(matrix3f.a01, this.a01) == 0 && Float.compare(matrix3f.a02, this.a02) == 0 && Float.compare(matrix3f.a10, this.a10) == 0 && Float.compare(matrix3f.a11, this.a11) == 0 && Float.compare(matrix3f.a12, this.a12) == 0 && Float.compare(matrix3f.a20, this.a20) == 0 && Float.compare(matrix3f.a21, this.a21) == 0 && Float.compare(matrix3f.a22, this.a22) == 0;
    }

    public int hashCode() {
        int i = this.a00 != 0.0f ? Float.floatToIntBits(this.a00) : 0;
        i = 31 * i + (this.a01 != 0.0f ? Float.floatToIntBits(this.a01) : 0);
        i = 31 * i + (this.a02 != 0.0f ? Float.floatToIntBits(this.a02) : 0);
        i = 31 * i + (this.a10 != 0.0f ? Float.floatToIntBits(this.a10) : 0);
        i = 31 * i + (this.a11 != 0.0f ? Float.floatToIntBits(this.a11) : 0);
        i = 31 * i + (this.a12 != 0.0f ? Float.floatToIntBits(this.a12) : 0);
        i = 31 * i + (this.a20 != 0.0f ? Float.floatToIntBits(this.a20) : 0);
        i = 31 * i + (this.a21 != 0.0f ? Float.floatToIntBits(this.a21) : 0);
        i = 31 * i + (this.a22 != 0.0f ? Float.floatToIntBits(this.a22) : 0);
        return i;
    }

    private static int pack(int x, int y) {
        return y * 3 + x;
    }

    public void readColumnMajor(FloatBuffer buf) {
        this.a00 = buf.get(Matrix3f.pack(0, 0));
        this.a01 = buf.get(Matrix3f.pack(0, 1));
        this.a02 = buf.get(Matrix3f.pack(0, 2));
        this.a10 = buf.get(Matrix3f.pack(1, 0));
        this.a11 = buf.get(Matrix3f.pack(1, 1));
        this.a12 = buf.get(Matrix3f.pack(1, 2));
        this.a20 = buf.get(Matrix3f.pack(2, 0));
        this.a21 = buf.get(Matrix3f.pack(2, 1));
        this.a22 = buf.get(Matrix3f.pack(2, 2));
    }

    public void readRowMajor(FloatBuffer buf) {
        this.a00 = buf.get(Matrix3f.pack(0, 0));
        this.a01 = buf.get(Matrix3f.pack(1, 0));
        this.a02 = buf.get(Matrix3f.pack(2, 0));
        this.a10 = buf.get(Matrix3f.pack(0, 1));
        this.a11 = buf.get(Matrix3f.pack(1, 1));
        this.a12 = buf.get(Matrix3f.pack(2, 1));
        this.a20 = buf.get(Matrix3f.pack(0, 2));
        this.a21 = buf.get(Matrix3f.pack(1, 2));
        this.a22 = buf.get(Matrix3f.pack(2, 2));
    }

    public void read(FloatBuffer buf, boolean rowMajor) {
        if (rowMajor) {
            this.readRowMajor(buf);
        } else {
            this.readColumnMajor(buf);
        }
    }

    public void load(Matrix3f source) {
        this.a00 = source.a00;
        this.a01 = source.a01;
        this.a02 = source.a02;
        this.a10 = source.a10;
        this.a11 = source.a11;
        this.a12 = source.a12;
        this.a20 = source.a20;
        this.a21 = source.a21;
        this.a22 = source.a22;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix3f:\n");
        stringBuilder.append(this.a00);
        stringBuilder.append(" ");
        stringBuilder.append(this.a01);
        stringBuilder.append(" ");
        stringBuilder.append(this.a02);
        stringBuilder.append("\n");
        stringBuilder.append(this.a10);
        stringBuilder.append(" ");
        stringBuilder.append(this.a11);
        stringBuilder.append(" ");
        stringBuilder.append(this.a12);
        stringBuilder.append("\n");
        stringBuilder.append(this.a20);
        stringBuilder.append(" ");
        stringBuilder.append(this.a21);
        stringBuilder.append(" ");
        stringBuilder.append(this.a22);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void writeColumnMajor(FloatBuffer buf) {
        buf.put(Matrix3f.pack(0, 0), this.a00);
        buf.put(Matrix3f.pack(0, 1), this.a01);
        buf.put(Matrix3f.pack(0, 2), this.a02);
        buf.put(Matrix3f.pack(1, 0), this.a10);
        buf.put(Matrix3f.pack(1, 1), this.a11);
        buf.put(Matrix3f.pack(1, 2), this.a12);
        buf.put(Matrix3f.pack(2, 0), this.a20);
        buf.put(Matrix3f.pack(2, 1), this.a21);
        buf.put(Matrix3f.pack(2, 2), this.a22);
    }

    public void writeRowMajor(FloatBuffer buf) {
        buf.put(Matrix3f.pack(0, 0), this.a00);
        buf.put(Matrix3f.pack(1, 0), this.a01);
        buf.put(Matrix3f.pack(2, 0), this.a02);
        buf.put(Matrix3f.pack(0, 1), this.a10);
        buf.put(Matrix3f.pack(1, 1), this.a11);
        buf.put(Matrix3f.pack(2, 1), this.a12);
        buf.put(Matrix3f.pack(0, 2), this.a20);
        buf.put(Matrix3f.pack(1, 2), this.a21);
        buf.put(Matrix3f.pack(2, 2), this.a22);
    }

    public void write(FloatBuffer buf, boolean rowMajor) {
        if (rowMajor) {
            this.writeRowMajor(buf);
        } else {
            this.writeColumnMajor(buf);
        }
    }

    public void loadIdentity() {
        this.a00 = 1.0f;
        this.a01 = 0.0f;
        this.a02 = 0.0f;
        this.a10 = 0.0f;
        this.a11 = 1.0f;
        this.a12 = 0.0f;
        this.a20 = 0.0f;
        this.a21 = 0.0f;
        this.a22 = 1.0f;
    }

    public float determinantAndAdjugate() {
        float f = this.a11 * this.a22 - this.a12 * this.a21;
        float g = -(this.a10 * this.a22 - this.a12 * this.a20);
        float h = this.a10 * this.a21 - this.a11 * this.a20;
        float i = -(this.a01 * this.a22 - this.a02 * this.a21);
        float j = this.a00 * this.a22 - this.a02 * this.a20;
        float k = -(this.a00 * this.a21 - this.a01 * this.a20);
        float l = this.a01 * this.a12 - this.a02 * this.a11;
        float m = -(this.a00 * this.a12 - this.a02 * this.a10);
        float n = this.a00 * this.a11 - this.a01 * this.a10;
        float o = this.a00 * f + this.a01 * g + this.a02 * h;
        this.a00 = f;
        this.a10 = g;
        this.a20 = h;
        this.a01 = i;
        this.a11 = j;
        this.a21 = k;
        this.a02 = l;
        this.a12 = m;
        this.a22 = n;
        return o;
    }

    public float determinant() {
        float f = this.a11 * this.a22 - this.a12 * this.a21;
        float g = -(this.a10 * this.a22 - this.a12 * this.a20);
        float h = this.a10 * this.a21 - this.a11 * this.a20;
        return this.a00 * f + this.a01 * g + this.a02 * h;
    }

    public boolean invert() {
        float f = this.determinantAndAdjugate();
        if (Math.abs(f) > 1.0E-6f) {
            this.multiply(f);
            return true;
        }
        return false;
    }

    public void set(int x, int y, float value) {
        if (x == 0) {
            if (y == 0) {
                this.a00 = value;
            } else if (y == 1) {
                this.a01 = value;
            } else {
                this.a02 = value;
            }
        } else if (x == 1) {
            if (y == 0) {
                this.a10 = value;
            } else if (y == 1) {
                this.a11 = value;
            } else {
                this.a12 = value;
            }
        } else if (y == 0) {
            this.a20 = value;
        } else if (y == 1) {
            this.a21 = value;
        } else {
            this.a22 = value;
        }
    }

    public void multiply(Matrix3f other) {
        float f = this.a00 * other.a00 + this.a01 * other.a10 + this.a02 * other.a20;
        float g = this.a00 * other.a01 + this.a01 * other.a11 + this.a02 * other.a21;
        float h = this.a00 * other.a02 + this.a01 * other.a12 + this.a02 * other.a22;
        float i = this.a10 * other.a00 + this.a11 * other.a10 + this.a12 * other.a20;
        float j = this.a10 * other.a01 + this.a11 * other.a11 + this.a12 * other.a21;
        float k = this.a10 * other.a02 + this.a11 * other.a12 + this.a12 * other.a22;
        float l = this.a20 * other.a00 + this.a21 * other.a10 + this.a22 * other.a20;
        float m = this.a20 * other.a01 + this.a21 * other.a11 + this.a22 * other.a21;
        float n = this.a20 * other.a02 + this.a21 * other.a12 + this.a22 * other.a22;
        this.a00 = f;
        this.a01 = g;
        this.a02 = h;
        this.a10 = i;
        this.a11 = j;
        this.a12 = k;
        this.a20 = l;
        this.a21 = m;
        this.a22 = n;
    }

    public void multiply(Quaternion quaternion) {
        this.multiply(new Matrix3f(quaternion));
    }

    public void multiply(float scalar) {
        this.a00 *= scalar;
        this.a01 *= scalar;
        this.a02 *= scalar;
        this.a10 *= scalar;
        this.a11 *= scalar;
        this.a12 *= scalar;
        this.a20 *= scalar;
        this.a21 *= scalar;
        this.a22 *= scalar;
    }

    public void add(Matrix3f matrix) {
        this.a00 += matrix.a00;
        this.a01 += matrix.a01;
        this.a02 += matrix.a02;
        this.a10 += matrix.a10;
        this.a11 += matrix.a11;
        this.a12 += matrix.a12;
        this.a20 += matrix.a20;
        this.a21 += matrix.a21;
        this.a22 += matrix.a22;
    }

    public void subtract(Matrix3f matrix) {
        this.a00 -= matrix.a00;
        this.a01 -= matrix.a01;
        this.a02 -= matrix.a02;
        this.a10 -= matrix.a10;
        this.a11 -= matrix.a11;
        this.a12 -= matrix.a12;
        this.a20 -= matrix.a20;
        this.a21 -= matrix.a21;
        this.a22 -= matrix.a22;
    }

    public float trace() {
        return this.a00 + this.a11 + this.a22;
    }

    public Matrix3f copy() {
        return new Matrix3f(this);
    }
}

