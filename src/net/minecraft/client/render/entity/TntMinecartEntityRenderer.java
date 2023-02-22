/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends MinecartEntityRenderer<TntMinecartEntity> {
    public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void renderBlock(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState) {
        int i = tntMinecartEntity.getFuseTicks();
        if (i > -1 && (float)i - f + 1.0f < 10.0f) {
            float g = 1.0f - ((float)i - f + 1.0f) / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            GlStateManager.scalef(h, h, h);
        }
        super.renderBlock(tntMinecartEntity, f, blockState);
        if (i > -1 && i / 5 % 2 == 0) {
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, (1.0f - ((float)i - f + 1.0f) / 100.0f) * 0.8f);
            GlStateManager.pushMatrix();
            blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0f);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
    }
}

