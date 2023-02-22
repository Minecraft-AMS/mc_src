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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, false);
    }

    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
        super(entityRenderDispatcher, new PlayerEntityModel(0.0f, bl), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new StuckStingersFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(abstractClientPlayerEntity);
        super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Vec3d getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            return new Vec3d(0.0, -0.125, 0.0);
        }
        return super.getPositionOffset(abstractClientPlayerEntity, f);
    }

    private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        if (abstractClientPlayerEntity.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.helmet.visible = true;
        } else {
            ItemStack itemStack = abstractClientPlayerEntity.getMainHandStack();
            ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
            playerEntityModel.setVisible(true);
            playerEntityModel.helmet.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = this.getArmPose(abstractClientPlayerEntity, itemStack, itemStack2, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = this.getArmPose(abstractClientPlayerEntity, itemStack, itemStack2, Hand.OFF_HAND);
            if (abstractClientPlayerEntity.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }
    }

    private BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
        ItemStack itemStack3;
        BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack4 = itemStack3 = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
        if (!itemStack3.isEmpty()) {
            armPose = BipedEntityModel.ArmPose.ITEM;
            if (abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack3.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    armPose = BipedEntityModel.ArmPose.BLOCK;
                } else if (useAction == UseAction.BOW) {
                    armPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
                } else if (useAction == UseAction.SPEAR) {
                    armPose = BipedEntityModel.ArmPose.THROW_SPEAR;
                } else if (useAction == UseAction.CROSSBOW && hand == abstractClientPlayerEntity.getActiveHand()) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean bl = itemStack.getItem() == Items.CROSSBOW;
                boolean bl2 = CrossbowItem.isCharged(itemStack);
                boolean bl3 = itemStack2.getItem() == Items.CROSSBOW;
                boolean bl4 = CrossbowItem.isCharged(itemStack2);
                if (bl && bl2) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }
                if (bl3 && bl4 && itemStack.getItem().getUseAction(itemStack) == UseAction.NONE) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }
        return armPose;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getSkinTexture();
    }

    @Override
    protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, String string, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Scoreboard scoreboard;
        ScoreboardObjective scoreboardObjective;
        double d = this.renderManager.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0 && (scoreboardObjective = (scoreboard = abstractClientPlayerEntity.getScoreboard()).getObjectiveForSlot(2)) != null) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
            super.renderLabelIfPresent(abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().asFormattedString(), matrixStack, vertexConsumerProvider, i);
            this.getFontRenderer().getClass();
            matrixStack.translate(0.0, 9.0f * 1.15f * 0.025f, 0.0);
        }
        super.renderLabelIfPresent(abstractClientPlayerEntity, string, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).leftArm, ((PlayerEntityModel)this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(player);
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.isSneaking = false;
        playerEntityModel.field_3396 = 0.0f;
        playerEntityModel.setAngles(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        arm.pitch = 0.0f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0f;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = abstractClientPlayerEntity.getLeaningPitch(h);
        if (abstractClientPlayerEntity.isFallFlying()) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = (float)abstractClientPlayerEntity.getRoll() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0f - abstractClientPlayerEntity.pitch)));
            }
            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
            double d = Entity.squaredHorizontalLength(vec3d2);
            double e = Entity.squaredHorizontalLength(vec3d);
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0f) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = abstractClientPlayerEntity.isTouchingWater() ? -90.0f - abstractClientPlayerEntity.pitch : -90.0f;
            float k = MathHelper.lerp(i, 0.0f, j);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0, -1.0, 0.3f);
            }
        } else {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }

    @Override
    public /* synthetic */ Vec3d getPositionOffset(Entity entity, float tickDelta) {
        return this.getPositionOffset((AbstractClientPlayerEntity)entity, tickDelta);
    }
}

