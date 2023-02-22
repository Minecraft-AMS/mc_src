/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.tuple.Triple
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class AffineTransformation {
    private final Matrix4f matrix;
    private boolean initialized;
    @Nullable
    private Vec3f translation;
    @Nullable
    private Quaternion rotation2;
    @Nullable
    private Vec3f scale;
    @Nullable
    private Quaternion rotation1;
    private static final AffineTransformation IDENTITY = Util.make(() -> {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        AffineTransformation affineTransformation = new AffineTransformation(matrix4f);
        affineTransformation.getRotation2();
        return affineTransformation;
    });

    public AffineTransformation(@Nullable Matrix4f matrix) {
        this.matrix = matrix == null ? AffineTransformation.IDENTITY.matrix : matrix;
    }

    public AffineTransformation(@Nullable Vec3f translation, @Nullable Quaternion rotation2, @Nullable Vec3f scale, @Nullable Quaternion rotation1) {
        this.matrix = AffineTransformation.setup(translation, rotation2, scale, rotation1);
        this.translation = translation != null ? translation : new Vec3f();
        this.rotation2 = rotation2 != null ? rotation2 : Quaternion.IDENTITY.copy();
        this.scale = scale != null ? scale : new Vec3f(1.0f, 1.0f, 1.0f);
        this.rotation1 = rotation1 != null ? rotation1 : Quaternion.IDENTITY.copy();
        this.initialized = true;
    }

    public static AffineTransformation identity() {
        return IDENTITY;
    }

    public AffineTransformation multiply(AffineTransformation other) {
        Matrix4f matrix4f = this.getMatrix();
        matrix4f.multiply(other.getMatrix());
        return new AffineTransformation(matrix4f);
    }

    @Nullable
    public AffineTransformation invert() {
        if (this == IDENTITY) {
            return this;
        }
        Matrix4f matrix4f = this.getMatrix();
        if (matrix4f.invert()) {
            return new AffineTransformation(matrix4f);
        }
        return null;
    }

    private void init() {
        if (!this.initialized) {
            Pair<Matrix3f, Vec3f> pair = AffineTransformation.getLinearTransformationAndTranslationFromAffine(this.matrix);
            Triple<Quaternion, Vec3f, Quaternion> triple = ((Matrix3f)pair.getFirst()).decomposeLinearTransformation();
            this.translation = (Vec3f)pair.getSecond();
            this.rotation2 = (Quaternion)triple.getLeft();
            this.scale = (Vec3f)triple.getMiddle();
            this.rotation1 = (Quaternion)triple.getRight();
            this.initialized = true;
        }
    }

    private static Matrix4f setup(@Nullable Vec3f translation, @Nullable Quaternion rotation2, @Nullable Vec3f scale, @Nullable Quaternion rotation1) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        if (rotation2 != null) {
            matrix4f.multiply(new Matrix4f(rotation2));
        }
        if (scale != null) {
            matrix4f.multiply(Matrix4f.scale(scale.getX(), scale.getY(), scale.getZ()));
        }
        if (rotation1 != null) {
            matrix4f.multiply(new Matrix4f(rotation1));
        }
        if (translation != null) {
            matrix4f.a03 = translation.getX();
            matrix4f.a13 = translation.getY();
            matrix4f.a23 = translation.getZ();
        }
        return matrix4f;
    }

    public static Pair<Matrix3f, Vec3f> getLinearTransformationAndTranslationFromAffine(Matrix4f affineTransform) {
        affineTransform.multiply(1.0f / affineTransform.a33);
        Vec3f vec3f = new Vec3f(affineTransform.a03, affineTransform.a13, affineTransform.a23);
        Matrix3f matrix3f = new Matrix3f(affineTransform);
        return Pair.of((Object)matrix3f, (Object)vec3f);
    }

    public Matrix4f getMatrix() {
        return this.matrix.copy();
    }

    public Quaternion getRotation2() {
        this.init();
        return this.rotation2.copy();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        AffineTransformation affineTransformation = (AffineTransformation)object;
        return Objects.equals(this.matrix, affineTransformation.matrix);
    }

    public int hashCode() {
        return Objects.hash(this.matrix);
    }
}

