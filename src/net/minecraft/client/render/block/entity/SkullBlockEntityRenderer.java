/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SkullBlockEntityRenderer
extends BlockEntityRenderer<SkullBlockEntity> {
    public static SkullBlockEntityRenderer INSTANCE;
    private static final Map<SkullBlock.SkullType, SkullEntityModel> MODELS;
    private static final Map<SkullBlock.SkullType, Identifier> TEXTURES;

    @Override
    public void render(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, int i) {
        float h = skullBlockEntity.getTicksPowered(g);
        BlockState blockState = skullBlockEntity.getCachedState();
        boolean bl = blockState.getBlock() instanceof WallSkullBlock;
        Direction direction = bl ? blockState.get(WallSkullBlock.FACING) : null;
        float j = 22.5f * (float)(bl ? (2 + direction.getHorizontal()) * 4 : blockState.get(SkullBlock.ROTATION));
        this.render((float)d, (float)e, (float)f, direction, j, ((AbstractSkullBlock)blockState.getBlock()).getSkullType(), skullBlockEntity.getOwner(), i, h);
    }

    @Override
    public void setRenderManager(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super.setRenderManager(blockEntityRenderDispatcher);
        INSTANCE = this;
    }

    public void render(float f, float g, float h, @Nullable Direction direction, float i, SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile, int j, float k) {
        SkullEntityModel skullEntityModel = MODELS.get(skullType);
        if (j >= 0) {
            this.bindTexture(DESTROY_STAGE_TEXTURES[j]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(this.method_3578(skullType, gameProfile));
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if (direction == null) {
            GlStateManager.translatef(f + 0.5f, g, h + 0.5f);
        } else {
            switch (direction) {
                case NORTH: {
                    GlStateManager.translatef(f + 0.5f, g + 0.25f, h + 0.74f);
                    break;
                }
                case SOUTH: {
                    GlStateManager.translatef(f + 0.5f, g + 0.25f, h + 0.26f);
                    break;
                }
                case WEST: {
                    GlStateManager.translatef(f + 0.74f, g + 0.25f, h + 0.5f);
                    break;
                }
                default: {
                    GlStateManager.translatef(f + 0.26f, g + 0.25f, h + 0.5f);
                }
            }
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        if (skullType == SkullBlock.Type.PLAYER) {
            GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
        }
        skullEntityModel.render(k, 0.0f, 0.0f, i, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (j >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private Identifier method_3578(SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile) {
        Identifier identifier = TEXTURES.get(skullType);
        if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
            identifier = map.containsKey(MinecraftProfileTexture.Type.SKIN) ? minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN) : DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
        }
        return identifier;
    }

    static {
        MODELS = Util.make(Maps.newHashMap(), hashMap -> {
            SkullEntityModel skullEntityModel = new SkullEntityModel(0, 0, 64, 32);
            SkullOverlayEntityModel skullEntityModel2 = new SkullOverlayEntityModel();
            DragonHeadEntityModel dragonHeadEntityModel = new DragonHeadEntityModel(0.0f);
            hashMap.put(SkullBlock.Type.SKELETON, skullEntityModel);
            hashMap.put(SkullBlock.Type.WITHER_SKELETON, skullEntityModel);
            hashMap.put(SkullBlock.Type.PLAYER, skullEntityModel2);
            hashMap.put(SkullBlock.Type.ZOMBIE, skullEntityModel2);
            hashMap.put(SkullBlock.Type.CREEPER, skullEntityModel);
            hashMap.put(SkullBlock.Type.DRAGON, dragonHeadEntityModel);
        });
        TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
            hashMap.put(SkullBlock.Type.SKELETON, new Identifier("textures/entity/skeleton/skeleton.png"));
            hashMap.put(SkullBlock.Type.WITHER_SKELETON, new Identifier("textures/entity/skeleton/wither_skeleton.png"));
            hashMap.put(SkullBlock.Type.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
            hashMap.put(SkullBlock.Type.CREEPER, new Identifier("textures/entity/creeper/creeper.png"));
            hashMap.put(SkullBlock.Type.DRAGON, new Identifier("textures/entity/enderdragon/dragon.png"));
            hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
        });
    }
}

