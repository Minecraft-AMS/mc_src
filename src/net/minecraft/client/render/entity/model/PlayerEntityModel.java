/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    private List<ModelPart> parts = Lists.newArrayList();
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPantLeg;
    public final ModelPart rightPantLeg;
    public final ModelPart jacket;
    private final ModelPart cape;
    private final ModelPart ears;
    private final boolean thinArms;

    public PlayerEntityModel(float scale, boolean thinArms) {
        super(RenderLayer::getEntityTranslucent, scale, 0.0f, 64, 64);
        this.thinArms = thinArms;
        this.ears = new ModelPart(this, 24, 0);
        this.ears.addCuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, scale);
        this.cape = new ModelPart(this, 0, 0);
        this.cape.setTextureSize(64, 32);
        this.cape.addCuboid(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, scale);
        if (thinArms) {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, scale);
            this.leftArm.setPivot(5.0f, 2.5f, 0.0f);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, scale);
            this.rightArm.setPivot(-5.0f, 2.5f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addCuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, scale + 0.25f);
            this.leftSleeve.setPivot(5.0f, 2.5f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addCuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, scale + 0.25f);
            this.rightSleeve.setPivot(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.25f);
            this.leftSleeve.setPivot(5.0f, 2.0f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.25f);
            this.rightSleeve.setPivot(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.leftPantLeg = new ModelPart(this, 0, 48);
        this.leftPantLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.25f);
        this.leftPantLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightPantLeg = new ModelPart(this, 0, 32);
        this.rightPantLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.25f);
        this.rightPantLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.jacket = new ModelPart(this, 16, 32);
        this.jacket.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale + 0.25f);
        this.jacket.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), (Iterable)ImmutableList.of((Object)this.leftPantLeg, (Object)this.rightPantLeg, (Object)this.leftSleeve, (Object)this.rightSleeve, (Object)this.jacket));
    }

    public void renderEars(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j) {
        this.ears.copyPositionAndRotation(this.head);
        this.ears.pivotX = 0.0f;
        this.ears.pivotY = 0.0f;
        this.ears.render(matrixStack, vertexConsumer, i, j);
    }

    public void renderCape(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j) {
        this.cape.render(matrixStack, vertexConsumer, i, j);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        super.setAngles(livingEntity, f, g, h, i, j);
        this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
        this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
        this.leftSleeve.copyPositionAndRotation(this.leftArm);
        this.rightSleeve.copyPositionAndRotation(this.rightArm);
        this.jacket.copyPositionAndRotation(this.torso);
        this.cape.pivotY = ((Entity)livingEntity).isInSneakingPose() ? 2.0f : 0.0f;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPantLeg.visible = visible;
        this.rightPantLeg.visible = visible;
        this.jacket.visible = visible;
        this.cape.visible = visible;
        this.ears.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrixStack) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float f = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.pivotX += f;
            modelPart.rotate(matrixStack);
            modelPart.pivotX -= f;
        } else {
            modelPart.rotate(matrixStack);
        }
    }

    public ModelPart getRandomPart(Random random) {
        return this.parts.get(random.nextInt(this.parts.size()));
    }

    @Override
    public void accept(ModelPart modelPart) {
        if (this.parts == null) {
            this.parts = Lists.newArrayList();
        }
        this.parts.add(modelPart);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((ModelPart)object);
    }
}

