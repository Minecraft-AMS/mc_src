/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.FrustumIntersection
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class Frustum {
    public static final int field_34820 = 4;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Matrix4f field_40824 = new Matrix4f();
    private Vector4f field_34821;
    private double x;
    private double y;
    private double z;

    public Frustum(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        this.init(positionMatrix, projectionMatrix);
    }

    public Frustum(Frustum frustum) {
        this.frustumIntersection.set((Matrix4fc)frustum.field_40824);
        this.field_40824.set((Matrix4fc)frustum.field_40824);
        this.x = frustum.x;
        this.y = frustum.y;
        this.z = frustum.z;
        this.field_34821 = frustum.field_34821;
    }

    public Frustum method_38557(int i) {
        double d = Math.floor(this.x / (double)i) * (double)i;
        double e = Math.floor(this.y / (double)i) * (double)i;
        double f = Math.floor(this.z / (double)i) * (double)i;
        double g = Math.ceil(this.x / (double)i) * (double)i;
        double h = Math.ceil(this.y / (double)i) * (double)i;
        double j = Math.ceil(this.z / (double)i) * (double)i;
        while (this.frustumIntersection.intersectAab((float)(d - this.x), (float)(e - this.y), (float)(f - this.z), (float)(g - this.x), (float)(h - this.y), (float)(j - this.z)) != -2) {
            this.x -= (double)(this.field_34821.x() * 4.0f);
            this.y -= (double)(this.field_34821.y() * 4.0f);
            this.z -= (double)(this.field_34821.z() * 4.0f);
        }
        return this;
    }

    public void setPosition(double cameraX, double cameraY, double cameraZ) {
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
    }

    private void init(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        projectionMatrix.mul((Matrix4fc)positionMatrix, this.field_40824);
        this.frustumIntersection.set((Matrix4fc)this.field_40824);
        this.field_34821 = this.field_40824.transformTranspose(new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));
    }

    public boolean isVisible(Box box) {
        return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    private boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float f = (float)(minX - this.x);
        float g = (float)(minY - this.y);
        float h = (float)(minZ - this.z);
        float i = (float)(maxX - this.x);
        float j = (float)(maxY - this.y);
        float k = (float)(maxZ - this.z);
        return this.frustumIntersection.testAab(f, g, h, i, j, k);
    }
}

