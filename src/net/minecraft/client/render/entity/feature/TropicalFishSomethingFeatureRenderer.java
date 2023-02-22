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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA(0.008f);
    private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB(0.008f);

    public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> context) {
        super(context);
    }

    @Override
    public void render(TropicalFishEntity tropicalFishEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (tropicalFishEntity.isInvisible()) {
            return;
        }
        EntityModel entityModel = tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB;
        this.bindTexture(tropicalFishEntity.getVarietyId());
        float[] fs = tropicalFishEntity.getPatternColorComponents();
        GlStateManager.color3f(fs[0], fs[1], fs[2]);
        ((EntityModel)this.getContextModel()).copyStateTo(entityModel);
        entityModel.animateModel((TropicalFishEntity)tropicalFishEntity, f, g, h);
        entityModel.render(tropicalFishEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

