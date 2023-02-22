/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer
extends EntityRenderer<ItemFrameEntity> {
    private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
    private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public ItemFrameEntityRenderer(EntityRenderDispatcher renderManager, ItemRenderer itemRenderer) {
        super(renderManager);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
        double i = (double)blockPos.getX() - itemFrameEntity.x + d;
        double j = (double)blockPos.getY() - itemFrameEntity.y + e;
        double k = (double)blockPos.getZ() - itemFrameEntity.z + f;
        GlStateManager.translated(i + 0.5, j + 0.5, k + 0.5);
        GlStateManager.rotatef(itemFrameEntity.pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f - itemFrameEntity.yaw, 0.0f, 1.0f, 0.0f);
        this.renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
        BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
        ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(itemFrameEntity));
        }
        blockRenderManager.getModelRenderer().render(bakedModelManager.getModel(modelIdentifier), 1.0f, 1.0f, 1.0f, 1.0f);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
            GlStateManager.pushLightingAttributes();
            DiffuseLighting.enable();
        }
        GlStateManager.translatef(0.0f, 0.0f, 0.4375f);
        this.renderItem(itemFrameEntity);
        if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
            DiffuseLighting.disable();
            GlStateManager.popAttributes();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        this.renderLabelIfPresent(itemFrameEntity, d + (double)((float)itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3f), e - 0.25, f + (double)((float)itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3f));
    }

    @Override
    @Nullable
    protected Identifier getTexture(ItemFrameEntity itemFrameEntity) {
        return null;
    }

    private void renderItem(ItemFrameEntity itemFrameEntity) {
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();
        if (itemStack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        boolean bl = itemStack.getItem() == Items.FILLED_MAP;
        int i = bl ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
        GlStateManager.rotatef((float)i * 360.0f / 8.0f, 0.0f, 0.0f, 1.0f);
        if (bl) {
            GlStateManager.disableLighting();
            this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEX);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            float f = 0.0078125f;
            GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
            GlStateManager.translatef(-64.0f, -64.0f, 0.0f);
            MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
            GlStateManager.translatef(0.0f, 0.0f, -1.0f);
            if (mapState != null) {
                this.client.gameRenderer.getMapRenderer().draw(mapState, true);
            }
        } else {
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderItem(itemStack, ModelTransformation.Type.FIXED);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderLabelIfPresent(ItemFrameEntity itemFrameEntity, double d, double e, double f) {
        float h;
        if (!MinecraftClient.isHudEnabled() || itemFrameEntity.getHeldItemStack().isEmpty() || !itemFrameEntity.getHeldItemStack().hasCustomName() || this.renderManager.targetedEntity != itemFrameEntity) {
            return;
        }
        double g = itemFrameEntity.squaredDistanceTo(this.renderManager.camera.getPos());
        float f2 = h = itemFrameEntity.isInSneakingPose() ? 32.0f : 64.0f;
        if (g >= (double)(h * h)) {
            return;
        }
        String string = itemFrameEntity.getHeldItemStack().getName().asFormattedString();
        this.renderLabel(itemFrameEntity, string, d, e, f, 64);
    }
}

