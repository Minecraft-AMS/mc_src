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
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
extends BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel book = new BookModel();

    public LecternBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = lecternBlockEntity.getCachedState();
        if (!blockState.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5, 1.0625, 0.5);
        float g = blockState.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-g));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(67.5f));
        matrixStack.translate(0.0, -0.125, 0.0);
        this.book.setPageAngles(0.0f, 0.1f, 0.9f, 1.2f);
        VertexConsumer vertexConsumer = EnchantingTableBlockEntityRenderer.BOOK_TEX.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.method_24184(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}

