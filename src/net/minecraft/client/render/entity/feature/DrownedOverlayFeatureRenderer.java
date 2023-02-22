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
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends ZombieEntity>
extends FeatureRenderer<T, DrownedEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedEntityModel<T> model = new DrownedEntityModel(0.25f, 0.0f, 64, 64);

    public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> context) {
        super(context);
    }

    @Override
    public void render(T zombieEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (((Entity)zombieEntity).isInvisible()) {
            return;
        }
        ((DrownedEntityModel)this.getContextModel()).setAttributes(this.model);
        this.model.animateModel(zombieEntity, f, g, h);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SKIN);
        this.model.render(zombieEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

