/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
extends BlockEntityRenderer<LecternBlockEntity> {
    private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
    private final BookModel book = new BookModel();

    @Override
    public void render(LecternBlockEntity lecternBlockEntity, double d, double e, double f, float g, int i) {
        BlockState blockState = lecternBlockEntity.getCachedState();
        if (!blockState.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d + 0.5f, (float)e + 1.0f + 0.0625f, (float)f + 0.5f);
        float h = blockState.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        GlStateManager.rotatef(-h, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(67.5f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(0.0f, -0.125f, 0.0f);
        this.bindTexture(BOOK_TEXTURE);
        GlStateManager.enableCull();
        this.book.render(0.0f, 0.1f, 0.9f, 1.2f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
}

