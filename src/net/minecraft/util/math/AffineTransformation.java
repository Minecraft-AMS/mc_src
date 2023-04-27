/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.tuple.Triple
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.util.math;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MatrixUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class AffineTransformation {
    private final Matrix4f matrix;
    public static final Codec<AffineTransformation> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.VECTOR_3F.fieldOf("translation").forGetter(affineTransformation -> affineTransformation.translation), (App)Codecs.ROTATION.fieldOf("left_rotation").forGetter(affineTransformation -> affineTransformation.leftRotation), (App)Codecs.VECTOR_3F.fieldOf("scale").forGetter(affineTransformation -> affineTransformation.scale), (App)Codecs.ROTATION.fieldOf("right_rotation").forGetter(affineTransformation -> affineTransformation.rightRotation)).apply((Applicative)instance, AffineTransformation::new));
    public static final Codec<AffineTransformation> ANY_CODEC = Codec.either(CODEC, (Codec)Codecs.MATRIX4F.xmap(AffineTransformation::new, AffineTransformation::getMatrix)).xmap(either -> (AffineTransformation)either.map(affineTransformation -> affineTransformation, affineTransformation -> affineTransformation), Either::left);
    private boolean initialized;
    @Nullable
    private Vector3f translation;
    @Nullable
    private Quaternionf leftRotation;
    @Nullable
    private Vector3f scale;
    @Nullable
    private Quaternionf rightRotation;
    private static final AffineTransformation IDENTITY = Util.make(() -> {
        AffineTransformation affineTransformation = new AffineTransformation(new Matrix4f());
        affineTransformation.translation = new Vector3f();
        affineTransformation.leftRotation = new Quaternionf();
        affineTransformation.scale = new Vector3f(1.0f, 1.0f, 1.0f);
        affineTransformation.rightRotation = new Quaternionf();
        affineTransformation.initialized = true;
        return affineTransformation;
    });

    public AffineTransformation(@Nullable Matrix4f matrix) {
        this.matrix = matrix == null ? new Matrix4f() : matrix;
    }

    public AffineTransformation(@Nullable Vector3f translation, @Nullable Quaternionf leftRotation, @Nullable Vector3f scale, @Nullable Quaternionf rightRotation) {
        this.matrix = AffineTransformation.setup(translation, leftRotation, scale, rightRotation);
        this.translation = translation != null ? translation : new Vector3f();
        this.leftRotation = leftRotation != null ? leftRotation : new Quaternionf();
        this.scale = scale != null ? scale : new Vector3f(1.0f, 1.0f, 1.0f);
        this.rightRotation = rightRotation != null ? rightRotation : new Quaternionf();
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
            float f = 1.0f / this.matrix.m33();
            Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose(new Matrix3f((Matrix4fc)this.matrix).scale(f));
            this.translation = this.matrix.getTranslation(new Vector3f()).mul(f);
            this.leftRotation = new Quaternionf((Quaternionfc)triple.getLeft());
            this.scale = new Vector3f((Vector3fc)triple.getMiddle());
            this.rightRotation = new Quaternionf((Quaternionfc)triple.getRight());
            this.initialized = true;
        }
    }

    private static Matrix4f setup(@Nullable Vector3f translation, @Nullable Quaternionf leftRotation, @Nullable Vector3f scale, @Nullable Quaternionf rightRotation) {
        Matrix4f matrix4f = new Matrix4f();
        if (translation != null) {
            matrix4f.translation((Vector3fc)translation);
        }
        if (leftRotation != null) {
            matrix4f.rotate((Quaternionfc)leftRotation);
        }
        if (scale != null) {
            matrix4f.scale((Vector3fc)scale);
        }
        if (rightRotation != null) {
            matrix4f.rotate((Quaternionfc)rightRotation);
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

    public Quaternionf getLeftRotation() {
        this.init();
        return new Quaternionf((Quaternionfc)this.leftRotation);
    }

    public Vector3f getScale() {
        this.init();
        return new Vector3f((Vector3fc)this.scale);
    }

    public Quaternionf getRightRotation() {
        this.init();
        return new Quaternionf((Quaternionfc)this.rightRotation);
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

    public AffineTransformation interpolate(AffineTransformation target, float factor) {
        Vector3f vector3f = this.getTranslation();
        Quaternionf quaternionf = this.getLeftRotation();
        Vector3f vector3f2 = this.getScale();
        Quaternionf quaternionf2 = this.getRightRotation();
        vector3f.lerp((Vector3fc)target.getTranslation(), factor);
        quaternionf.slerp((Quaternionfc)target.getLeftRotation(), factor);
        vector3f2.lerp((Vector3fc)target.getScale(), factor);
        quaternionf2.slerp((Quaternionfc)target.getRightRotation(), factor);
        return new AffineTransformation(vector3f, quaternionf, vector3f2, quaternionf2);
    }
}

