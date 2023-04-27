/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class MatrixStack {
    private final Deque<Entry> stack = Util.make(Queues.newArrayDeque(), stack -> {
        Matrix4f matrix4f = new Matrix4f();
        Matrix3f matrix3f = new Matrix3f();
        stack.add(new Entry(matrix4f, matrix3f));
    });

    public void translate(double x, double y, double z) {
        this.translate((float)x, (float)y, (float)z);
    }

    public void translate(float x, float y, float z) {
        Entry entry = this.stack.getLast();
        entry.positionMatrix.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        Entry entry = this.stack.getLast();
        entry.positionMatrix.scale(x, y, z);
        if (x == y && y == z) {
            if (x > 0.0f) {
                return;
            }
            entry.normalMatrix.scale(-1.0f);
        }
        float f = 1.0f / x;
        float g = 1.0f / y;
        float h = 1.0f / z;
        float i = MathHelper.fastInverseCbrt(f * g * h);
        entry.normalMatrix.scale(i * f, i * g, i * h);
    }

    public void multiply(Quaternionf quaternion) {
        Entry entry = this.stack.getLast();
        entry.positionMatrix.rotate((Quaternionfc)quaternion);
        entry.normalMatrix.rotate((Quaternionfc)quaternion);
    }

    public void multiply(Quaternionf quaternion, float originX, float originY, float originZ) {
        Entry entry = this.stack.getLast();
        entry.positionMatrix.rotateAround((Quaternionfc)quaternion, originX, originY, originZ);
        entry.normalMatrix.rotate((Quaternionfc)quaternion);
    }

    public void push() {
        Entry entry = this.stack.getLast();
        this.stack.addLast(new Entry(new Matrix4f((Matrix4fc)entry.positionMatrix), new Matrix3f((Matrix3fc)entry.normalMatrix)));
    }

    public void pop() {
        this.stack.removeLast();
    }

    public Entry peek() {
        return this.stack.getLast();
    }

    public boolean isEmpty() {
        return this.stack.size() == 1;
    }

    public void loadIdentity() {
        Entry entry = this.stack.getLast();
        entry.positionMatrix.identity();
        entry.normalMatrix.identity();
    }

    public void multiplyPositionMatrix(Matrix4f matrix) {
        this.stack.getLast().positionMatrix.mul((Matrix4fc)matrix);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry {
        final Matrix4f positionMatrix;
        final Matrix3f normalMatrix;

        Entry(Matrix4f positionMatrix, Matrix3f normalMatrix) {
            this.positionMatrix = positionMatrix;
            this.normalMatrix = normalMatrix;
        }

        public Matrix4f getPositionMatrix() {
            return this.positionMatrix;
        }

        public Matrix3f getNormalMatrix() {
            return this.normalMatrix;
        }
    }
}

