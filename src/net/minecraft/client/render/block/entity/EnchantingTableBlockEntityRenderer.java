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
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
extends BlockEntityRenderer<EnchantingTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/enchanting_table_book"));
    private final BookModel book = new BookModel();

    public EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EnchantingTableBlockEntity enchantingTableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float h;
        matrixStack.push();
        matrixStack.translate(0.5, 0.75, 0.5);
        float g = (float)enchantingTableBlockEntity.ticks + f;
        matrixStack.translate(0.0, 0.1f + MathHelper.sin(g * 0.1f) * 0.01f, 0.0);
        for (h = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963; h >= (float)Math.PI; h -= (float)Math.PI * 2) {
        }
        while (h < (float)(-Math.PI)) {
            h += (float)Math.PI * 2;
        }
        float k = enchantingTableBlockEntity.field_11963 + h * f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(-k));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(80.0f));
        float l = MathHelper.lerp(f, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25f) * 1.6f - 0.3f;
        float n = MathHelper.fractionalPart(l + 0.75f) * 1.6f - 0.3f;
        float o = MathHelper.lerp(f, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o);
        VertexConsumer vertexConsumer = BOOK_TEX.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.method_24184(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}

