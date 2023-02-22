/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    public void draw(BufferBuilder bufferBuilder) {
        if (bufferBuilder.getVertexCount() > 0) {
            int l;
            int j;
            VertexFormat vertexFormat = bufferBuilder.getVertexFormat();
            int i = vertexFormat.getVertexSize();
            ByteBuffer byteBuffer = bufferBuilder.getByteBuffer();
            List<VertexFormatElement> list = vertexFormat.getElements();
            block12: for (j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexFormatElement = list.get(j);
                VertexFormatElement.Type type = vertexFormatElement.getType();
                int k = vertexFormatElement.getFormat().getGlId();
                l = vertexFormatElement.getIndex();
                byteBuffer.position(vertexFormat.getElementOffset(j));
                switch (type) {
                    case POSITION: {
                        GlStateManager.vertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        GlStateManager.enableClientState(32884);
                        continue block12;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
                        GlStateManager.texCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        GlStateManager.enableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        continue block12;
                    }
                    case COLOR: {
                        GlStateManager.colorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        GlStateManager.enableClientState(32886);
                        continue block12;
                    }
                    case NORMAL: {
                        GlStateManager.normalPointer(k, i, byteBuffer);
                        GlStateManager.enableClientState(32885);
                    }
                }
            }
            GlStateManager.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
            int m = list.size();
            block13: for (j = 0; j < m; ++j) {
                VertexFormatElement vertexFormatElement2 = list.get(j);
                VertexFormatElement.Type type2 = vertexFormatElement2.getType();
                l = vertexFormatElement2.getIndex();
                switch (type2) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        continue block13;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        continue block13;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                        continue block13;
                    }
                    case NORMAL: {
                        GlStateManager.disableClientState(32885);
                    }
                }
            }
        }
        bufferBuilder.clear();
    }
}

