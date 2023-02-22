/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> context) {
        super(context);
    }

    @Override
    public void render(PandaEntity pandaEntity, float f, float g, float h, float i, float j, float k, float l) {
        ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!pandaEntity.isScared() || itemStack.isEmpty() || pandaEntity.method_6524()) {
            return;
        }
        float m = -0.6f;
        float n = 1.4f;
        if (pandaEntity.isEating()) {
            m -= 0.2f * MathHelper.sin(i * 0.6f) + 0.2f;
            n -= 0.09f * MathHelper.sin(i * 0.6f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, n, m);
        MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack, pandaEntity, ModelTransformation.Type.GROUND, false);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

