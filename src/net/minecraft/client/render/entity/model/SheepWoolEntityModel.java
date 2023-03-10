/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(value=EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity>
extends QuadrupedEntityModel<T> {
    private float headAngle;

    public SheepWoolEntityModel(ModelPart root) {
        super(root, false, 8.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, new Dilation(0.6f)), ModelTransform.pivot(0.0f, 6.0f, -8.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(28, 8).cuboid(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f, new Dilation(1.75f)), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, new Dilation(0.5f));
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-3.0f, 12.0f, 7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(3.0f, 12.0f, 7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-3.0f, 12.0f, -5.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(3.0f, 12.0f, -5.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void animateModel(T sheepEntity, float f, float g, float h) {
        super.animateModel(sheepEntity, f, g, h);
        this.head.pivotY = 6.0f + ((SheepEntity)sheepEntity).getNeckAngle(h) * 9.0f;
        this.headAngle = ((SheepEntity)sheepEntity).getHeadAngle(h);
    }

    @Override
    public void setAngles(T sheepEntity, float f, float g, float h, float i, float j) {
        super.setAngles(sheepEntity, f, g, h, i, j);
        this.head.pitch = this.headAngle;
    }
}

