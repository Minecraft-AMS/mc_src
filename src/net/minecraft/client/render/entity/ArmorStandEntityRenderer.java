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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
    public static final Identifier SKIN = new Identifier("textures/entity/armorstand/wood.png");

    public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0f);
        this.addFeature(new ArmorBipedFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(0.5f), new ArmorStandArmorEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
    }

    @Override
    protected Identifier getTexture(ArmorStandEntity armorStandEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(ArmorStandEntity armorStandEntity, float f, float g, float h) {
        GlStateManager.rotatef(180.0f - g, 0.0f, 1.0f, 0.0f);
        float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.field_7112) + h;
        if (i < 5.0f) {
            GlStateManager.rotatef(MathHelper.sin(i / 1.5f * (float)Math.PI) * 3.0f, 0.0f, 1.0f, 0.0f);
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity armorStandEntity) {
        return armorStandEntity.isCustomNameVisible();
    }

    @Override
    public void render(ArmorStandEntity armorStandEntity, double d, double e, double f, float g, float h) {
        if (armorStandEntity.isMarker()) {
            this.disableOutlineRender = true;
        }
        super.render(armorStandEntity, d, e, f, g, h);
        if (armorStandEntity.isMarker()) {
            this.disableOutlineRender = false;
        }
    }

    @Override
    protected /* synthetic */ boolean hasLabel(LivingEntity livingEntity) {
        return this.hasLabel((ArmorStandEntity)livingEntity);
    }
}

