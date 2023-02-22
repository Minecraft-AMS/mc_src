/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class DisplayListChunkRenderer
extends ChunkRenderer {
    private final int displayListsStartIndex = GlAllocationUtils.genLists(RenderLayer.values().length);

    public DisplayListChunkRenderer(World world, WorldRenderer worldRenderer) {
        super(world, worldRenderer);
    }

    public int method_3639(RenderLayer renderLayer, ChunkRenderData chunkRenderData) {
        if (!chunkRenderData.isEmpty(renderLayer)) {
            return this.displayListsStartIndex + renderLayer.ordinal();
        }
        return -1;
    }

    @Override
    public void delete() {
        super.delete();
        GlAllocationUtils.deleteLists(this.displayListsStartIndex, RenderLayer.values().length);
    }
}

