/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer texture(float var1, float var2);

    public VertexConsumer overlay(int var1, int var2);

    public VertexConsumer light(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void next();

    default public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertex(x, y, z);
        this.color(red, green, blue, alpha);
        this.texture(u, v);
        this.overlay(overlay);
        this.light(light);
        this.normal(normalX, normalY, normalZ);
        this.next();
    }

    public void fixedColor(int var1, int var2, int var3, int var4);

    public void unfixColor();

    default public VertexConsumer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    default public VertexConsumer color(int argb) {
        return this.color(ColorHelper.Argb.getRed(argb), ColorHelper.Argb.getGreen(argb), ColorHelper.Argb.getBlue(argb), ColorHelper.Argb.getAlpha(argb));
    }

    default public VertexConsumer light(int uv) {
        return this.light(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlay(int uv) {
        return this.overlay(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
        this.quad(matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData) {
        float[] fs = new float[]{brightnesses[0], brightnesses[1], brightnesses[2], brightnesses[3]};
        int[] is = new int[]{lights[0], lights[1], lights[2], lights[3]};
        int[] js = quad.getVertexData();
        Vec3i vec3i = quad.getFace().getVector();
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();
        Vector3f vector3f = matrixEntry.getNormalMatrix().transform(new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ()));
        int i = 8;
        int j = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int k = 0; k < j; ++k) {
                float q;
                float p;
                float o;
                float n;
                float m;
                intBuffer.clear();
                intBuffer.put(js, k * 8, 8);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                if (useQuadColorData) {
                    float l = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    m = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    n = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    o = l * fs[k] * red;
                    p = m * fs[k] * green;
                    q = n * fs[k] * blue;
                } else {
                    o = fs[k] * red;
                    p = fs[k] * green;
                    q = fs[k] * blue;
                }
                int r = is[k];
                m = byteBuffer.getFloat(16);
                n = byteBuffer.getFloat(20);
                Vector4f vector4f = matrix4f.transform(new Vector4f(f, g, h, 1.0f));
                this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), o, p, q, 1.0f, m, n, overlay, r, vector3f.x(), vector3f.y(), vector3f.z());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Vector4f vector4f = matrix.transform(new Vector4f(x, y, z, 1.0f));
        return this.vertex(vector4f.x(), vector4f.y(), vector4f.z());
    }

    default public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        Vector3f vector3f = matrix.transform(new Vector3f(x, y, z));
        return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
    }
}

