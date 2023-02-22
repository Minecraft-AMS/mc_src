/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private final float field_24474;
    private final float field_24475;
    private final float field_24476;

    public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        this(featureRendererContext, 1.0f, 1.0f, 1.0f);
    }

    public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, float f, float g, float h) {
        super(featureRendererContext);
        this.field_24474 = f;
        this.field_24475 = g;
        this.field_24476 = h;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        float m;
        boolean bl;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        matrixStack.push();
        matrixStack.scale(this.field_24474, this.field_24475, this.field_24476);
        boolean bl2 = bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
        if (((LivingEntity)livingEntity).isBaby() && !(livingEntity instanceof VillagerEntity)) {
            m = 2.0f;
            float n = 1.4f;
            matrixStack.translate(0.0, 0.03125, 0.0);
            matrixStack.scale(0.7f, 0.7f, 0.7f);
            matrixStack.translate(0.0, 1.0, 0.0);
        }
        ((ModelWithHead)this.getContextModel()).getHead().rotate(matrixStack);
        if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            m = 1.1875f;
            matrixStack.scale(1.1875f, -1.1875f, -1.1875f);
            if (bl) {
                matrixStack.translate(0.0, 0.0625, 0.0);
            }
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                String string;
                NbtCompound nbtCompound = itemStack.getTag();
                if (nbtCompound.contains("SkullOwner", 10)) {
                    gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                } else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)(string = nbtCompound.getString("SkullOwner")))) {
                    gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
                    nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
                }
            }
            matrixStack.translate(-0.5, 0.0, -0.5);
            SkullBlockEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, f, matrixStack, vertexConsumerProvider, i);
        } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
            m = 0.625f;
            matrixStack.translate(0.0, -0.25, 0.0);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
            matrixStack.scale(0.625f, -0.625f, -0.625f);
            if (bl) {
                matrixStack.translate(0.0, 0.1875, 0.0);
            }
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
        }
        matrixStack.pop();
    }
}

