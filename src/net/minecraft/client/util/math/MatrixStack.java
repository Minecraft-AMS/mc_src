/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class MatrixStack {
    private final Deque<Entry> stack = Util.make(Queues.newArrayDeque(), arrayDeque -> {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.loadIdentity();
        arrayDeque.add(new Entry(matrix4f, matrix3f));
    });

    public void translate(double x, double y, double z) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(Matrix4f.translate((float)x, (float)y, (float)z));
    }

    public void scale(float x, float y, float z) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(Matrix4f.scale(x, y, z));
        if (x == y && y == z) {
            if (x > 0.0f) {
                return;
            }
            entry.normalMatrix.multiply(-1.0f);
        }
        float f = 1.0f / x;
        float g = 1.0f / y;
        float h = 1.0f / z;
        float i = MathHelper.fastInverseCbrt(f * g * h);
        entry.normalMatrix.multiply(Matrix3f.scale(i * f, i * g, i * h));
    }

    public void multiply(Quaternion quaternion) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(quaternion);
        entry.normalMatrix.multiply(quaternion);
    }

    public void push() {
        Entry entry = this.stack.getLast();
        this.stack.addLast(new Entry(entry.modelMatrix.copy(), entry.normalMatrix.copy()));
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

    @Environment(value=EnvType.CLIENT)
    public static final class Entry {
        private final Matrix4f modelMatrix;
        private final Matrix3f normalMatrix;

        private Entry(Matrix4f matrix4f, Matrix3f matrix3f) {
            this.modelMatrix = matrix4f;
            this.normalMatrix = matrix3f;
        }

        public Matrix4f getModel() {
            return this.modelMatrix;
        }

        public Matrix3f getNormal() {
            return this.normalMatrix;
        }
    }
}

