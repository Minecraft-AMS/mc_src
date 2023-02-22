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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final ModelPart leftChest = new ModelPart(this, 26, 21);
    private final ModelPart rightChest;

    public DonkeyEntityModel(float f) {
        super(f);
        this.leftChest.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.rightChest = new ModelPart(this, 26, 21);
        this.rightChest.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.leftChest.yaw = -1.5707964f;
        this.rightChest.yaw = 1.5707964f;
        this.leftChest.setPivot(6.0f, -8.0f, 0.0f);
        this.rightChest.setPivot(-6.0f, -8.0f, 0.0f);
        this.body.addChild(this.leftChest);
        this.body.addChild(this.rightChest);
    }

    @Override
    protected void method_2789(ModelPart modelPart) {
        ModelPart modelPart2 = new ModelPart(this, 0, 12);
        modelPart2.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart2.setPivot(1.25f, -10.0f, 4.0f);
        ModelPart modelPart3 = new ModelPart(this, 0, 12);
        modelPart3.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart3.setPivot(-1.25f, -10.0f, 4.0f);
        modelPart2.pitch = 0.2617994f;
        modelPart2.roll = 0.2617994f;
        modelPart3.pitch = 0.2617994f;
        modelPart3.roll = -0.2617994f;
        modelPart.addChild(modelPart2);
        modelPart.addChild(modelPart3);
    }

    @Override
    public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        super.setAngles(abstractDonkeyEntity, f, g, h, i, j);
        if (((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest()) {
            this.leftChest.visible = true;
            this.rightChest.visible = true;
        } else {
            this.leftChest.visible = false;
            this.rightChest.visible = false;
        }
    }
}

