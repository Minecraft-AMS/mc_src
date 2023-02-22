/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(value=EnvType.CLIENT)
public class EnderDragonDeathFeatureRenderer
extends FeatureRenderer<EnderDragonEntity, DragonEntityModel> {
    public EnderDragonDeathFeatureRenderer(FeatureRendererContext<EnderDragonEntity, DragonEntityModel> context) {
        super(context);
    }

    @Override
    public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (enderDragonEntity.field_7031 <= 0) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        DiffuseLighting.disable();
        float m = ((float)enderDragonEntity.field_7031 + h) / 200.0f;
        float n = 0.0f;
        if (m > 0.8f) {
            n = (m - 0.8f) / 0.2f;
        }
        Random random = new Random(432L);
        GlStateManager.disableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -1.0f, -2.0f);
        int o = 0;
        while ((float)o < (m + m * m) / 2.0f * 60.0f) {
            GlStateManager.rotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random.nextFloat() * 360.0f + m * 90.0f, 0.0f, 0.0f, 1.0f);
            float p = random.nextFloat() * 20.0f + 5.0f + n * 10.0f;
            float q = random.nextFloat() * 2.0f + 1.0f + n * 2.0f;
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - n))).next();
            bufferBuilder.vertex(-0.866 * (double)q, p, -0.5f * q).color(255, 0, 255, 0).next();
            bufferBuilder.vertex(0.866 * (double)q, p, -0.5f * q).color(255, 0, 255, 0).next();
            bufferBuilder.vertex(0.0, p, 1.0f * q).color(255, 0, 255, 0).next();
            bufferBuilder.vertex(-0.866 * (double)q, p, -0.5f * q).color(255, 0, 255, 0).next();
            tessellator.draw();
            ++o;
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.enableAlphaTest();
        DiffuseLighting.enable();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

