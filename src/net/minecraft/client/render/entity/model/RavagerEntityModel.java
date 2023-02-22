/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends CompositeEntityModel<RavagerEntity> {
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    public RavagerEntityModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        int i = 16;
        float f = 0.0f;
        this.neck = new ModelPart(this);
        this.neck.setPivot(0.0f, -7.0f, -1.5f);
        this.neck.setTextureOffset(68, 73).addCuboid(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f, 0.0f);
        this.head = new ModelPart(this);
        this.head.setPivot(0.0f, 16.0f, -17.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f, 0.0f);
        ModelPart modelPart = new ModelPart(this);
        modelPart.setPivot(-10.0f, -14.0f, -8.0f);
        modelPart.setTextureOffset(74, 55).addCuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart.pitch = 1.0995574f;
        this.head.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this);
        modelPart2.mirror = true;
        modelPart2.setPivot(8.0f, -14.0f, -8.0f);
        modelPart2.setTextureOffset(74, 55).addCuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart2.pitch = 1.0995574f;
        this.head.addChild(modelPart2);
        this.jaw = new ModelPart(this);
        this.jaw.setPivot(0.0f, -2.0f, 2.0f);
        this.jaw.setTextureOffset(0, 36).addCuboid(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f, 0.0f);
        this.head.addChild(this.jaw);
        this.neck.addChild(this.head);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(0, 55).addCuboid(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f, 0.0f);
        this.torso.setTextureOffset(0, 91).addCuboid(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f, 0.0f);
        this.torso.setPivot(0.0f, 1.0f, 2.0f);
        this.rightBackLeg = new ModelPart(this, 96, 0);
        this.rightBackLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.rightBackLeg.setPivot(-8.0f, -13.0f, 18.0f);
        this.leftBackLeg = new ModelPart(this, 96, 0);
        this.leftBackLeg.mirror = true;
        this.leftBackLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leftBackLeg.setPivot(8.0f, -13.0f, 18.0f);
        this.rightFrontLeg = new ModelPart(this, 64, 0);
        this.rightFrontLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.rightFrontLeg.setPivot(-8.0f, -13.0f, -5.0f);
        this.leftFrontLeg = new ModelPart(this, 64, 0);
        this.leftFrontLeg.mirror = true;
        this.leftFrontLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leftFrontLeg.setPivot(8.0f, -13.0f, -5.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.neck, (Object)this.torso, (Object)this.rightBackLeg, (Object)this.leftBackLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg);
    }

    @Override
    public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        float k = 0.4f * g;
        this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
        this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
    }

    @Override
    public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
        super.animateModel(ravagerEntity, f, g, h);
        int i = ravagerEntity.getStunTick();
        int j = ravagerEntity.getRoarTick();
        int k = 20;
        int l = ravagerEntity.getAttackTick();
        int m = 10;
        if (l > 0) {
            float n = MathHelper.method_24504((float)l - h, 10.0f);
            float o = (1.0f + n) * 0.5f;
            float p = o * o * o * 12.0f;
            float q = p * MathHelper.sin(this.neck.pitch);
            this.neck.pivotZ = -6.5f + p;
            this.neck.pivotY = -7.0f - q;
            float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
            this.jaw.pitch = 1.5707964f * r;
            this.jaw.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
        } else {
            float n = -1.0f;
            float o = -1.0f * MathHelper.sin(this.neck.pitch);
            this.neck.pivotX = 0.0f;
            this.neck.pivotY = -7.0f - o;
            this.neck.pivotZ = 5.5f;
            boolean bl = i > 0;
            this.neck.pitch = bl ? 0.21991149f : 0.0f;
            this.jaw.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)i / 40.0;
                this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (j > 0) {
                float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
                this.jaw.pitch = 1.5707964f * q;
            }
        }
    }
}

