/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HeldItemRenderer {
    private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
    private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");
    private final MinecraftClient client;
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher renderManager;
    private final ItemRenderer itemRenderer;

    public HeldItemRenderer(MinecraftClient client) {
        this.client = client;
        this.renderManager = client.getEntityRenderManager();
        this.itemRenderer = client.getItemRenderer();
    }

    public void renderItem(LivingEntity holder, ItemStack stack, ModelTransformation.Type type) {
        this.renderItemFromSide(holder, stack, type, false);
    }

    public void renderItemFromSide(LivingEntity holder, ItemStack stack, ModelTransformation.Type transformation, boolean bl) {
        boolean bl2;
        if (stack.isEmpty()) {
            return;
        }
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        GlStateManager.pushMatrix();
        boolean bl3 = bl2 = this.itemRenderer.hasDepthInGui(stack) && block.getRenderLayer() == RenderLayer.TRANSLUCENT;
        if (bl2) {
            GlStateManager.depthMask(false);
        }
        this.itemRenderer.renderHeldItem(stack, holder, transformation, bl);
        if (bl2) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }

    private void rotate(float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(yaw, 0.0f, 1.0f, 0.0f);
        DiffuseLighting.enable();
        GlStateManager.popMatrix();
    }

    private void applyLightmap() {
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        int i = this.client.world.getLightmapIndex(new BlockPos(abstractClientPlayerEntity.x, abstractClientPlayerEntity.y + (double)abstractClientPlayerEntity.getStandingEyeHeight(), abstractClientPlayerEntity.z), 0);
        float f = i & 0xFFFF;
        float g = i >> 16;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, f, g);
    }

    private void applyCameraAngles(float tickDelta) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        float f = MathHelper.lerp(tickDelta, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
        float g = MathHelper.lerp(tickDelta, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
        GlStateManager.rotatef((clientPlayerEntity.getPitch(tickDelta) - f) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef((clientPlayerEntity.getYaw(tickDelta) - g) * 0.1f, 0.0f, 1.0f, 0.0f);
    }

    private float getMapAngle(float tickDelta) {
        float f = 1.0f - tickDelta / 45.0f + 0.1f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
        return f;
    }

    private void renderArms() {
        if (this.client.player.isInvisible()) {
            return;
        }
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.renderArm(Arm.RIGHT);
        this.renderArm(Arm.LEFT);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }

    private void renderArm(Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        Object entityRenderer = this.renderManager.getRenderer(this.client.player);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
        GlStateManager.pushMatrix();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        GlStateManager.rotatef(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(f * -41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(this.client.player);
        }
        GlStateManager.popMatrix();
    }

    private void renderMapInOneHand(float equipProgress, Arm hand, float f, ItemStack item) {
        float g = hand == Arm.RIGHT ? 1.0f : -1.0f;
        GlStateManager.translatef(g * 0.125f, -0.125f, 0.0f);
        if (!this.client.player.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(g * 10.0f, 0.0f, 0.0f, 1.0f);
            this.renderArmHoldingItem(equipProgress, f, hand);
            GlStateManager.popMatrix();
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(g * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
        float h = MathHelper.sqrt(f);
        float i = MathHelper.sin(h * (float)Math.PI);
        float j = -0.5f * i;
        float k = 0.4f * MathHelper.sin(h * ((float)Math.PI * 2));
        float l = -0.3f * MathHelper.sin(f * (float)Math.PI);
        GlStateManager.translatef(g * j, k - 0.3f * i, l);
        GlStateManager.rotatef(i * -45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(g * i * -30.0f, 0.0f, 1.0f, 0.0f);
        this.renderFirstPersonMap(item);
        GlStateManager.popMatrix();
    }

    private void renderMapInBothHands(float pitch, float equipProgress, float f) {
        float g = MathHelper.sqrt(f);
        float h = -0.2f * MathHelper.sin(f * (float)Math.PI);
        float i = -0.4f * MathHelper.sin(g * (float)Math.PI);
        GlStateManager.translatef(0.0f, -h / 2.0f, i);
        float j = this.getMapAngle(pitch);
        GlStateManager.translatef(0.0f, 0.04f + equipProgress * -1.2f + j * -0.5f, -0.72f);
        GlStateManager.rotatef(j * -85.0f, 1.0f, 0.0f, 0.0f);
        this.renderArms();
        float k = MathHelper.sin(g * (float)Math.PI);
        GlStateManager.rotatef(k * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(this.mainHand);
    }

    private void renderFirstPersonMap(ItemStack map) {
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scalef(0.38f, 0.38f, 0.38f);
        GlStateManager.disableLighting();
        this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.translatef(-0.5f, -0.5f, 0.0f);
        GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
        bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
        bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
        bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
        tessellator.draw();
        MapState mapState = FilledMapItem.getOrCreateMapState(map, this.client.world);
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(mapState, false);
        }
        GlStateManager.enableLighting();
    }

    private void renderArmHoldingItem(float f, float g, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float i = MathHelper.sqrt(g);
        float j = -0.3f * MathHelper.sin(i * (float)Math.PI);
        float k = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float l = -0.4f * MathHelper.sin(g * (float)Math.PI);
        GlStateManager.translatef(h * (j + 0.64000005f), k + -0.6f + f * -0.6f, l + -0.71999997f);
        GlStateManager.rotatef(h * 45.0f, 0.0f, 1.0f, 0.0f);
        float m = MathHelper.sin(g * g * (float)Math.PI);
        float n = MathHelper.sin(i * (float)Math.PI);
        GlStateManager.rotatef(h * n * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(h * m * -20.0f, 0.0f, 0.0f, 1.0f);
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        GlStateManager.translatef(h * -1.0f, 3.6f, 3.5f);
        GlStateManager.rotatef(h * 120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(h * -135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(h * 5.6f, 0.0f, 0.0f);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
        GlStateManager.disableCull();
        if (bl) {
            playerEntityRenderer.renderRightArm(abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(abstractClientPlayerEntity);
        }
        GlStateManager.enableCull();
    }

    private void applyEatOrDrinkTransformation(float tickDelta, Arm hand, ItemStack item) {
        float h;
        float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        float g = f / (float)item.getMaxUseTime();
        if (g < 0.8f) {
            h = MathHelper.abs(MathHelper.cos(f / 4.0f * (float)Math.PI) * 0.1f);
            GlStateManager.translatef(0.0f, h, 0.0f);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = hand == Arm.RIGHT ? 1 : -1;
        GlStateManager.translatef(h * 0.6f * (float)i, h * -0.5f, h * 0.0f);
        GlStateManager.rotatef((float)i * h * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(h * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef((float)i * h * 30.0f, 0.0f, 0.0f, 1.0f);
    }

    private void method_3217(Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        GlStateManager.rotatef((float)i * (45.0f + g * -20.0f), 0.0f, 1.0f, 0.0f);
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        GlStateManager.rotatef((float)i * h * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(h * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef((float)i * -45.0f, 0.0f, 1.0f, 0.0f);
    }

    private void applyHandOffset(Arm hand, float f) {
        int i = hand == Arm.RIGHT ? 1 : -1;
        GlStateManager.translatef((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void renderFirstPersonItem(float tickDelta) {
        ItemStack itemStack;
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        float f = abstractClientPlayerEntity.getHandSwingProgress(tickDelta);
        Hand hand = (Hand)((Object)MoreObjects.firstNonNull((Object)((Object)abstractClientPlayerEntity.preferredHand), (Object)((Object)Hand.MAIN_HAND)));
        float g = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
        float h = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw);
        boolean bl = true;
        boolean bl2 = true;
        if (((LivingEntity)abstractClientPlayerEntity).isUsingItem()) {
            ItemStack itemStack2;
            Hand hand2;
            itemStack = abstractClientPlayerEntity.getActiveItem();
            if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
                bl = ((LivingEntity)abstractClientPlayerEntity).getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((hand2 = ((LivingEntity)abstractClientPlayerEntity).getActiveHand()) == Hand.MAIN_HAND && (itemStack2 = abstractClientPlayerEntity.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                bl2 = false;
            }
        } else {
            itemStack = abstractClientPlayerEntity.getMainHandStack();
            ItemStack itemStack3 = abstractClientPlayerEntity.getOffHandStack();
            if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
                boolean bl4 = bl2 = !bl;
            }
            if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
                bl = !itemStack.isEmpty();
                bl2 = !bl;
            }
        }
        this.rotate(g, h);
        this.applyLightmap();
        this.applyCameraAngles(tickDelta);
        GlStateManager.enableRescaleNormal();
        if (bl) {
            float i = hand == Hand.MAIN_HAND ? f : 0.0f;
            float j = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity, tickDelta, g, Hand.MAIN_HAND, i, this.mainHand, j);
        }
        if (bl2) {
            float i = hand == Hand.OFF_HAND ? f : 0.0f;
            float j = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity, tickDelta, g, Hand.OFF_HAND, i, this.offHand, j);
        }
        GlStateManager.disableRescaleNormal();
        DiffuseLighting.disable();
    }

    public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float f, ItemStack item, float equipProgress) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
        GlStateManager.pushMatrix();
        if (item.isEmpty()) {
            if (bl && !player.isInvisible()) {
                this.renderArmHoldingItem(equipProgress, f, arm);
            }
        } else if (item.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(pitch, equipProgress, f);
            } else {
                this.renderMapInOneHand(equipProgress, arm, f, item);
            }
        } else if (item.getItem() == Items.CROSSBOW) {
            int i;
            boolean bl2 = CrossbowItem.isCharged(item);
            boolean bl3 = arm == Arm.RIGHT;
            int n = i = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                this.applyHandOffset(arm, equipProgress);
                GlStateManager.translatef((float)i * -0.4785682f, -0.094387f, 0.05731531f);
                GlStateManager.rotatef(-11.935f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef((float)i * 65.3f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef((float)i * -9.785f, 0.0f, 0.0f, 1.0f);
                float g = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                float h = g / (float)CrossbowItem.getPullTime(item);
                if (h > 1.0f) {
                    h = 1.0f;
                }
                if (h > 0.1f) {
                    float j = MathHelper.sin((g - 0.1f) * 1.3f);
                    float k = h - 0.1f;
                    float l = j * k;
                    GlStateManager.translatef(l * 0.0f, l * 0.004f, l * 0.0f);
                }
                GlStateManager.translatef(h * 0.0f, h * 0.0f, h * 0.04f);
                GlStateManager.scalef(1.0f, 1.0f, 1.0f + h * 0.2f);
                GlStateManager.rotatef((float)i * 45.0f, 0.0f, -1.0f, 0.0f);
            } else {
                float g = -0.4f * MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
                float h = 0.2f * MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2));
                float j = -0.2f * MathHelper.sin(f * (float)Math.PI);
                GlStateManager.translatef((float)i * g, h, j);
                this.applyHandOffset(arm, equipProgress);
                this.method_3217(arm, f);
                if (bl2 && f < 0.001f) {
                    GlStateManager.translatef((float)i * -0.641864f, 0.0f, 0.0f);
                    GlStateManager.rotatef((float)i * 10.0f, 0.0f, 1.0f, 0.0f);
                }
            }
            this.renderItemFromSide(player, item, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                int m = bl2 ? 1 : -1;
                switch (item.getUseAction()) {
                    case NONE: {
                        this.applyHandOffset(arm, equipProgress);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(tickDelta, arm, item);
                        this.applyHandOffset(arm, equipProgress);
                        break;
                    }
                    case BLOCK: {
                        this.applyHandOffset(arm, equipProgress);
                        break;
                    }
                    case BOW: {
                        this.applyHandOffset(arm, equipProgress);
                        GlStateManager.translatef((float)m * -0.2785682f, 0.18344387f, 0.15731531f);
                        GlStateManager.rotatef(-13.935f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef((float)m * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef((float)m * -9.785f, 0.0f, 0.0f, 1.0f);
                        float n = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float g = n / 20.0f;
                        g = (g * g + g * 2.0f) / 3.0f;
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            float h = MathHelper.sin((n - 0.1f) * 1.3f);
                            float j = g - 0.1f;
                            float k = h * j;
                            GlStateManager.translatef(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        GlStateManager.translatef(g * 0.0f, g * 0.0f, g * 0.04f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + g * 0.2f);
                        GlStateManager.rotatef((float)m * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                    case SPEAR: {
                        this.applyHandOffset(arm, equipProgress);
                        GlStateManager.translatef((float)m * -0.5f, 0.7f, 0.1f);
                        GlStateManager.rotatef(-55.0f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef((float)m * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef((float)m * -9.785f, 0.0f, 0.0f, 1.0f);
                        float n = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float g = n / 10.0f;
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            float h = MathHelper.sin((n - 0.1f) * 1.3f);
                            float j = g - 0.1f;
                            float k = h * j;
                            GlStateManager.translatef(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        GlStateManager.translatef(0.0f, 0.0f, g * 0.2f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + g * 0.2f);
                        GlStateManager.rotatef((float)m * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                }
            } else if (player.isUsingRiptide()) {
                this.applyHandOffset(arm, equipProgress);
                int m = bl2 ? 1 : -1;
                GlStateManager.translatef((float)m * -0.4f, 0.8f, 0.3f);
                GlStateManager.rotatef((float)m * 65.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef((float)m * -85.0f, 0.0f, 0.0f, 1.0f);
            } else {
                float o = -0.4f * MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
                float n = 0.2f * MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2));
                float g = -0.2f * MathHelper.sin(f * (float)Math.PI);
                int p = bl2 ? 1 : -1;
                GlStateManager.translatef((float)p * o, n, g);
                this.applyHandOffset(arm, equipProgress);
                this.method_3217(arm, f);
            }
            this.renderItemFromSide(player, item, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2);
        }
        GlStateManager.popMatrix();
    }

    public void renderOverlays(float f) {
        GlStateManager.disableAlphaTest();
        if (this.client.player.isInsideWall()) {
            BlockState blockState = this.client.world.getBlockState(new BlockPos(this.client.player));
            ClientPlayerEntity playerEntity = this.client.player;
            for (int i = 0; i < 8; ++i) {
                double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double g = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), g);
                BlockState blockState2 = this.client.world.getBlockState(blockPos);
                if (!blockState2.canSuffocate(this.client.world, blockPos)) continue;
                blockState = blockState2;
            }
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                this.renderBlock(this.client.getBlockRenderManager().getModels().getSprite(blockState));
            }
        }
        if (!this.client.player.isSpectator()) {
            if (this.client.player.isInFluid(FluidTags.WATER)) {
                this.renderWaterOverlay(f);
            }
            if (this.client.player.isOnFire()) {
                this.renderFireOverlay();
            }
        }
        GlStateManager.enableAlphaTest();
    }

    private void renderBlock(Sprite sprite) {
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float f = 0.1f;
        GlStateManager.color4f(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        float g = -1.0f;
        float h = 1.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -0.5f;
        float l = sprite.getMinU();
        float m = sprite.getMaxU();
        float n = sprite.getMinV();
        float o = sprite.getMaxV();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-1.0, -1.0, -0.5).texture(m, o).next();
        bufferBuilder.vertex(1.0, -1.0, -0.5).texture(l, o).next();
        bufferBuilder.vertex(1.0, 1.0, -0.5).texture(l, n).next();
        bufferBuilder.vertex(-1.0, 1.0, -0.5).texture(m, n).next();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderWaterOverlay(float f) {
        this.client.getTextureManager().bindTexture(UNDERWATER_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float g = this.client.player.getBrightnessAtEyes();
        GlStateManager.color4f(g, g, g, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        float h = 4.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -1.0f;
        float l = 1.0f;
        float m = -0.5f;
        float n = -this.client.player.yaw / 64.0f;
        float o = this.client.player.pitch / 64.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-1.0, -1.0, -0.5).texture(4.0f + n, 4.0f + o).next();
        bufferBuilder.vertex(1.0, -1.0, -0.5).texture(0.0f + n, 4.0f + o).next();
        bufferBuilder.vertex(1.0, 1.0, -0.5).texture(0.0f + n, 0.0f + o).next();
        bufferBuilder.vertex(-1.0, 1.0, -0.5).texture(4.0f + n, 0.0f + o).next();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
    }

    private void renderFireOverlay() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            Sprite sprite = this.client.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
            this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float g = sprite.getMinU();
            float h = sprite.getMaxU();
            float j = sprite.getMinV();
            float k = sprite.getMaxV();
            float l = -0.5f;
            float m = 0.5f;
            float n = -0.5f;
            float o = 0.5f;
            float p = -0.5f;
            GlStateManager.translatef((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotatef((float)(i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(-0.5, -0.5, -0.5).texture(h, k).next();
            bufferBuilder.vertex(0.5, -0.5, -0.5).texture(g, k).next();
            bufferBuilder.vertex(0.5, 0.5, -0.5).texture(g, j).next();
            bufferBuilder.vertex(-0.5, 0.5, -0.5).texture(h, j).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateHeldItems() {
        this.prevEquipProgressMainHand = this.equipProgressMainHand;
        this.prevEquipProgressOffHand = this.equipProgressOffHand;
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
        if (clientPlayerEntity.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = clientPlayerEntity.getAttackCooldownProgress(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((Objects.equals(this.mainHand, itemStack) ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(Objects.equals(this.offHand, itemStack2) ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = itemStack;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = itemStack2;
        }
    }

    public void resetEquipProgress(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.equipProgressMainHand = 0.0f;
        } else {
            this.equipProgressOffHand = 0.0f;
        }
    }
}

