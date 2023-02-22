/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
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
    private final SkullBlockEntity renderSkull = new SkullBlockEntity();
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
    private final ShieldEntityModel modelShield = new ShieldEntityModel();
    private final TridentEntityModel modelTrident = new TridentEntityModel();

    public void render(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BannerItem) {
            this.renderBanner.readFrom(stack, ((BannerItem)item).getColor());
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner);
        } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BedBlock) {
            this.renderBed.setColor(((BedBlock)((BlockItem)item).getBlock()).getColor());
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed);
        } else if (item == Items.SHIELD) {
            if (stack.getSubTag("BlockEntityTag") != null) {
                this.renderBanner.readFrom(stack, ShieldItem.getColor(stack));
                MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors()));
            } else {
                MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
            }
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.modelShield.renderItem();
            if (stack.hasEnchantmentGlint()) {
                this.renderEnchantmentGlint(this.modelShield::renderItem);
            }
            GlStateManager.popMatrix();
        } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = null;
            if (stack.hasTag()) {
                CompoundTag compoundTag = stack.getTag();
                if (compoundTag.contains("SkullOwner", 10)) {
                    gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)compoundTag.getString("SkullOwner"))) {
                    gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                    gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                    compoundTag.remove("SkullOwner");
                    compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                }
            }
            if (SkullBlockEntityRenderer.INSTANCE != null) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                SkullBlockEntityRenderer.INSTANCE.render(0.0f, 0.0f, 0.0f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, -1, 0.0f);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        } else if (item == Items.TRIDENT) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TridentEntityModel.TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.modelTrident.renderItem();
            if (stack.hasEnchantmentGlint()) {
                this.renderEnchantmentGlint(this.modelTrident::renderItem);
            }
            GlStateManager.popMatrix();
        } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() == Blocks.CONDUIT) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit);
        } else if (item == Blocks.ENDER_CHEST.asItem()) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder);
        } else if (item == Blocks.TRAPPED_CHEST.asItem()) {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped);
        } else if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
            DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
            if (dyeColor == null) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX);
            } else {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX_DYED[dyeColor.getId()]);
            }
        } else {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal);
        }
    }

    private void renderEnchantmentGlint(Runnable runnable) {
        GlStateManager.color3f(0.5019608f, 0.2509804f, 0.8f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(ItemRenderer.ENCHANTMENT_GLINT_TEX);
        ItemRenderer.renderGlint(MinecraftClient.getInstance().getTextureManager(), runnable, 1);
    }
}

