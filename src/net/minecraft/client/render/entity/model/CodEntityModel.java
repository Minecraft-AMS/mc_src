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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CodEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart body;
    private final ModelPart topFin;
    private final ModelPart head;
    private final ModelPart face;
    private final ModelPart rightFin;
    private final ModelPart leftFin;
    private final ModelPart tailFin;

    public CodEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.body = new ModelPart(this, 0, 0);
        this.body.addCuboid(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f);
        this.body.setPivot(0.0f, 22.0f, 0.0f);
        this.head = new ModelPart(this, 11, 0);
        this.head.addCuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setPivot(0.0f, 22.0f, 0.0f);
        this.face = new ModelPart(this, 0, 0);
        this.face.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f);
        this.face.setPivot(0.0f, 22.0f, -3.0f);
        this.rightFin = new ModelPart(this, 22, 1);
        this.rightFin.addCuboid(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.rightFin.setPivot(-1.0f, 23.0f, 0.0f);
        this.rightFin.roll = -0.7853982f;
        this.leftFin = new ModelPart(this, 22, 4);
        this.leftFin.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.leftFin.setPivot(1.0f, 23.0f, 0.0f);
        this.leftFin.roll = 0.7853982f;
        this.tailFin = new ModelPart(this, 22, 3);
        this.tailFin.addCuboid(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f);
        this.tailFin.setPivot(0.0f, 22.0f, 7.0f);
        this.topFin = new ModelPart(this, 20, -6);
        this.topFin.addCuboid(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f);
        this.topFin.setPivot(0.0f, 20.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.body, (Object)this.head, (Object)this.face, (Object)this.rightFin, (Object)this.leftFin, (Object)this.tailFin, (Object)this.topFin);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.tailFin.yaw = -f * 0.45f * MathHelper.sin(0.6f * animationProgress);
    }
}

