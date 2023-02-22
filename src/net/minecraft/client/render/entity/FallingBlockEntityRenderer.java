/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity> {
    public FallingBlockEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    @Override
    public void render(FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h) {
        BlockState blockState = fallingBlockEntity.getBlockState();
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        World world = fallingBlockEntity.getWorldClient();
        if (blockState == world.getBlockState(new BlockPos(fallingBlockEntity)) || blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fallingBlockEntity));
        }
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
        BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().y2, fallingBlockEntity.z);
        GlStateManager.translatef((float)(d - (double)blockPos.getX() - 0.5), (float)(e - (double)blockPos.getY()), (float)(f - (double)blockPos.getZ() - 0.5));
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        blockRenderManager.getModelRenderer().tesselate(world, blockRenderManager.getModel(blockState), blockState, blockPos, bufferBuilder, false, new Random(), blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos()));
        tessellator.draw();
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.render(fallingBlockEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
