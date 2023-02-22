/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer
extends EntityRenderer<ItemFrameEntity> {
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
    private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public ItemFrameEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
        super(dispatcher);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ItemFrameEntity itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ItemStack itemStack;
        super.render(itemFrameEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        Direction direction = itemFrameEntity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(itemFrameEntity.pitch));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - itemFrameEntity.yaw));
        boolean bl = itemFrameEntity.isInvisible();
        if (!bl) {
            BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
            BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
            matrixStack.push();
            matrixStack.translate(-0.5, -0.5, -0.5);
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), null, bakedModelManager.getModel(modelIdentifier), 1.0f, 1.0f, 1.0f, i, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }
        if (!(itemStack = itemFrameEntity.getHeldItemStack()).isEmpty()) {
            boolean bl2;
            boolean bl3 = bl2 = itemStack.getItem() == Items.FILLED_MAP;
            if (bl) {
                matrixStack.translate(0.0, 0.0, 0.5);
            } else {
                matrixStack.translate(0.0, 0.0, 0.4375);
            }
            int j = bl2 ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0f / 8.0f));
            if (bl2) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                float h = 0.0078125f;
                matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
                matrixStack.translate(-64.0, -64.0, 0.0);
                MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
                matrixStack.translate(0.0, 0.0, -1.0);
                if (mapState != null) {
                    this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapState, true, i);
                }
            } else {
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
            }
        }
        matrixStack.pop();
    }

    @Override
    public Vec3d getPositionOffset(ItemFrameEntity itemFrameEntity, float f) {
        return new Vec3d((float)itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3f, -0.25, (float)itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3f);
    }

    @Override
    public Identifier getTexture(ItemFrameEntity itemFrameEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    protected boolean hasLabel(ItemFrameEntity itemFrameEntity) {
        if (!MinecraftClient.isHudEnabled() || itemFrameEntity.getHeldItemStack().isEmpty() || !itemFrameEntity.getHeldItemStack().hasCustomName() || this.dispatcher.targetedEntity != itemFrameEntity) {
            return false;
        }
        double d = this.dispatcher.getSquaredDistanceToCamera(itemFrameEntity);
        float f = itemFrameEntity.isSneaky() ? 32.0f : 64.0f;
        return d < (double)(f * f);
    }

    @Override
    protected void renderLabelIfPresent(ItemFrameEntity itemFrameEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.renderLabelIfPresent(itemFrameEntity, itemFrameEntity.getHeldItemStack().getName(), matrixStack, vertexConsumerProvider, i);
    }
}

