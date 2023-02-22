/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer
extends BlockEntityRenderer<StructureBlockBlockEntity> {
    @Override
    public void render(StructureBlockBlockEntity structureBlockBlockEntity, double d, double e, double f, float g, int i) {
        double s;
        double r;
        double q;
        double p;
        double o;
        double n;
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        super.render(structureBlockBlockEntity, d, e, f, g, i);
        BlockPos blockPos = structureBlockBlockEntity.getOffset();
        BlockPos blockPos2 = structureBlockBlockEntity.getSize();
        if (blockPos2.getX() < 1 || blockPos2.getY() < 1 || blockPos2.getZ() < 1) {
            return;
        }
        if (structureBlockBlockEntity.getMode() != StructureBlockMode.SAVE && structureBlockBlockEntity.getMode() != StructureBlockMode.LOAD) {
            return;
        }
        double h = 0.01;
        double j = blockPos.getX();
        double k = blockPos.getZ();
        double l = e + (double)blockPos.getY() - 0.01;
        double m = l + (double)blockPos2.getY() + 0.02;
        switch (structureBlockBlockEntity.getMirror()) {
            case LEFT_RIGHT: {
                n = (double)blockPos2.getX() + 0.02;
                o = -((double)blockPos2.getZ() + 0.02);
                break;
            }
            case FRONT_BACK: {
                n = -((double)blockPos2.getX() + 0.02);
                o = (double)blockPos2.getZ() + 0.02;
                break;
            }
            default: {
                n = (double)blockPos2.getX() + 0.02;
                o = (double)blockPos2.getZ() + 0.02;
            }
        }
        switch (structureBlockBlockEntity.getRotation()) {
            case CLOCKWISE_90: {
                p = d + (o < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
                q = f + (n < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
                r = p - o;
                s = q + n;
                break;
            }
            case CLOCKWISE_180: {
                p = d + (n < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
                q = f + (o < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
                r = p - n;
                s = q - o;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                p = d + (o < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
                q = f + (n < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
                r = p + o;
                s = q - n;
                break;
            }
            default: {
                p = d + (n < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
                q = f + (o < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
                r = p + n;
                s = q + o;
            }
        }
        int t = 255;
        int u = 223;
        int v = 127;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.disableLightmap(true);
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
            this.method_3586(tessellator, bufferBuilder, p, l, q, r, m, s, 255, 223, 127);
        }
        if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
            this.method_3585(structureBlockBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, true);
            this.method_3585(structureBlockBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, false);
        }
        this.disableLightmap(false);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
    }

    private void method_3585(StructureBlockBlockEntity structureBlockBlockEntity, double d, double e, double f, BlockPos blockPos, Tessellator tessellator, BufferBuilder bufferBuilder, boolean bl) {
        GlStateManager.lineWidth(bl ? 3.0f : 1.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        World blockView = structureBlockBlockEntity.getWorld();
        BlockPos blockPos2 = structureBlockBlockEntity.getPos();
        BlockPos blockPos3 = blockPos2.add(blockPos);
        for (BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockBlockEntity.getSize()).add(-1, -1, -1))) {
            boolean bl3;
            BlockState blockState = blockView.getBlockState(blockPos4);
            boolean bl2 = blockState.isAir();
            boolean bl4 = bl3 = blockState.getBlock() == Blocks.STRUCTURE_VOID;
            if (!bl2 && !bl3) continue;
            float g = bl2 ? 0.05f : 0.0f;
            double h = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.45f) + d - (double)g;
            double i = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.45f) + e - (double)g;
            double j = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.45f) + f - (double)g;
            double k = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.55f) + d + (double)g;
            double l = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.55f) + e + (double)g;
            double m = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.55f) + f + (double)g;
            if (bl) {
                WorldRenderer.drawBox(bufferBuilder, h, i, j, k, l, m, 0.0f, 0.0f, 0.0f, 1.0f);
                continue;
            }
            if (bl2) {
                WorldRenderer.drawBox(bufferBuilder, h, i, j, k, l, m, 0.5f, 0.5f, 1.0f, 1.0f);
                continue;
            }
            WorldRenderer.drawBox(bufferBuilder, h, i, j, k, l, m, 1.0f, 0.25f, 0.25f, 1.0f);
        }
        tessellator.draw();
    }

    private void method_3586(Tessellator tessellator, BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, int j, int k, int l) {
        GlStateManager.lineWidth(2.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(d, e, f).color((float)k, (float)k, (float)k, 0.0f).next();
        bufferBuilder.vertex(d, e, f).color(k, k, k, j).next();
        bufferBuilder.vertex(g, e, f).color(k, l, l, j).next();
        bufferBuilder.vertex(g, e, i).color(k, k, k, j).next();
        bufferBuilder.vertex(d, e, i).color(k, k, k, j).next();
        bufferBuilder.vertex(d, e, f).color(l, l, k, j).next();
        bufferBuilder.vertex(d, h, f).color(l, k, l, j).next();
        bufferBuilder.vertex(g, h, f).color(k, k, k, j).next();
        bufferBuilder.vertex(g, h, i).color(k, k, k, j).next();
        bufferBuilder.vertex(d, h, i).color(k, k, k, j).next();
        bufferBuilder.vertex(d, h, f).color(k, k, k, j).next();
        bufferBuilder.vertex(d, h, i).color(k, k, k, j).next();
        bufferBuilder.vertex(d, e, i).color(k, k, k, j).next();
        bufferBuilder.vertex(g, e, i).color(k, k, k, j).next();
        bufferBuilder.vertex(g, h, i).color(k, k, k, j).next();
        bufferBuilder.vertex(g, h, f).color(k, k, k, j).next();
        bufferBuilder.vertex(g, e, f).color(k, k, k, j).next();
        bufferBuilder.vertex(g, e, f).color((float)k, (float)k, (float)k, 0.0f).next();
        tessellator.draw();
        GlStateManager.lineWidth(1.0f);
    }

    @Override
    public boolean method_3563(StructureBlockBlockEntity structureBlockBlockEntity) {
        return true;
    }
}

