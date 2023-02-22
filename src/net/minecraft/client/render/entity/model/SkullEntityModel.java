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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final ModelPart skull;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int textureU, int textureV, int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.skull = new ModelPart(this, textureU, textureV);
        this.skull.addCuboid(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f);
        this.skull.setPivot(0.0f, 0.0f, 0.0f);
    }

    public void render(float limbMoveAngle, float limbMoveAmount, float age, float headYaw, float headPitch, float scale) {
        this.skull.yaw = headYaw * ((float)Math.PI / 180);
        this.skull.pitch = headPitch * ((float)Math.PI / 180);
        this.skull.render(scale);
    }
}

