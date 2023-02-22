/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererList;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;

@Environment(value=EnvType.CLIENT)
public class DisplayListChunkRendererList
extends ChunkRendererList {
    @Override
    public void render(RenderLayer layer) {
        if (!this.isCameraPositionSet) {
            return;
        }
        for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
            DisplayListChunkRenderer displayListChunkRenderer = (DisplayListChunkRenderer)chunkRenderer;
            GlStateManager.pushMatrix();
            this.translateToOrigin(chunkRenderer);
            GlStateManager.callList(displayListChunkRenderer.method_3639(layer, displayListChunkRenderer.getData()));
            GlStateManager.popMatrix();
        }
        GlStateManager.clearCurrentColor();
        this.chunkRenderers.clear();
    }
}

