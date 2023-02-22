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
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
extends BlockEntityRenderer<MobSpawnerBlockEntity> {
    @Override
    public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, double d, double e, double f, float g, int i) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d + 0.5f, (float)e, (float)f + 0.5f);
        MobSpawnerBlockEntityRenderer.method_3589(mobSpawnerBlockEntity.getLogic(), d, e, f, g);
        GlStateManager.popMatrix();
    }

    public static void method_3589(MobSpawnerLogic mobSpawnerLogic, double d, double e, double f, float g) {
        Entity entity = mobSpawnerLogic.getRenderedEntity();
        if (entity != null) {
            float h = 0.53125f;
            float i = Math.max(entity.getWidth(), entity.getHeight());
            if ((double)i > 1.0) {
                h /= i;
            }
            GlStateManager.translatef(0.0f, 0.4f, 0.0f);
            GlStateManager.rotatef((float)MathHelper.lerp((double)g, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.2f, 0.0f);
            GlStateManager.rotatef(-30.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.scalef(h, h, h);
            entity.refreshPositionAndAngles(d, e, f, 0.0f, 0.0f);
            MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0f, g, false);
        }
    }
}

