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
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer
extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    private final ItemRenderer field_4847 = MinecraftClient.getInstance().getItemRenderer();

    public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> context) {
        super(context);
    }

    @Override
    public void render(DolphinEntity dolphinEntity, float f, float g, float h, float i, float j, float k, float l) {
        ItemStack itemStack2;
        boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? dolphinEntity.getOffHandStack() : dolphinEntity.getMainHandStack();
        ItemStack itemStack3 = itemStack2 = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return;
        }
        this.method_4180(dolphinEntity, itemStack2);
    }

    private void method_4180(LivingEntity livingEntity, ItemStack itemStack) {
        boolean bl;
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        Block block = Block.getBlockFromItem(item);
        GlStateManager.pushMatrix();
        boolean bl2 = bl = this.field_4847.hasDepthInGui(itemStack) && block.getRenderLayer() == RenderLayer.TRANSLUCENT;
        if (bl) {
            GlStateManager.depthMask(false);
        }
        float f = 1.0f;
        float g = -1.0f;
        float h = MathHelper.abs(livingEntity.pitch) / 60.0f;
        if (livingEntity.pitch < 0.0f) {
            GlStateManager.translatef(0.0f, 1.0f - h * 0.5f, -1.0f + h * 0.5f);
        } else {
            GlStateManager.translatef(0.0f, 1.0f + h * 0.8f, -1.0f + h * 0.2f);
        }
        this.field_4847.renderHeldItem(itemStack, livingEntity, ModelTransformation.Type.GROUND, false);
        if (bl) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

