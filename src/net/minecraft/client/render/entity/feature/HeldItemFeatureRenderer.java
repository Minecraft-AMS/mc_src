/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private final HeldItemRenderer heldItemRenderer;

    public HeldItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack2;
        boolean bl = ((LivingEntity)livingEntity).getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? ((LivingEntity)livingEntity).getOffHandStack() : ((LivingEntity)livingEntity).getMainHandStack();
        ItemStack itemStack3 = itemStack2 = bl ? ((LivingEntity)livingEntity).getMainHandStack() : ((LivingEntity)livingEntity).getOffHandStack();
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return;
        }
        matrixStack.push();
        if (((EntityModel)this.getContextModel()).child) {
            float m = 0.5f;
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        this.renderItem((LivingEntity)livingEntity, itemStack2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i);
        this.renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        ((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        boolean bl = arm == Arm.LEFT;
        matrices.translate((float)(bl ? -1 : 1) / 16.0f, 0.125, -0.625);
        this.heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
        matrices.pop();
    }
}

