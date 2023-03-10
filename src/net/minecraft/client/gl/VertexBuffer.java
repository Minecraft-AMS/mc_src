/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class VertexBuffer
implements AutoCloseable {
    private int vertexBufferId;
    private int indexBufferId;
    private VertexFormat.IntType elementFormat;
    private int vertexArrayId;
    private int vertexCount;
    private VertexFormat.DrawMode drawMode;
    private boolean hasNoIndexBuffer;
    private VertexFormat vertexFormat;

    public VertexBuffer() {
        RenderSystem.glGenBuffers(id -> {
            this.vertexBufferId = id;
        });
        RenderSystem.glGenVertexArrays(id -> {
            this.vertexArrayId = id;
        });
        RenderSystem.glGenBuffers(id -> {
            this.indexBufferId = id;
        });
    }

    public void bind() {
        RenderSystem.glBindBuffer(34962, () -> this.vertexBufferId);
        if (this.hasNoIndexBuffer) {
            RenderSystem.glBindBuffer(34963, () -> {
                RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(this.drawMode, this.vertexCount);
                this.elementFormat = indexBuffer.getElementFormat();
                return indexBuffer.getId();
            });
        } else {
            RenderSystem.glBindBuffer(34963, () -> this.indexBufferId);
        }
    }

    public void upload(BufferBuilder buffer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.uploadInternal(buffer));
        } else {
            this.uploadInternal(buffer);
        }
    }

    public CompletableFuture<Void> submitUpload(BufferBuilder buffer) {
        if (!RenderSystem.isOnRenderThread()) {
            return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), action -> RenderSystem.recordRenderCall(action::run));
        }
        this.uploadInternal(buffer);
        return CompletableFuture.completedFuture(null);
    }

    private void uploadInternal(BufferBuilder buffer) {
        Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = buffer.popData();
        if (this.vertexBufferId == 0) {
            return;
        }
        BufferRenderer.unbindAll();
        BufferBuilder.DrawArrayParameters drawArrayParameters = (BufferBuilder.DrawArrayParameters)pair.getFirst();
        ByteBuffer byteBuffer = (ByteBuffer)pair.getSecond();
        int i = drawArrayParameters.getIndexBufferStart();
        this.vertexCount = drawArrayParameters.getVertexCount();
        this.elementFormat = drawArrayParameters.getElementFormat();
        this.vertexFormat = drawArrayParameters.getVertexFormat();
        this.drawMode = drawArrayParameters.getMode();
        this.hasNoIndexBuffer = drawArrayParameters.hasNoIndexBuffer();
        this.bindVertexArray();
        this.bind();
        if (!drawArrayParameters.hasNoVertexBuffer()) {
            byteBuffer.limit(i);
            RenderSystem.glBufferData(34962, byteBuffer, 35044);
            byteBuffer.position(i);
        }
        if (!this.hasNoIndexBuffer) {
            byteBuffer.limit(drawArrayParameters.getIndexBufferEnd());
            RenderSystem.glBufferData(34963, byteBuffer, 35044);
            byteBuffer.position(0);
        } else {
            byteBuffer.limit(drawArrayParameters.getIndexBufferEnd());
            byteBuffer.position(0);
        }
        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
    }

    private void bindVertexArray() {
        RenderSystem.glBindVertexArray(() -> this.vertexArrayId);
    }

    public static void unbindVertexArray() {
        RenderSystem.glBindVertexArray(() -> 0);
    }

    public void drawElements() {
        if (this.vertexCount == 0) {
            return;
        }
        RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.elementFormat.type);
    }

    public void setShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.innerSetShader(viewMatrix.copy(), projectionMatrix.copy(), shader));
        } else {
            this.innerSetShader(viewMatrix, projectionMatrix, shader);
        }
    }

    public void innerSetShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        if (this.vertexCount == 0) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        BufferRenderer.unbindAll();
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            shader.addSampler("Sampler" + i, j);
        }
        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(viewMatrix);
        }
        if (shader.projectionMat != null) {
            shader.projectionMat.set(projectionMatrix);
        }
        if (shader.viewRotationMat != null) {
            shader.viewRotationMat.method_39978(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shader.colorModulator != null) {
            shader.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (shader.fogStart != null) {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }
        if (shader.fogEnd != null) {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.fogColor != null) {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }
        if (shader.fogShape != null) {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        if (shader.textureMat != null) {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (shader.gameTime != null) {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }
        if (shader.screenSize != null) {
            Window window = MinecraftClient.getInstance().getWindow();
            shader.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
        }
        if (shader.lineWidth != null && (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            shader.lineWidth.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(shader);
        this.bindVertexArray();
        this.bind();
        this.getVertexFormat().startDrawing();
        shader.bind();
        RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.elementFormat.type);
        shader.unbind();
        this.getVertexFormat().endDrawing();
        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
    }

    public void drawVertices() {
        if (this.vertexCount == 0) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        this.bindVertexArray();
        this.bind();
        this.vertexFormat.startDrawing();
        RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.elementFormat.type);
    }

    public static void unbind() {
        RenderSystem.glBindBuffer(34962, () -> 0);
        RenderSystem.glBindBuffer(34963, () -> 0);
    }

    @Override
    public void close() {
        if (this.indexBufferId >= 0) {
            RenderSystem.glDeleteBuffers(this.indexBufferId);
            this.indexBufferId = -1;
        }
        if (this.vertexBufferId > 0) {
            RenderSystem.glDeleteBuffers(this.vertexBufferId);
            this.vertexBufferId = 0;
        }
        if (this.vertexArrayId > 0) {
            RenderSystem.glDeleteVertexArrays(this.vertexArrayId);
            this.vertexArrayId = 0;
        }
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }
}

