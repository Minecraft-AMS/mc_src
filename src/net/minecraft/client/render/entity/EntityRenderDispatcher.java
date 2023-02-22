/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EntityRenderDispatcher
implements SynchronousResourceReloader {
    private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(new Identifier("textures/misc/shadow.png"));
    private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
    private Map<String, EntityRenderer<? extends PlayerEntity>> modelRenderers = ImmutableMap.of();
    public final TextureManager textureManager;
    private World world;
    public Camera camera;
    private Quaternion rotation;
    public Entity targetedEntity;
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderManager;
    private final HeldItemRenderer heldItemRenderer;
    private final TextRenderer textRenderer;
    public final GameOptions gameOptions;
    private final EntityModelLoader modelLoader;
    private boolean renderShadows = true;
    private boolean renderHitboxes;

    public <E extends Entity> int getLight(E entity, float tickDelta) {
        return this.getRenderer(entity).getLight(entity, tickDelta);
    }

    public EntityRenderDispatcher(MinecraftClient client, TextureManager textureManager, ItemRenderer itemRenderer, BlockRenderManager blockRenderManager, TextRenderer textRenderer, GameOptions gameOptions, EntityModelLoader modelLoader) {
        this.textureManager = textureManager;
        this.itemRenderer = itemRenderer;
        this.heldItemRenderer = new HeldItemRenderer(client, this, itemRenderer);
        this.blockRenderManager = blockRenderManager;
        this.textRenderer = textRenderer;
        this.gameOptions = gameOptions;
        this.modelLoader = modelLoader;
    }

    public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            String string = ((AbstractClientPlayerEntity)entity).getModel();
            EntityRenderer<? extends PlayerEntity> entityRenderer = this.modelRenderers.get(string);
            if (entityRenderer != null) {
                return entityRenderer;
            }
            return this.modelRenderers.get("default");
        }
        return this.renderers.get(entity.getType());
    }

    public void configure(World world, Camera camera, Entity target) {
        this.world = world;
        this.camera = camera;
        this.rotation = camera.getRotation();
        this.targetedEntity = target;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setRenderHitboxes(boolean renderHitboxes) {
        this.renderHitboxes = renderHitboxes;
    }

    public boolean shouldRenderHitboxes() {
        return this.renderHitboxes;
    }

    public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
        EntityRenderer<E> entityRenderer = this.getRenderer(entity);
        return entityRenderer.shouldRender(entity, frustum, x, y, z);
    }

    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderer<E> entityRenderer = this.getRenderer(entity);
        try {
            double g;
            float h;
            Vec3d vec3d = entityRenderer.getPositionOffset(entity, tickDelta);
            double d = x + vec3d.getX();
            double e = y + vec3d.getY();
            double f = z + vec3d.getZ();
            matrices.push();
            matrices.translate(d, e, f);
            entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            if (entity.doesRenderOnFire()) {
                this.renderFire(matrices, vertexConsumers, entity);
            }
            matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
            if (this.gameOptions.getEntityShadows().getValue().booleanValue() && this.renderShadows && entityRenderer.shadowRadius > 0.0f && !entity.isInvisible() && (h = (float)((1.0 - (g = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ())) / 256.0) * (double)entityRenderer.shadowOpacity)) > 0.0f) {
                EntityRenderDispatcher.renderShadow(matrices, vertexConsumers, entity, h, tickDelta, this.world, entityRenderer.shadowRadius);
            }
            if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                EntityRenderDispatcher.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
            }
            matrices.pop();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
            entity.populateCrashReport(crashReportSection);
            CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
            crashReportSection2.add("Assigned renderer", entityRenderer);
            crashReportSection2.add("Location", CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
            crashReportSection2.add("Rotation", Float.valueOf(yaw));
            crashReportSection2.add("Delta", Float.valueOf(tickDelta));
            throw new CrashException(crashReport);
        }
    }

    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, 1.0f, 1.0f, 1.0f, 1.0f);
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
            double f = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
            for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
                matrices.push();
                double g = d + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
                double h = e + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
                double i = f + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                matrices.translate(g, h, i);
                WorldRenderer.drawBox(matrices, vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), 0.25f, 1.0f, 0.0f, 1.0f);
                matrices.pop();
            }
        }
        if (entity instanceof LivingEntity) {
            float j = 0.01f;
            WorldRenderer.drawBox(matrices, vertices, box.minX, entity.getStandingEyeHeight() - 0.01f, box.minZ, box.maxX, entity.getStandingEyeHeight() + 0.01f, box.maxZ, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        Vec3d vec3d = entity.getRotationVec(tickDelta);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        vertices.vertex(matrix4f, 0.0f, entity.getStandingEyeHeight(), 0.0f).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
        vertices.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
    }

    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        Sprite sprite = ModelLoader.FIRE_0.getSprite();
        Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
        matrices.push();
        float f = entity.getWidth() * 1.4f;
        matrices.scale(f, f, f);
        float g = 0.5f;
        float h = 0.0f;
        float i = entity.getHeight() / f;
        float j = 0.0f;
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
        matrices.translate(0.0, 0.0, -0.3f + (float)((int)i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        MatrixStack.Entry entry = matrices.peek();
        while (i > 0.0f) {
            Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
            float m = sprite3.getMinU();
            float n = sprite3.getMinV();
            float o = sprite3.getMaxU();
            float p = sprite3.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, g - 0.0f, 0.0f - j, k, o, p);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, -g - 0.0f, 0.0f - j, k, m, p);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, -g - 0.0f, 1.4f - j, k, m, n);
            EntityRenderDispatcher.drawFireVertex(entry, vertexConsumer, g - 0.0f, 1.4f - j, k, o, n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k += 0.03f;
            ++l;
        }
        matrices.pop();
    }

    private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry.getPositionMatrix(), x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(0, 10).light(240).normal(entry.getNormalMatrix(), 0.0f, 1.0f, 0.0f).next();
    }

    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
        MobEntity mobEntity;
        float f = radius;
        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).isBaby()) {
            f *= 0.5f;
        }
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double g = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        int i = MathHelper.floor(d - (double)f);
        int j = MathHelper.floor(d + (double)f);
        int k = MathHelper.floor(e - (double)f);
        int l = MathHelper.floor(e);
        int m = MathHelper.floor(g - (double)f);
        int n = MathHelper.floor(g + (double)f);
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);
        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(i, k, m), new BlockPos(j, l, n))) {
            EntityRenderDispatcher.renderShadowPart(entry, vertexConsumer, world, blockPos, d, e, g, f, opacity);
        }
    }

    private static void renderShadowPart(MatrixStack.Entry entry, VertexConsumer vertices, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE || world.getLightLevel(pos) <= 3) {
            return;
        }
        if (!blockState.isFullCube(world, blockPos)) {
            return;
        }
        VoxelShape voxelShape = blockState.getOutlineShape(world, pos.down());
        if (voxelShape.isEmpty()) {
            return;
        }
        float f = LightmapTextureManager.getBrightness(world.getDimension(), world.getLightLevel(pos));
        float g = (float)(((double)opacity - (y - (double)pos.getY()) / 2.0) * 0.5 * (double)f);
        if (g >= 0.0f) {
            if (g > 1.0f) {
                g = 1.0f;
            }
            Box box = voxelShape.getBoundingBox();
            double d = (double)pos.getX() + box.minX;
            double e = (double)pos.getX() + box.maxX;
            double h = (double)pos.getY() + box.minY;
            double i = (double)pos.getZ() + box.minZ;
            double j = (double)pos.getZ() + box.maxZ;
            float k = (float)(d - x);
            float l = (float)(e - x);
            float m = (float)(h - y);
            float n = (float)(i - z);
            float o = (float)(j - z);
            float p = -k / 2.0f / radius + 0.5f;
            float q = -l / 2.0f / radius + 0.5f;
            float r = -n / 2.0f / radius + 0.5f;
            float s = -o / 2.0f / radius + 0.5f;
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, g, k, m, n, p, r);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, g, k, m, o, p, s);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, g, l, m, o, q, s);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, g, l, m, n, q, r);
        }
    }

    private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, float alpha, float x, float y, float z, float u, float v) {
        vertices.vertex(entry.getPositionMatrix(), x, y, z).color(1.0f, 1.0f, 1.0f, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(entry.getNormalMatrix(), 0.0f, 1.0f, 0.0f).next();
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    public double getSquaredDistanceToCamera(Entity entity) {
        return this.camera.getPos().squaredDistanceTo(entity.getPos());
    }

    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }

    public Quaternion getRotation() {
        return this.rotation;
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    @Override
    public void reload(ResourceManager manager) {
        EntityRendererFactory.Context context = new EntityRendererFactory.Context(this, this.itemRenderer, this.blockRenderManager, this.heldItemRenderer, manager, this.modelLoader, this.textRenderer);
        this.renderers = EntityRenderers.reloadEntityRenderers(context);
        this.modelRenderers = EntityRenderers.reloadPlayerRenderers(context);
    }
}

