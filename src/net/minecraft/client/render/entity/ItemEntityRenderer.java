/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer renderer) {
        super(dispatcher);
        this.itemRenderer = renderer;
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    private int method_3997(ItemEntity itemEntity, double d, double e, double f, float g, BakedModel bakedModel) {
        ItemStack itemStack = itemEntity.getStack();
        Item item = itemStack.getItem();
        if (item == null) {
            return 0;
        }
        boolean bl = bakedModel.hasDepth();
        int i = this.getRenderedAmount(itemStack);
        float h = 0.25f;
        float j = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0f + itemEntity.hoverHeight) * 0.1f + 0.1f;
        float k = bakedModel.getTransformation().getTransformation((ModelTransformation.Type)ModelTransformation.Type.GROUND).scale.getY();
        GlStateManager.translatef((float)d, (float)e + j + 0.25f * k, (float)f);
        if (bl || this.renderManager.gameOptions != null) {
            float l = (((float)itemEntity.getAge() + g) / 20.0f + itemEntity.hoverHeight) * 57.295776f;
            GlStateManager.rotatef(l, 0.0f, 1.0f, 0.0f);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        return i;
    }

    private int getRenderedAmount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    @Override
    public void render(ItemEntity itemEntity, double d, double e, double f, float g, float h) {
        float p;
        float o;
        ItemStack itemStack = itemEntity.getStack();
        int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(i);
        boolean bl = false;
        if (this.bindEntityTexture(itemEntity)) {
            this.renderManager.textureManager.getTexture(this.getTexture(itemEntity)).pushFilter(false, false);
            bl = true;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        DiffuseLighting.enable();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.world, null);
        int j = this.method_3997(itemEntity, d, e, f, h, bakedModel);
        float k = bakedModel.getTransformation().ground.scale.getX();
        float l = bakedModel.getTransformation().ground.scale.getY();
        float m = bakedModel.getTransformation().ground.scale.getZ();
        boolean bl2 = bakedModel.hasDepth();
        if (!bl2) {
            float n = -0.0f * (float)(j - 1) * 0.5f * k;
            o = -0.0f * (float)(j - 1) * 0.5f * l;
            p = -0.09375f * (float)(j - 1) * 0.5f * m;
            GlStateManager.translatef(n, o, p);
        }
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(itemEntity));
        }
        for (int q = 0; q < j; ++q) {
            if (bl2) {
                GlStateManager.pushMatrix();
                if (q > 0) {
                    o = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    p = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float r = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    GlStateManager.translatef(o, p, r);
                }
                bakedModel.getTransformation().applyGl(ModelTransformation.Type.GROUND);
                this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
                GlStateManager.popMatrix();
                continue;
            }
            GlStateManager.pushMatrix();
            if (q > 0) {
                o = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                p = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                GlStateManager.translatef(o, p, 0.0f);
            }
            bakedModel.getTransformation().applyGl(ModelTransformation.Type.GROUND);
            this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
            GlStateManager.popMatrix();
            GlStateManager.translatef(0.0f * k, 0.0f * l, 0.09375f * m);
        }
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(itemEntity);
        if (bl) {
            this.renderManager.textureManager.getTexture(this.getTexture(itemEntity)).popFilter();
        }
        super.render(itemEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(ItemEntity itemEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

