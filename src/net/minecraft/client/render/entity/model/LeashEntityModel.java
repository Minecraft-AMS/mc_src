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

@Environment(value=EnvType.CLIENT)
public class LeashEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3431;

    public LeashEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.field_3431 = new ModelPart(this, 0, 0);
        this.field_3431.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.field_3431.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.field_3431);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        this.field_3431.yaw = headYaw * ((float)Math.PI / 180);
        this.field_3431.pitch = headPitch * ((float)Math.PI / 180);
    }
}

