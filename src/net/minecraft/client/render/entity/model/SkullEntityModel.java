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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final ModelPart head;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int textureU, int textureV, int textureWidth, int textureHeight) {
        super(RenderLayer::getEntityTranslucent);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.head = new ModelPart(this, textureU, textureV);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
    }

    public void method_2821(float f, float g, float h) {
        this.head.yaw = g * ((float)Math.PI / 180);
        this.head.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

