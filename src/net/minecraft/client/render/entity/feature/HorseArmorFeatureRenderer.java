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
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class HorseArmorFeatureRenderer
extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
    private final HorseEntityModel<HorseEntity> model = new HorseEntityModel(0.1f);

    public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(HorseEntity horseEntity, float f, float g, float h, float i, float j, float k, float l) {
        ItemStack itemStack = horseEntity.getArmorType();
        if (itemStack.getItem() instanceof HorseArmorItem) {
            HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
            ((HorseEntityModel)this.getContextModel()).copyStateTo(this.model);
            this.model.animateModel(horseEntity, f, g, h);
            this.bindTexture(horseArmorItem.getEntityTexture());
            if (horseArmorItem instanceof DyeableHorseArmorItem) {
                int m = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
                float n = (float)(m >> 16 & 0xFF) / 255.0f;
                float o = (float)(m >> 8 & 0xFF) / 255.0f;
                float p = (float)(m & 0xFF) / 255.0f;
                GlStateManager.color4f(n, o, p, 1.0f);
                this.model.render(horseEntity, f, g, i, j, k, l);
                return;
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.model.render(horseEntity, f, g, i, j, k, l);
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

