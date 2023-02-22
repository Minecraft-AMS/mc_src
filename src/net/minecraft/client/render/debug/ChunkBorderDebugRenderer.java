/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;

@Environment(value=EnvType.CLIENT)
public class ChunkBorderDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public ChunkBorderDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        int k;
        Camera camera = this.client.gameRenderer.getCamera();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        double g = 0.0 - e;
        double h = 256.0 - e;
        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
        double i = (double)(camera.getFocusedEntity().chunkX << 4) - d;
        double j = (double)(camera.getFocusedEntity().chunkZ << 4) - f;
        GlStateManager.lineWidth(1.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (k = -16; k <= 32; k += 16) {
            for (int m = -16; m <= 32; m += 16) {
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.0f).next();
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.0f).next();
            }
        }
        for (k = 2; k < 16; k += 2) {
            bufferBuilder.vertex(i + (double)k, g, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (k = 2; k < 16; k += 2) {
            bufferBuilder.vertex(i, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (k = 0; k <= 256; k += 2) {
            double n = (double)k - e;
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        tessellator.draw();
        GlStateManager.lineWidth(2.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (k = 0; k <= 16; k += 16) {
            for (int m = 0; m <= 16; m += 16) {
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (k = 0; k <= 256; k += 16) {
            double n = (double)k - e;
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
        tessellator.draw();
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
    }
}

