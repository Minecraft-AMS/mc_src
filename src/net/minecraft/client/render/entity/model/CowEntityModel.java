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
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class CowEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public CowEntityModel() {
        super(12, 0.0f);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -4.0f, -6.0f, 8, 8, 6, 0.0f);
        this.head.setPivot(0.0f, 4.0f, -8.0f);
        this.head.setTextureOffset(22, 0).addCuboid(-5.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        this.head.setTextureOffset(22, 0).addCuboid(4.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        this.torso = new ModelPart(this, 18, 4);
        this.torso.addCuboid(-6.0f, -10.0f, -7.0f, 12, 18, 10, 0.0f);
        this.torso.setPivot(0.0f, 5.0f, 2.0f);
        this.torso.setTextureOffset(52, 0).addCuboid(-2.0f, 2.0f, -8.0f, 4, 6, 1);
        this.backRightLeg.pivotX -= 1.0f;
        this.backLeftLeg.pivotX += 1.0f;
        this.backRightLeg.pivotZ += 0.0f;
        this.backLeftLeg.pivotZ += 0.0f;
        this.frontRightLeg.pivotX -= 1.0f;
        this.frontLeftLeg.pivotX += 1.0f;
        this.frontRightLeg.pivotZ -= 1.0f;
        this.frontLeftLeg.pivotZ -= 1.0f;
        this.field_3537 += 2.0f;
    }

    public ModelPart method_2800() {
        return this.head;
    }
}

