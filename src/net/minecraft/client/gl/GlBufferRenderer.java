/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(value=EnvType.CLIENT)
public class GlBufferRenderer
extends BufferRenderer {
    private VertexBuffer glBuffer;

    @Override
    public void draw(BufferBuilder bufferBuilder) {
        bufferBuilder.clear();
        this.glBuffer.set(bufferBuilder.getByteBuffer());
    }

    public void setGlBuffer(VertexBuffer glBuffer) {
        this.glBuffer = glBuffer;
    }
}

