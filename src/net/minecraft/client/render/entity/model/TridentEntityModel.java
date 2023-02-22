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
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentEntityModel
extends Model {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final ModelPart trident = new ModelPart(32, 32, 0, 6);

    public TridentEntityModel() {
        super(RenderLayer::getEntitySolid);
        this.trident.addCuboid(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f, 0.0f);
        ModelPart modelPart = new ModelPart(32, 32, 4, 0);
        modelPart.addCuboid(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f);
        this.trident.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(32, 32, 4, 3);
        modelPart2.addCuboid(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.trident.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(32, 32, 0, 0);
        modelPart3.addCuboid(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.trident.addChild(modelPart3);
        ModelPart modelPart4 = new ModelPart(32, 32, 4, 3);
        modelPart4.mirror = true;
        modelPart4.addCuboid(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.trident.addChild(modelPart4);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.trident.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

