/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorBipedFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends ArmorFeatureRenderer<T, M, A> {
    public ArmorBipedFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
        super(featureRendererContext, bipedEntityModel, bipedEntityModel2);
    }

    @Override
    protected void method_4170(A bipedEntityModel, EquipmentSlot equipmentSlot) {
        this.method_4190(bipedEntityModel);
        switch (equipmentSlot) {
            case HEAD: {
                ((BipedEntityModel)bipedEntityModel).head.visible = true;
                ((BipedEntityModel)bipedEntityModel).helmet.visible = true;
                break;
            }
            case CHEST: {
                ((BipedEntityModel)bipedEntityModel).torso.visible = true;
                ((BipedEntityModel)bipedEntityModel).rightArm.visible = true;
                ((BipedEntityModel)bipedEntityModel).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((BipedEntityModel)bipedEntityModel).torso.visible = true;
                ((BipedEntityModel)bipedEntityModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedEntityModel).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((BipedEntityModel)bipedEntityModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedEntityModel).leftLeg.visible = true;
            }
        }
    }

    @Override
    protected void method_4190(A bipedEntityModel) {
        ((BipedEntityModel)bipedEntityModel).setVisible(false);
    }
}

