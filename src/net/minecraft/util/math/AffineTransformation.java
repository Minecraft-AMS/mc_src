/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Triple
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Matrix4x3f
 *  org.joml.Matrix4x3fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.util.math;

import java.util.Objects;
import net.minecraft.util.Util;
import net.minecraft.util.math.MatrixUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class AffineTransformation {
    private final Matrix4f matrix;
    private boolean initialized;
    @Nullable
    private Vector3f translation;
    @Nullable
    private Quaternionf rotation2;
    @Nullable
    private Vector3f scale;
    @Nullable
    private Quaternionf rotation1;
    private static final AffineTransformation IDENTITY = Util.make(() -> {
        AffineTransformation affineTransformation = new AffineTransformation(new Matrix4f());
        affineTransformation.getRotation2();
        return affineTransformation;
    });

    public AffineTransformation(@Nullable Matrix4f matrix) {
        this.matrix = matrix == null ? AffineTransformation.IDENTITY.matrix : matrix;
    }

    public AffineTransformation(@Nullable Vector3f translation, @Nullable Quaternionf rotation2, @Nullable Vector3f scale, @Nullable Quaternionf rotation1) {
        this.matrix = AffineTransformation.setup(translation, rotation2, scale, rotation1);
        this.translation = translation != null ? translation : new Vector3f();
        this.rotation2 = rotation2 != null ? rotation2 : new Quaternionf();
        this.scale = scale != null ? scale : new Vector3f(1.0f, 1.0f, 1.0f);
        this.rotation1 = rotation1 != null ? rotation1 : new Quaternionf();
        this.initialized = true;
    }

    public static AffineTransformation identity() {
        return IDENTITY;
    }

    public AffineTransformation multiply(AffineTransformation other) {
        Matrix4f matrix4f = this.getMatrix();
        matrix4f.mul((Matrix4fc)other.getMatrix());
        return new AffineTransformation(matrix4f);
    }

    @Nullable
    public AffineTransformation invert() {
        if (this == IDENTITY) {
            return this;
        }
        Matrix4f matrix4f = this.getMatrix().invert();
        if (matrix4f.isFinite()) {
            return new AffineTransformation(matrix4f);
        }
        return null;
    }

    private void init() {
        if (!this.initialized) {
            Matrix4x3f matrix4x3f = MatrixUtil.affineTransform(this.matrix);
            Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose(new Matrix3f().set((Matrix4x3fc)matrix4x3f));
            this.translation = matrix4x3f.getTranslation(new Vector3f());
            this.rotation2 = new Quaternionf((Quaternionfc)triple.getLeft());
            this.scale = new Vector3f((Vector3fc)triple.getMiddle());
            this.rotation1 = new Quaternionf((Quaternionfc)triple.getRight());
            this.initialized = true;
        }
    }

    private static Matrix4f setup(@Nullable Vector3f vector3f, @Nullable Quaternionf quaternionf, @Nullable Vector3f vector3f2, @Nullable Quaternionf quaternionf2) {
        Matrix4f matrix4f = new Matrix4f();
        if (vector3f != null) {
            matrix4f.translation((Vector3fc)vector3f);
        }
        if (quaternionf != null) {
            matrix4f.rotate((Quaternionfc)quaternionf);
        }
        if (vector3f2 != null) {
            matrix4f.scale((Vector3fc)vector3f2);
        }
        if (quaternionf2 != null) {
            matrix4f.rotate((Quaternionfc)quaternionf2);
        }
        return matrix4f;
    }

    public Matrix4f getMatrix() {
        return new Matrix4f((Matrix4fc)this.matrix);
    }

    public Vector3f getTranslation() {
        this.init();
        return new Vector3f((Vector3fc)this.translation);
    }

    public Quaternionf getRotation2() {
        this.init();
        return new Quaternionf((Quaternionfc)this.rotation2);
    }

    public Vector3f getScale() {
        this.init();
        return new Vector3f((Vector3fc)this.scale);
    }

    public Quaternionf getRotation1() {
        this.init();
        return new Quaternionf((Quaternionfc)this.rotation1);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AffineTransformation affineTransformation = (AffineTransformation)o;
        return Objects.equals(this.matrix, affineTransformation.matrix);
    }

    public int hashCode() {
        return Objects.hash(this.matrix);
    }

    public AffineTransformation method_35864(AffineTransformation affineTransformation, float f) {
        Vector3f vector3f = this.getTranslation();
        Quaternionf quaternionf = this.getRotation2();
        Vector3f vector3f2 = this.getScale();
        Quaternionf quaternionf2 = this.getRotation1();
        vector3f.lerp((Vector3fc)affineTransformation.getTranslation(), f);
        quaternionf.slerp((Quaternionfc)affineTransformation.getRotation2(), f);
        vector3f2.lerp((Vector3fc)affineTransformation.getScale(), f);
        quaternionf2.slerp((Quaternionfc)affineTransformation.getRotation1(), f);
        return new AffineTransformation(vector3f, quaternionf, vector3f2, quaternionf2);
    }
}

