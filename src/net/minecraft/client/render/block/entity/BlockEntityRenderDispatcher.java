/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
    private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.newHashMap();
    public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
    private TextRenderer textRenderer;
    public static double renderOffsetX;
    public static double renderOffsetY;
    public static double renderOffsetZ;
    public TextureManager textureManager;
    public World world;
    public Camera camera;
    public HitResult crosshairTarget;

    private BlockEntityRenderDispatcher() {
        this.renderers.put(SignBlockEntity.class, new SignBlockEntityRenderer());
        this.renderers.put(MobSpawnerBlockEntity.class, new MobSpawnerBlockEntityRenderer());
        this.renderers.put(PistonBlockEntity.class, new PistonBlockEntityRenderer());
        this.renderers.put(ChestBlockEntity.class, new ChestBlockEntityRenderer());
        this.renderers.put(EnderChestBlockEntity.class, new ChestBlockEntityRenderer());
        this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableBlockEntityRenderer());
        this.renderers.put(LecternBlockEntity.class, new LecternBlockEntityRenderer());
        this.renderers.put(EndPortalBlockEntity.class, new EndPortalBlockEntityRenderer());
        this.renderers.put(EndGatewayBlockEntity.class, new EndGatewayBlockEntityRenderer());
        this.renderers.put(BeaconBlockEntity.class, new BeaconBlockEntityRenderer());
        this.renderers.put(SkullBlockEntity.class, new SkullBlockEntityRenderer());
        this.renderers.put(BannerBlockEntity.class, new BannerBlockEntityRenderer());
        this.renderers.put(StructureBlockBlockEntity.class, new StructureBlockBlockEntityRenderer());
        this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel()));
        this.renderers.put(BedBlockEntity.class, new BedBlockEntityRenderer());
        this.renderers.put(ConduitBlockEntity.class, new ConduitBlockEntityRenderer());
        this.renderers.put(BellBlockEntity.class, new BellBlockEntityRenderer());
        this.renderers.put(CampfireBlockEntity.class, new CampfireBlockEntityRenderer());
        for (BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer : this.renderers.values()) {
            blockEntityRenderer.setRenderManager(this);
        }
    }

    public <T extends BlockEntity> BlockEntityRenderer<T> get(Class<? extends BlockEntity> class_) {
        BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.renderers.get(class_);
        if (blockEntityRenderer == null && class_ != BlockEntity.class) {
            blockEntityRenderer = this.get(class_.getSuperclass());
            this.renderers.put(class_, blockEntityRenderer);
        }
        return blockEntityRenderer;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityRenderer<T> get(@Nullable BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        return this.get(blockEntity.getClass());
    }

    public void configure(World world, TextureManager textureManager, TextRenderer textRenderer, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }
        this.textureManager = textureManager;
        this.camera = camera;
        this.textRenderer = textRenderer;
        this.crosshairTarget = crosshairTarget;
    }

    public void render(BlockEntity blockEntity, float tickDelta, int blockBreakStage) {
        if (blockEntity.getSquaredDistance(this.camera.getPos().x, this.camera.getPos().y, this.camera.getPos().z) < blockEntity.getSquaredRenderDistance()) {
            DiffuseLighting.enable();
            int i = this.world.getLightmapIndex(blockEntity.getPos(), 0);
            int j = i % 65536;
            int k = i / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, j, k);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            BlockPos blockPos = blockEntity.getPos();
            this.renderEntity(blockEntity, (double)blockPos.getX() - renderOffsetX, (double)blockPos.getY() - renderOffsetY, (double)blockPos.getZ() - renderOffsetZ, tickDelta, blockBreakStage, false);
        }
    }

    public void renderEntity(BlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta) {
        this.renderEntity(blockEntity, xOffset, yOffset, zOffset, tickDelta, -1, false);
    }

    public void renderEntity(BlockEntity blockEntity) {
        this.renderEntity(blockEntity, 0.0, 0.0, 0.0, 0.0f, -1, true);
    }

    public void renderEntity(BlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage, boolean bl) {
        BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer != null) {
            try {
                if (bl || blockEntity.hasWorld() && blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
                    blockEntityRenderer.render(blockEntity, xOffset, yOffset, zOffset, tickDelta, blockBreakStage);
                }
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering Block Entity");
                CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
                blockEntity.populateCrashReport(crashReportSection);
                throw new CrashException(crashReport);
            }
        }
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}
