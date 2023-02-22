/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class TransformingVertexConsumer
extends FixedColorVertexConsumer {
    private final VertexConsumer vertexConsumer;
    private final Matrix4f textureMatrix;
    private final Matrix3f normalMatrix;
    private float x;
    private float y;
    private float z;
    private int u1;
    private int v1;
    private int light;
    private float normalX;
    private float normalY;
    private float normalZ;

    public TransformingVertexConsumer(VertexConsumer vertexConsumer, MatrixStack.Entry entry) {
        this.vertexConsumer = vertexConsumer;
        this.textureMatrix = entry.getModel().copy();
        this.textureMatrix.invert();
        this.normalMatrix = entry.getNormal().copy();
        this.normalMatrix.invert();
        this.init();
    }

    private void init() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.u1 = 0;
        this.v1 = 10;
        this.light = 0xF000F0;
        this.normalX = 0.0f;
        this.normalY = 1.0f;
        this.normalZ = 0.0f;
    }

    @Override
    public void next() {
        Vector3f vector3f = new Vector3f(this.normalX, this.normalY, this.normalZ);
        vector3f.transform(this.normalMatrix);
        Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ());
        Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0f);
        vector4f.transform(this.textureMatrix);
        vector4f.rotate(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        vector4f.rotate(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        vector4f.rotate(direction.getRotationQuaternion());
        float f = -vector4f.getX();
        float g = -vector4f.getY();
        this.vertexConsumer.vertex(this.x, this.y, this.z).color(1.0f, 1.0f, 1.0f, 1.0f).texture(f, g).overlay(this.u1, this.v1).light(this.light).normal(this.normalX, this.normalY, this.normalZ).next();
        this.init();
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        this.u1 = u;
        this.v1 = v;
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        this.light = u | v << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.normalX = x;
        this.normalY = y;
        this.normalZ = z;
        return this;
    }
}

