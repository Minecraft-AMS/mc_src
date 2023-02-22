/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
extends BlockEntityRenderer<T> {
    private final ModelPart field_20817;
    private final ModelPart field_20818;
    private final ModelPart field_20819;
    private final ModelPart field_20820;
    private final ModelPart field_20821;
    private final ModelPart field_20822;
    private final ModelPart field_21479;
    private final ModelPart field_21480;
    private final ModelPart field_21481;
    private boolean isChristmas;

    public ChestBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
        this.field_20818 = new ModelPart(64, 64, 0, 19);
        this.field_20818.addCuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f, 0.0f);
        this.field_20817 = new ModelPart(64, 64, 0, 0);
        this.field_20817.addCuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f, 0.0f);
        this.field_20817.pivotY = 9.0f;
        this.field_20817.pivotZ = 1.0f;
        this.field_20819 = new ModelPart(64, 64, 0, 0);
        this.field_20819.addCuboid(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f, 0.0f);
        this.field_20819.pivotY = 8.0f;
        this.field_20821 = new ModelPart(64, 64, 0, 19);
        this.field_20821.addCuboid(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.field_20820 = new ModelPart(64, 64, 0, 0);
        this.field_20820.addCuboid(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.field_20820.pivotY = 9.0f;
        this.field_20820.pivotZ = 1.0f;
        this.field_20822 = new ModelPart(64, 64, 0, 0);
        this.field_20822.addCuboid(15.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.field_20822.pivotY = 8.0f;
        this.field_21480 = new ModelPart(64, 64, 0, 19);
        this.field_21480.addCuboid(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.field_21479 = new ModelPart(64, 64, 0, 0);
        this.field_21479.addCuboid(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.field_21479.pivotY = 9.0f;
        this.field_21479.pivotZ = 1.0f;
        this.field_21481 = new ModelPart(64, 64, 0, 0);
        this.field_21481.addCuboid(0.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.field_21481.pivotY = 8.0f;
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = ((BlockEntity)blockEntity).getWorld();
        boolean bl = world != null;
        BlockState blockState = bl ? ((BlockEntity)blockEntity).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        Block block = blockState.getBlock();
        if (!(block instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock abstractChestBlock = (AbstractChestBlock)block;
        boolean bl2 = chestType != ChestType.SINGLE;
        matrices.push();
        float f = blockState.get(ChestBlock.FACING).asRotation();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
        matrices.translate(-0.5, -0.5, -0.5);
        DoubleBlockProperties.PropertySource<Object> propertySource = bl ? abstractChestBlock.getBlockEntitySource(blockState, world, ((BlockEntity)blockEntity).getPos(), true) : DoubleBlockProperties.PropertyRetriever::getFallback;
        float g = propertySource.apply(ChestBlock.getAnimationProgressRetriever((ChestAnimationProgress)blockEntity)).get(tickDelta);
        g = 1.0f - g;
        g = 1.0f - g * g * g;
        int i = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).applyAsInt(light);
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTexture(blockEntity, chestType, this.isChristmas);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        if (bl2) {
            if (chestType == ChestType.LEFT) {
                this.method_22749(matrices, vertexConsumer, this.field_21479, this.field_21481, this.field_21480, g, i, overlay);
            } else {
                this.method_22749(matrices, vertexConsumer, this.field_20820, this.field_20822, this.field_20821, g, i, overlay);
            }
        } else {
            this.method_22749(matrices, vertexConsumer, this.field_20817, this.field_20819, this.field_20818, g, i, overlay);
        }
        matrices.pop();
    }

    private void method_22749(MatrixStack matrixStack, VertexConsumer vertexConsumer, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, float f, int i, int j) {
        modelPart2.pitch = modelPart.pitch = -(f * 1.5707964f);
        modelPart.render(matrixStack, vertexConsumer, i, j);
        modelPart2.render(matrixStack, vertexConsumer, i, j);
        modelPart3.render(matrixStack, vertexConsumer, i, j);
    }
}

