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
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CatCollarFeatureRenderer
extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/cat/cat_collar.png");
    private final CatEntityModel<CatEntity> model = new CatEntityModel(0.01f);

    public CatCollarFeatureRenderer(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context) {
        super(context);
    }

    @Override
    public void render(CatEntity catEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!catEntity.isTamed() || catEntity.isInvisible()) {
            return;
        }
        this.bindTexture(SKIN);
        float[] fs = catEntity.getCollarColor().getColorComponents();
        GlStateManager.color3f(fs[0], fs[1], fs[2]);
        ((CatEntityModel)this.getContextModel()).copyStateTo(this.model);
        this.model.animateModel(catEntity, f, g, h);
        this.model.render(catEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

