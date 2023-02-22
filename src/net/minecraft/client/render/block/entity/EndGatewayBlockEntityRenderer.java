/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer
extends EndPortalBlockEntityRenderer<EndGatewayBlockEntity> {
    private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

    public EndGatewayBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EndGatewayBlockEntity endGatewayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
            float g = endGatewayBlockEntity.isRecentlyGenerated() ? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f) : endGatewayBlockEntity.getCooldownBeamHeight(f);
            double d = endGatewayBlockEntity.isRecentlyGenerated() ? 256.0 : 50.0;
            g = MathHelper.sin(g * (float)Math.PI);
            int k = MathHelper.floor((double)g * d);
            float[] fs = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
            long l = endGatewayBlockEntity.getWorld().getTime();
            BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, l, 0, k, fs, 0.15f, 0.175f);
            BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, l, 0, -k, fs, 0.15f, 0.175f);
        }
        super.render(endGatewayBlockEntity, f, matrixStack, vertexConsumerProvider, i, j);
    }

    @Override
    protected int method_3592(double d) {
        return super.method_3592(d) + 1;
    }

    @Override
    protected float method_3594() {
        return 1.0f;
    }
}

