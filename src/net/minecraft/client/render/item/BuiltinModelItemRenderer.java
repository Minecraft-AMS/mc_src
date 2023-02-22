/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class BuiltinModelItemRenderer {
    private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray(ShulkerBoxBlockEntity[]::new);
    private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
    public static final BuiltinModelItemRenderer INSTANCE = new BuiltinModelItemRenderer();
    private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
    private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
    private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
    private final BannerBlockEntity renderBanner = new BannerBlockEntity();
    private final BedBlockEntity renderBed = new BedBlockEntity();
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
    private final ShieldEntityModel modelShield = new ShieldEntityModel();
    private final TridentEntityModel modelTrident = new TridentEntityModel();

    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockEntity blockEntity;
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof AbstractSkullBlock) {
                GameProfile gameProfile = null;
                if (stack.hasTag()) {
                    NbtCompound nbtCompound = stack.getTag();
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                    } else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)nbtCompound.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
                        gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                        nbtCompound.remove("SkullOwner");
                        nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
                    }
                }
                SkullBlockEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0f, matrices, vertexConsumers, light);
                return;
            }
            if (block instanceof AbstractBannerBlock) {
                this.renderBanner.readFrom(stack, ((AbstractBannerBlock)block).getColor());
                blockEntity = this.renderBanner;
            } else if (block instanceof BedBlock) {
                this.renderBed.setColor(((BedBlock)block).getColor());
                blockEntity = this.renderBed;
            } else if (block == Blocks.CONDUIT) {
                blockEntity = this.renderConduit;
            } else if (block == Blocks.CHEST) {
                blockEntity = this.renderChestNormal;
            } else if (block == Blocks.ENDER_CHEST) {
                blockEntity = this.renderChestEnder;
            } else if (block == Blocks.TRAPPED_CHEST) {
                blockEntity = this.renderChestTrapped;
            } else if (block instanceof ShulkerBoxBlock) {
                DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
                blockEntity = dyeColor == null ? RENDER_SHULKER_BOX : RENDER_SHULKER_BOX_DYED[dyeColor.getId()];
            } else {
                return;
            }
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
            return;
        }
        if (item == Items.SHIELD) {
            boolean bl = stack.getSubTag("BlockEntityTag") != null;
            matrices.push();
            matrices.scale(1.0f, -1.0f, -1.0f);
            SpriteIdentifier spriteIdentifier = bl ? ModelLoader.SHIELD_BASE : ModelLoader.SHIELD_BASE_NO_PATTERN;
            VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelShield.getLayer(spriteIdentifier.getAtlasId()), true, stack.hasGlint()));
            this.modelShield.getHandle().render(matrices, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            if (bl) {
                List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(ShieldItem.getColor(stack), BannerBlockEntity.getPatternListTag(stack));
                BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, this.modelShield.getPlate(), spriteIdentifier, false, list, stack.hasGlint());
            } else {
                this.modelShield.getPlate().render(matrices, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            matrices.pop();
        } else if (item == Items.TRIDENT) {
            matrices.push();
            matrices.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer vertexConsumer2 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint());
            this.modelTrident.render(matrices, vertexConsumer2, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }
    }
}

