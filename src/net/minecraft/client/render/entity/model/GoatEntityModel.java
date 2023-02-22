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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.GoatEntity;

@Environment(value=EnvType.CLIENT)
public class GoatEntityModel<T extends GoatEntity>
extends QuadrupedEntityModel<T> {
    public GoatEntityModel(ModelPart root) {
        super(root, true, 19.0f, 1.0f, 2.5f, 2.0f, 24);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(2, 61).cuboid("right ear", -6.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).uv(2, 61).mirrored().cuboid("left ear", 2.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).uv(23, 52).cuboid("goatee", -0.5f, -3.0f, -14.0f, 0.0f, 7.0f, 5.0f), ModelTransform.pivot(1.0f, 14.0f, 0.0f));
        modelPartData2.addChild("left_horn", ModelPartBuilder.create().uv(12, 55).cuboid(-0.01f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("right_horn", ModelPartBuilder.create().uv(12, 55).cuboid(-2.99f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(34, 46).cuboid(-3.0f, -4.0f, -8.0f, 5.0f, 7.0f, 10.0f), ModelTransform.of(0.0f, -8.0f, -8.0f, 0.9599f, 0.0f, 0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(1, 1).cuboid(-4.0f, -17.0f, -7.0f, 9.0f, 11.0f, 16.0f).uv(0, 28).cuboid(-5.0f, -18.0f, -8.0f, 11.0f, 14.0f, 11.0f), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(36, 29).cuboid(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot(1.0f, 14.0f, 4.0f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(49, 29).cuboid(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot(-3.0f, 14.0f, 4.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(49, 2).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), ModelTransform.pivot(1.0f, 14.0f, -6.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(35, 2).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), ModelTransform.pivot(-3.0f, 14.0f, -6.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T goatEntity, float f, float g, float h, float i, float j) {
        this.head.getChild((String)"left_horn").visible = ((GoatEntity)goatEntity).hasLeftHorn();
        this.head.getChild((String)"right_horn").visible = ((GoatEntity)goatEntity).hasRightHorn();
        super.setAngles(goatEntity, f, g, h, i, j);
        float k = ((GoatEntity)goatEntity).getHeadPitch();
        if (k != 0.0f) {
            this.head.pitch = k;
        }
    }
}

