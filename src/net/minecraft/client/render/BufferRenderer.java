/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    @Nullable
    private static VertexBuffer currentVertexBuffer;

    public static void reset() {
        if (currentVertexBuffer != null) {
            BufferRenderer.resetCurrentVertexBuffer();
            VertexBuffer.unbind();
        }
    }

    public static void resetCurrentVertexBuffer() {
        currentVertexBuffer = null;
    }

    public static void drawWithGlobalProgram(BufferBuilder.BuiltBuffer buffer) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> BufferRenderer.drawWithGlobalProgramInternal(buffer));
        } else {
            BufferRenderer.drawWithGlobalProgramInternal(buffer);
        }
    }

    private static void drawWithGlobalProgramInternal(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.upload(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    public static void draw(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.upload(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.draw();
        }
    }

    @Nullable
    private static VertexBuffer upload(BufferBuilder.BuiltBuffer buffer) {
        RenderSystem.assertOnRenderThread();
        if (buffer.isEmpty()) {
            buffer.release();
            return null;
        }
        VertexBuffer vertexBuffer = BufferRenderer.bind(buffer.getParameters().format());
        vertexBuffer.upload(buffer);
        return vertexBuffer;
    }

    private static VertexBuffer bind(VertexFormat vertexFormat) {
        VertexBuffer vertexBuffer = vertexFormat.getBuffer();
        BufferRenderer.bind(vertexBuffer);
        return vertexBuffer;
    }

    private static void bind(VertexBuffer vertexBuffer) {
        if (vertexBuffer != currentVertexBuffer) {
            vertexBuffer.bind();
            currentVertexBuffer = vertexBuffer;
        }
    }
}

