/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SkullEntityModel;

@Environment(value=EnvType.CLIENT)
public class DragonHeadEntityModel
extends SkullEntityModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadEntityModel(float f) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        float g = -16.0f;
        this.head = new ModelPart(this, "head");
        this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, f, 176, 44);
        this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, f, 112, 30);
        this.head.mirror = true;
        this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.head.mirror = false;
        this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.jaw = new ModelPart(this, "jaw");
        this.jaw.setPivot(0.0f, 4.0f, -8.0f);
        this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, f, 176, 65);
        this.head.addChild(this.jaw);
    }

    @Override
    public void render(float limbMoveAngle, float limbMoveAmount, float age, float headYaw, float headPitch, float scale) {
        this.jaw.pitch = (float)(Math.sin(limbMoveAngle * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        GlStateManager.translatef(0.0f, -0.374375f, 0.0f);
        GlStateManager.scalef(0.75f, 0.75f, 0.75f);
        this.head.render(scale);
    }
}

