/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.FrustumWithOrigin;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.client.render.chunk.ChunkRendererList;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;
import net.minecraft.client.render.chunk.DisplayListChunkRendererList;
import net.minecraft.client.render.chunk.VboChunkRendererList;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements AutoCloseable,
SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    private static final Identifier SUN = new Identifier("textures/environment/sun.png");
    private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");
    private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");
    private static final Identifier FORCEFIELD = new Identifier("textures/misc/forcefield.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private ClientWorld world;
    private Set<ChunkRenderer> chunksToRebuild = Sets.newLinkedHashSet();
    private List<ChunkInfo> chunkInfos = Lists.newArrayListWithCapacity((int)69696);
    private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
    private BuiltChunkStorage chunks;
    private int starsDisplayList = -1;
    private int field_4117 = -1;
    private int field_4067 = -1;
    private final VertexFormat field_4100;
    private VertexBuffer starsBuffer;
    private VertexBuffer field_4087;
    private VertexBuffer field_4102;
    private final int field_4079 = 28;
    private boolean cloudsDirty = true;
    private int cloudsDisplayList = -1;
    private VertexBuffer cloudsBuffer;
    private int ticks;
    private final Map<Integer, BlockBreakingInfo> partiallyBrokenBlocks = Maps.newHashMap();
    private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
    private final Sprite[] destroyStages = new Sprite[10];
    private Framebuffer entityOutlinesFramebuffer;
    private ShaderEffect entityOutlineShader;
    private double lastCameraChunkUpdateX = Double.MIN_VALUE;
    private double lastCameraChunkUpdateY = Double.MIN_VALUE;
    private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
    private int cameraChunkX = Integer.MIN_VALUE;
    private int cameraChunkY = Integer.MIN_VALUE;
    private int cameraChunkZ = Integer.MIN_VALUE;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private double lastCameraPitch = Double.MIN_VALUE;
    private double lastCameraYaw = Double.MIN_VALUE;
    private int field_4082 = Integer.MIN_VALUE;
    private int field_4097 = Integer.MIN_VALUE;
    private int field_4116 = Integer.MIN_VALUE;
    private Vec3d field_4072 = Vec3d.ZERO;
    private CloudRenderMode field_4080;
    private ChunkBuilder chunkBuilder;
    private ChunkRendererList chunkRendererList;
    private int renderDistance = -1;
    private int field_4076 = 2;
    private int regularEntityCount;
    private int blockEntityCount;
    private boolean field_4066;
    private Frustum forcedFrustum;
    private final Vector4f[] field_4065 = new Vector4f[8];
    private final Vector3d capturedFrustumPosition = new Vector3d(0.0, 0.0, 0.0);
    private boolean vertexBufferObjectsEnabled;
    private ChunkRendererFactory chunkRendererFactory;
    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean needsTerrainUpdate = true;
    private boolean shouldCaptureFrustum;

    public WorldRenderer(MinecraftClient client) {
        this.client = client;
        this.entityRenderDispatcher = client.getEntityRenderManager();
        this.textureManager = client.getTextureManager();
        this.vertexBufferObjectsEnabled = GLX.useVbo();
        if (this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new VboChunkRendererList();
            this.chunkRendererFactory = ChunkRenderer::new;
        } else {
            this.chunkRendererList = new DisplayListChunkRendererList();
            this.chunkRendererFactory = DisplayListChunkRenderer::new;
        }
        this.field_4100 = new VertexFormat();
        this.field_4100.add(new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3));
        this.renderStars();
        this.method_3277();
        this.method_3265();
    }

    @Override
    public void close() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        this.textureManager.bindTexture(FORCEFIELD);
        GlStateManager.texParameter(3553, 10242, 10497);
        GlStateManager.texParameter(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.loadDestroyStageTextures();
        this.loadEntityOutlineShader();
    }

    private void loadDestroyStageTextures() {
        SpriteAtlasTexture spriteAtlasTexture = this.client.getSpriteAtlas();
        this.destroyStages[0] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_0);
        this.destroyStages[1] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_1);
        this.destroyStages[2] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_2);
        this.destroyStages[3] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_3);
        this.destroyStages[4] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_4);
        this.destroyStages[5] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_5);
        this.destroyStages[6] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_6);
        this.destroyStages[7] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_7);
        this.destroyStages[8] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_8);
        this.destroyStages[9] = spriteAtlasTexture.getSprite(ModelLoader.DESTROY_STAGE_9);
    }

    public void loadEntityOutlineShader() {
        if (GLX.usePostProcess) {
            if (GlProgramManager.getInstance() == null) {
                GlProgramManager.init();
            }
            if (this.entityOutlineShader != null) {
                this.entityOutlineShader.close();
            }
            Identifier identifier = new Identifier("shaders/post/entity_outline.json");
            try {
                this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
                this.entityOutlineShader.setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
                this.entityOutlinesFramebuffer = this.entityOutlineShader.getSecondaryTarget("final");
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)iOException);
                this.entityOutlineShader = null;
                this.entityOutlinesFramebuffer = null;
            }
            catch (JsonSyntaxException jsonSyntaxException) {
                LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)jsonSyntaxException);
                this.entityOutlineShader = null;
                this.entityOutlinesFramebuffer = null;
            }
        } else {
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlinesFramebuffer.drawInternal(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), false);
            GlStateManager.disableBlend();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }

    private void method_3265() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.field_4102 != null) {
            this.field_4102.delete();
        }
        if (this.field_4067 >= 0) {
            GlAllocationUtils.deleteSingletonList(this.field_4067);
            this.field_4067 = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.field_4102 = new VertexBuffer(this.field_4100);
            this.renderSkyHalf(bufferBuilder, -16.0f, true);
            bufferBuilder.end();
            bufferBuilder.clear();
            this.field_4102.set(bufferBuilder.getByteBuffer());
        } else {
            this.field_4067 = GlAllocationUtils.genLists(1);
            GlStateManager.newList(this.field_4067, 4864);
            this.renderSkyHalf(bufferBuilder, -16.0f, true);
            tessellator.draw();
            GlStateManager.endList();
        }
    }

    private void method_3277() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.field_4087 != null) {
            this.field_4087.delete();
        }
        if (this.field_4117 >= 0) {
            GlAllocationUtils.deleteSingletonList(this.field_4117);
            this.field_4117 = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.field_4087 = new VertexBuffer(this.field_4100);
            this.renderSkyHalf(bufferBuilder, 16.0f, false);
            bufferBuilder.end();
            bufferBuilder.clear();
            this.field_4087.set(bufferBuilder.getByteBuffer());
        } else {
            this.field_4117 = GlAllocationUtils.genLists(1);
            GlStateManager.newList(this.field_4117, 4864);
            this.renderSkyHalf(bufferBuilder, 16.0f, false);
            tessellator.draw();
            GlStateManager.endList();
        }
    }

    private void renderSkyHalf(BufferBuilder buffer, float y, boolean bottom) {
        int i = 64;
        int j = 6;
        buffer.begin(7, VertexFormats.POSITION);
        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float f = k;
                float g = k + 64;
                if (bottom) {
                    g = k;
                    f = k + 64;
                }
                buffer.vertex(f, y, l).next();
                buffer.vertex(g, y, l).next();
                buffer.vertex(g, y, l + 64).next();
                buffer.vertex(f, y, l + 64).next();
            }
        }
    }

    private void renderStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.starsBuffer != null) {
            this.starsBuffer.delete();
        }
        if (this.starsDisplayList >= 0) {
            GlAllocationUtils.deleteSingletonList(this.starsDisplayList);
            this.starsDisplayList = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.starsBuffer = new VertexBuffer(this.field_4100);
            this.renderStars(bufferBuilder);
            bufferBuilder.end();
            bufferBuilder.clear();
            this.starsBuffer.set(bufferBuilder.getByteBuffer());
        } else {
            this.starsDisplayList = GlAllocationUtils.genLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.newList(this.starsDisplayList, 4864);
            this.renderStars(bufferBuilder);
            tessellator.draw();
            GlStateManager.endList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(BufferBuilder buffer) {
        Random random = new Random(10842L);
        buffer.begin(7, VertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            double e = random.nextFloat() * 2.0f - 1.0f;
            double f = random.nextFloat() * 2.0f - 1.0f;
            double g = 0.15f + random.nextFloat() * 0.1f;
            double h = d * d + e * e + f * f;
            if (!(h < 1.0) || !(h > 0.01)) continue;
            h = 1.0 / Math.sqrt(h);
            double j = (d *= h) * 100.0;
            double k = (e *= h) * 100.0;
            double l = (f *= h) * 100.0;
            double m = Math.atan2(d, f);
            double n = Math.sin(m);
            double o = Math.cos(m);
            double p = Math.atan2(Math.sqrt(d * d + f * f), e);
            double q = Math.sin(p);
            double r = Math.cos(p);
            double s = random.nextDouble() * Math.PI * 2.0;
            double t = Math.sin(s);
            double u = Math.cos(s);
            for (int v = 0; v < 4; ++v) {
                double ab;
                double w = 0.0;
                double x = (double)((v & 2) - 1) * g;
                double y = (double)((v + 1 & 2) - 1) * g;
                double z = 0.0;
                double aa = x * u - y * t;
                double ac = ab = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ac * o;
                double ag = ad;
                double ah = ac * n + ae * o;
                buffer.vertex(j + af, k + ag, l + ah).next();
            }
        }
    }

    public void setWorld(@Nullable ClientWorld clientWorld) {
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setWorld(clientWorld);
        this.world = clientWorld;
        if (clientWorld != null) {
            this.reload();
        } else {
            this.chunksToRebuild.clear();
            this.chunkInfos.clear();
            if (this.chunks != null) {
                this.chunks.clear();
                this.chunks = null;
            }
            if (this.chunkBuilder != null) {
                this.chunkBuilder.stop();
            }
            this.chunkBuilder = null;
            this.noCullingBlockEntities.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        Entity entity;
        if (this.world == null) {
            return;
        }
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.client.is64Bit());
        }
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
        LeavesBlock.setRenderingMode(this.client.options.fancyGraphics);
        this.renderDistance = this.client.options.viewDistance;
        boolean bl = this.vertexBufferObjectsEnabled;
        this.vertexBufferObjectsEnabled = GLX.useVbo();
        if (bl && !this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new DisplayListChunkRendererList();
            this.chunkRendererFactory = DisplayListChunkRenderer::new;
        } else if (!bl && this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new VboChunkRendererList();
            this.chunkRendererFactory = ChunkRenderer::new;
        }
        if (bl != this.vertexBufferObjectsEnabled) {
            this.renderStars();
            this.method_3277();
            this.method_3265();
        }
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.clearChunkRenderers();
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.clear();
        }
        this.chunks = new BuiltChunkStorage(this.world, this.client.options.viewDistance, this, this.chunkRendererFactory);
        if (this.world != null && (entity = this.client.getCameraEntity()) != null) {
            this.chunks.updateCameraPosition(entity.x, entity.z);
        }
        this.field_4076 = 2;
    }

    protected void clearChunkRenderers() {
        this.chunksToRebuild.clear();
        this.chunkBuilder.reset();
    }

    public void onResized(int i, int j) {
        this.scheduleTerrainUpdate();
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(i, j);
        }
    }

    public void method_21595(Camera camera) {
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, this.client.textRenderer, camera, this.client.targetedEntity, this.client.options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderEntities(Camera camera, VisibleRegion visibleRegion, float tickDelta) {
        if (this.field_4076 > 0) {
            --this.field_4076;
            return;
        }
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        this.world.getProfiler().push("prepare");
        this.regularEntityCount = 0;
        this.blockEntityCount = 0;
        double g = camera.getPos().x;
        double h = camera.getPos().y;
        double i = camera.getPos().z;
        BlockEntityRenderDispatcher.renderOffsetX = g;
        BlockEntityRenderDispatcher.renderOffsetY = h;
        BlockEntityRenderDispatcher.renderOffsetZ = i;
        this.entityRenderDispatcher.setRenderPosition(g, h, i);
        this.client.gameRenderer.enableLightmap();
        this.world.getProfiler().swap("entities");
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (Entity entity : this.world.getEntities()) {
            if (!this.entityRenderDispatcher.shouldRender(entity, visibleRegion, d, e, f) && !entity.hasPassengerDeep(this.client.player) || entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping())) continue;
            ++this.regularEntityCount;
            this.entityRenderDispatcher.render(entity, tickDelta, false);
            if (entity.isGlowing() || entity instanceof PlayerEntity && this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.isPressed()) {
                list.add(entity);
            }
            if (!this.entityRenderDispatcher.hasSecondPass(entity)) continue;
            list2.add(entity);
        }
        if (!list2.isEmpty()) {
            for (Entity entity : list2) {
                this.entityRenderDispatcher.renderSecondPass(entity, tickDelta);
            }
        }
        if (this.canDrawEntityOutlines() && (!list.isEmpty() || this.shouldCaptureFrustum)) {
            this.world.getProfiler().swap("entityOutlines");
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            boolean bl = this.shouldCaptureFrustum = !list.isEmpty();
            if (!list.isEmpty()) {
                GlStateManager.depthFunc(519);
                GlStateManager.disableFog();
                this.entityOutlinesFramebuffer.beginWrite(false);
                DiffuseLighting.disable();
                this.entityRenderDispatcher.setRenderOutlines(true);
                for (int j = 0; j < list.size(); ++j) {
                    this.entityRenderDispatcher.render((Entity)list.get(j), tickDelta, false);
                }
                this.entityRenderDispatcher.setRenderOutlines(false);
                DiffuseLighting.enable();
                GlStateManager.depthMask(false);
                this.entityOutlineShader.render(tickDelta);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(515);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
            }
            this.client.getFramebuffer().beginWrite(false);
        }
        this.world.getProfiler().swap("blockentities");
        DiffuseLighting.enable();
        for (ChunkInfo chunkInfo : this.chunkInfos) {
            List<BlockEntity> list3 = chunkInfo.renderer.getData().getBlockEntities();
            if (list3.isEmpty()) continue;
            for (BlockEntity blockEntity : list3) {
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, tickDelta, -1);
            }
        }
        Set<BlockEntity> j = this.noCullingBlockEntities;
        synchronized (j) {
            for (BlockEntity blockEntity2 : this.noCullingBlockEntities) {
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, tickDelta, -1);
            }
        }
        this.enableBlockOverlayRendering();
        for (BlockBreakingInfo blockBreakingInfo : this.partiallyBrokenBlocks.values()) {
            BlockEntity blockEntity;
            BlockPos blockPos = blockBreakingInfo.getPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            if (!blockState.getBlock().hasBlockEntity()) continue;
            blockEntity = this.world.getBlockEntity(blockPos);
            if (blockEntity instanceof ChestBlockEntity && blockState.get(ChestBlock.CHEST_TYPE) == ChestType.LEFT) {
                blockPos = blockPos.offset(blockState.get(ChestBlock.FACING).rotateYClockwise());
                blockEntity = this.world.getBlockEntity(blockPos);
            }
            if (blockEntity == null || !blockState.hasBlockEntityBreakingRender()) continue;
            BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, tickDelta, blockBreakingInfo.getStage());
        }
        this.disableBlockOverlayRendering();
        this.client.gameRenderer.disableLightmap();
        this.client.getProfiler().pop();
    }

    public String getChunksDebugString() {
        int i = this.chunks.renderers.length;
        int j = this.getCompletedChunkCount();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.field_1730 ? "(s) " : "", this.renderDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    protected int getCompletedChunkCount() {
        int i = 0;
        for (ChunkInfo chunkInfo : this.chunkInfos) {
            ChunkRenderData chunkRenderData = ((ChunkInfo)chunkInfo).renderer.data;
            if (chunkRenderData == ChunkRenderData.EMPTY || chunkRenderData.isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount;
    }

    public void setUpTerrain(Camera camera, VisibleRegion visibleRegion, int i, boolean bl) {
        if (this.client.options.viewDistance != this.renderDistance) {
            this.reload();
        }
        this.world.getProfiler().push("camera");
        double d = this.client.player.x - this.lastCameraChunkUpdateX;
        double e = this.client.player.y - this.lastCameraChunkUpdateY;
        double f = this.client.player.z - this.lastCameraChunkUpdateZ;
        if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || d * d + e * e + f * f > 16.0) {
            this.lastCameraChunkUpdateX = this.client.player.x;
            this.lastCameraChunkUpdateY = this.client.player.y;
            this.lastCameraChunkUpdateZ = this.client.player.z;
            this.cameraChunkX = this.client.player.chunkX;
            this.cameraChunkY = this.client.player.chunkY;
            this.cameraChunkZ = this.client.player.chunkZ;
            this.chunks.updateCameraPosition(this.client.player.x, this.client.player.z);
        }
        this.world.getProfiler().swap("renderlistcamera");
        this.chunkRendererList.setCameraPosition(camera.getPos().x, camera.getPos().y, camera.getPos().z);
        this.chunkBuilder.setCameraPosition(camera.getPos());
        this.world.getProfiler().swap("cull");
        if (this.forcedFrustum != null) {
            FrustumWithOrigin frustumWithOrigin = new FrustumWithOrigin(this.forcedFrustum);
            frustumWithOrigin.setOrigin(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
            visibleRegion = frustumWithOrigin;
        }
        this.client.getProfiler().swap("culling");
        BlockPos blockPos = camera.getBlockPos();
        ChunkRenderer chunkRenderer = this.chunks.getChunkRenderer(blockPos);
        BlockPos blockPos2 = new BlockPos(MathHelper.floor(camera.getPos().x / 16.0) * 16, MathHelper.floor(camera.getPos().y / 16.0) * 16, MathHelper.floor(camera.getPos().z / 16.0) * 16);
        float g = camera.getPitch();
        float h = camera.getYaw();
        this.needsTerrainUpdate = this.needsTerrainUpdate || !this.chunksToRebuild.isEmpty() || camera.getPos().x != this.lastCameraX || camera.getPos().y != this.lastCameraY || camera.getPos().z != this.lastCameraZ || (double)g != this.lastCameraPitch || (double)h != this.lastCameraYaw;
        this.lastCameraX = camera.getPos().x;
        this.lastCameraY = camera.getPos().y;
        this.lastCameraZ = camera.getPos().z;
        this.lastCameraPitch = g;
        this.lastCameraYaw = h;
        boolean bl2 = this.forcedFrustum != null;
        this.client.getProfiler().swap("update");
        if (!bl2 && this.needsTerrainUpdate) {
            this.needsTerrainUpdate = false;
            this.chunkInfos = Lists.newArrayList();
            ArrayDeque queue = Queues.newArrayDeque();
            Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5));
            boolean bl3 = this.client.field_1730;
            if (chunkRenderer == null) {
                int j = blockPos.getY() > 0 ? 248 : 8;
                for (int k = -this.renderDistance; k <= this.renderDistance; ++k) {
                    for (int l = -this.renderDistance; l <= this.renderDistance; ++l) {
                        ChunkRenderer chunkRenderer2 = this.chunks.getChunkRenderer(new BlockPos((k << 4) + 8, j, (l << 4) + 8));
                        if (chunkRenderer2 == null || !visibleRegion.intersects(chunkRenderer2.boundingBox)) continue;
                        chunkRenderer2.method_3671(i);
                        queue.add(new ChunkInfo(chunkRenderer2, null, 0));
                    }
                }
            } else {
                boolean bl4 = false;
                ChunkInfo chunkInfo = new ChunkInfo(chunkRenderer, null, 0);
                Set<Direction> set = this.getOpenChunkFaces(blockPos);
                if (set.size() == 1) {
                    Direction[] vec3d = camera.getHorizontalPlane();
                    Direction direction = Direction.getFacing(vec3d.x, vec3d.y, vec3d.z).getOpposite();
                    set.remove(direction);
                }
                if (set.isEmpty()) {
                    bl4 = true;
                }
                if (!bl4 || bl) {
                    if (bl && this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos)) {
                        bl3 = false;
                    }
                    chunkRenderer.method_3671(i);
                    queue.add(chunkInfo);
                } else {
                    this.chunkInfos.add(chunkInfo);
                }
            }
            this.client.getProfiler().push("iteration");
            while (!queue.isEmpty()) {
                ChunkInfo chunkInfo2 = (ChunkInfo)queue.poll();
                ChunkRenderer chunkRenderer3 = chunkInfo2.renderer;
                Direction direction2 = chunkInfo2.field_4125;
                this.chunkInfos.add(chunkInfo2);
                for (Direction direction3 : DIRECTIONS) {
                    ChunkRenderer chunkRenderer4 = this.getAdjacentChunkRenderer(blockPos2, chunkRenderer3, direction3);
                    if (bl3 && chunkInfo2.method_3298(direction3.getOpposite()) || bl3 && direction2 != null && !chunkRenderer3.getData().isVisibleThrough(direction2.getOpposite(), direction3) || chunkRenderer4 == null || !chunkRenderer4.shouldBuild() || !chunkRenderer4.method_3671(i) || !visibleRegion.intersects(chunkRenderer4.boundingBox)) continue;
                    ChunkInfo chunkInfo3 = new ChunkInfo(chunkRenderer4, direction3, chunkInfo2.field_4122 + 1);
                    chunkInfo3.method_3299(chunkInfo2.field_4126, direction3);
                    queue.add(chunkInfo3);
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.field_4066) {
            this.method_3275(camera.getPos().x, camera.getPos().y, camera.getPos().z);
            this.field_4066 = false;
        }
        this.client.getProfiler().swap("rebuildNear");
        Set<ChunkRenderer> set2 = this.chunksToRebuild;
        this.chunksToRebuild = Sets.newLinkedHashSet();
        for (ChunkInfo chunkInfo2 : this.chunkInfos) {
            boolean bl5;
            ChunkRenderer chunkRenderer3 = chunkInfo2.renderer;
            if (!chunkRenderer3.shouldRebuild() && !set2.contains(chunkRenderer3)) continue;
            this.needsTerrainUpdate = true;
            BlockPos blockPos3 = chunkRenderer3.getOrigin().add(8, 8, 8);
            boolean bl3 = bl5 = blockPos3.getSquaredDistance(blockPos) < 768.0;
            if (chunkRenderer3.shouldRebuildOnClientThread() || bl5) {
                this.client.getProfiler().push("build near");
                this.chunkBuilder.rebuildSync(chunkRenderer3);
                chunkRenderer3.unscheduleRebuild();
                this.client.getProfiler().pop();
                continue;
            }
            this.chunksToRebuild.add(chunkRenderer3);
        }
        this.chunksToRebuild.addAll(set2);
        this.client.getProfiler().pop();
    }

    private Set<Direction> getOpenChunkFaces(BlockPos pos) {
        ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
        BlockPos blockPos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        WorldChunk worldChunk = this.world.getWorldChunk(blockPos);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(15, 15, 15))) {
            if (!worldChunk.getBlockState(blockPos2).isFullOpaque(this.world, blockPos2)) continue;
            chunkOcclusionDataBuilder.markClosed(blockPos2);
        }
        return chunkOcclusionDataBuilder.getOpenFaces(pos);
    }

    @Nullable
    private ChunkRenderer getAdjacentChunkRenderer(BlockPos pos, ChunkRenderer chunkRenderer, Direction direction) {
        BlockPos blockPos = chunkRenderer.getNeighborPosition(direction);
        if (MathHelper.abs(pos.getX() - blockPos.getX()) > this.renderDistance * 16) {
            return null;
        }
        if (blockPos.getY() < 0 || blockPos.getY() >= 256) {
            return null;
        }
        if (MathHelper.abs(pos.getZ() - blockPos.getZ()) > this.renderDistance * 16) {
            return null;
        }
        return this.chunks.getChunkRenderer(blockPos);
    }

    private void method_3275(double d, double e, double f) {
    }

    public int renderLayer(RenderLayer layer, Camera camera) {
        DiffuseLighting.disable();
        if (layer == RenderLayer.TRANSLUCENT) {
            this.client.getProfiler().push("translucent_sort");
            double d = camera.getPos().x - this.lastTranslucentSortX;
            double e = camera.getPos().y - this.lastTranslucentSortY;
            double f = camera.getPos().z - this.lastTranslucentSortZ;
            if (d * d + e * e + f * f > 1.0) {
                this.lastTranslucentSortX = camera.getPos().x;
                this.lastTranslucentSortY = camera.getPos().y;
                this.lastTranslucentSortZ = camera.getPos().z;
                int i = 0;
                for (ChunkInfo chunkInfo : this.chunkInfos) {
                    if (!((ChunkInfo)chunkInfo).renderer.data.isBufferInitialized(layer) || i++ >= 15) continue;
                    this.chunkBuilder.resortTransparency(chunkInfo.renderer);
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        int j = 0;
        boolean bl = layer == RenderLayer.TRANSLUCENT;
        int k = bl ? this.chunkInfos.size() - 1 : 0;
        int l = bl ? -1 : this.chunkInfos.size();
        int m = bl ? -1 : 1;
        for (int n = k; n != l; n += m) {
            ChunkRenderer chunkRenderer = this.chunkInfos.get(n).renderer;
            if (chunkRenderer.getData().isEmpty(layer)) continue;
            ++j;
            this.chunkRendererList.add(chunkRenderer, layer);
        }
        this.client.getProfiler().swap(() -> "render_" + (Object)((Object)layer));
        this.renderLayer(layer);
        this.client.getProfiler().pop();
        return j;
    }

    private void renderLayer(RenderLayer layer) {
        this.client.gameRenderer.enableLightmap();
        if (GLX.useVbo()) {
            GlStateManager.enableClientState(32884);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
        }
        this.chunkRendererList.render(layer);
        if (GLX.useVbo()) {
            List<VertexFormatElement> list = VertexFormats.POSITION_COLOR_UV_LMAP.getElements();
            for (VertexFormatElement vertexFormatElement : list) {
                VertexFormatElement.Type type = vertexFormatElement.getType();
                int i = vertexFormatElement.getIndex();
                switch (type) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        break;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + i);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                    }
                }
            }
        }
        this.client.gameRenderer.disableLightmap();
    }

    private void removeOutdatedPartiallyBrokenBlocks(Iterator<BlockBreakingInfo> partiallyBrokenBlocks) {
        while (partiallyBrokenBlocks.hasNext()) {
            BlockBreakingInfo blockBreakingInfo = partiallyBrokenBlocks.next();
            int i = blockBreakingInfo.getLastUpdateTick();
            if (this.ticks - i <= 400) continue;
            partiallyBrokenBlocks.remove();
        }
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 == 0) {
            this.removeOutdatedPartiallyBrokenBlocks(this.partiallyBrokenBlocks.values().iterator());
        }
    }

    private void renderEndSky() {
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        DiffuseLighting.disable();
        GlStateManager.depthMask(false);
        this.textureManager.bindTexture(END_SKY);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int i = 0; i < 6; ++i) {
            GlStateManager.pushMatrix();
            if (i == 1) {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 2) {
                GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 3) {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 4) {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            }
            if (i == 5) {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            }
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(-100.0, -100.0, 100.0).texture(0.0, 16.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(100.0, -100.0, 100.0).texture(16.0, 16.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(100.0, -100.0, -100.0).texture(16.0, 0.0).color(40, 40, 40, 255).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }

    public void renderSky(float tickDelta) {
        float p;
        float o;
        float n;
        int m;
        int l;
        float j;
        float i;
        if (this.client.world.dimension.getType() == DimensionType.THE_END) {
            this.renderEndSky();
            return;
        }
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        GlStateManager.disableTexture();
        Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getBlockPos(), tickDelta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        GlStateManager.color3f(f, g, h);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color3f(f, g, h);
        if (this.vertexBufferObjectsEnabled) {
            this.field_4087.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.field_4087.draw(7);
            VertexBuffer.unbind();
            GlStateManager.disableClientState(32884);
        } else {
            GlStateManager.callList(this.field_4117);
        }
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        DiffuseLighting.disable();
        float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(tickDelta), tickDelta);
        if (fs != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            i = fs[0];
            j = fs[1];
            float k = fs[2];
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(0.0, 100.0, 0.0).color(i, j, k, fs[3]).next();
            l = 16;
            for (m = 0; m <= 16; ++m) {
                n = (float)m * ((float)Math.PI * 2) / 16.0f;
                o = MathHelper.sin(n);
                p = MathHelper.cos(n);
                bufferBuilder.vertex(o * 120.0f, p * 120.0f, -p * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        i = 1.0f - this.world.getRainGradient(tickDelta);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, i);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(this.world.getSkyAngle(tickDelta) * 360.0f, 1.0f, 0.0f, 0.0f);
        j = 30.0f;
        this.textureManager.bindTexture(SUN);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-j, 100.0, -j).texture(0.0, 0.0).next();
        bufferBuilder.vertex(j, 100.0, -j).texture(1.0, 0.0).next();
        bufferBuilder.vertex(j, 100.0, j).texture(1.0, 1.0).next();
        bufferBuilder.vertex(-j, 100.0, j).texture(0.0, 1.0).next();
        tessellator.draw();
        j = 20.0f;
        this.textureManager.bindTexture(MOON_PHASES);
        int q = this.world.getMoonPhase();
        l = q % 4;
        m = q / 4 % 2;
        n = (float)(l + 0) / 4.0f;
        o = (float)(m + 0) / 2.0f;
        p = (float)(l + 1) / 4.0f;
        float r = (float)(m + 1) / 2.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-j, -100.0, j).texture(p, r).next();
        bufferBuilder.vertex(j, -100.0, j).texture(n, r).next();
        bufferBuilder.vertex(j, -100.0, -j).texture(n, o).next();
        bufferBuilder.vertex(-j, -100.0, -j).texture(p, o).next();
        tessellator.draw();
        GlStateManager.disableTexture();
        float s = this.world.getStarsBrightness(tickDelta) * i;
        if (s > 0.0f) {
            GlStateManager.color4f(s, s, s, s);
            if (this.vertexBufferObjectsEnabled) {
                this.starsBuffer.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.starsBuffer.draw(7);
                VertexBuffer.unbind();
                GlStateManager.disableClientState(32884);
            } else {
                GlStateManager.callList(this.starsDisplayList);
            }
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture();
        GlStateManager.color3f(0.0f, 0.0f, 0.0f);
        double d = this.client.player.getCameraPosVec((float)tickDelta).y - this.world.getHorizonHeight();
        if (d < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 12.0f, 0.0f);
            if (this.vertexBufferObjectsEnabled) {
                this.field_4102.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.field_4102.draw(7);
                VertexBuffer.unbind();
                GlStateManager.disableClientState(32884);
            } else {
                GlStateManager.callList(this.field_4067);
            }
            GlStateManager.popMatrix();
        }
        if (this.world.dimension.method_12449()) {
            GlStateManager.color3f(f * 0.2f + 0.04f, g * 0.2f + 0.04f, h * 0.6f + 0.1f);
        } else {
            GlStateManager.color3f(f, g, h);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -((float)(d - 16.0)), 0.0f);
        GlStateManager.callList(this.field_4067);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }

    public void renderClouds(float tickDelta, double d, double e, double f) {
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        float g = 12.0f;
        float h = 4.0f;
        double i = 2.0E-4;
        double j = ((float)this.ticks + tickDelta) * 0.03f;
        double k = (d + j) / 12.0;
        double l = this.world.dimension.getCloudHeight() - (float)e + 0.33f;
        double m = f / 12.0 + (double)0.33f;
        k -= (double)(MathHelper.floor(k / 2048.0) * 2048);
        m -= (double)(MathHelper.floor(m / 2048.0) * 2048);
        float n = (float)(k - (double)MathHelper.floor(k));
        float o = (float)(l / 4.0 - (double)MathHelper.floor(l / 4.0)) * 4.0f;
        float p = (float)(m - (double)MathHelper.floor(m));
        Vec3d vec3d = this.world.getCloudColor(tickDelta);
        int q = (int)Math.floor(k);
        int r = (int)Math.floor(l / 4.0);
        int s = (int)Math.floor(m);
        if (q != this.field_4082 || r != this.field_4097 || s != this.field_4116 || this.client.options.getCloudRenderMode() != this.field_4080 || this.field_4072.squaredDistanceTo(vec3d) > 2.0E-4) {
            this.field_4082 = q;
            this.field_4097 = r;
            this.field_4116 = s;
            this.field_4072 = vec3d;
            this.field_4080 = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.delete();
            }
            if (this.cloudsDisplayList >= 0) {
                GlAllocationUtils.deleteSingletonList(this.cloudsDisplayList);
                this.cloudsDisplayList = -1;
            }
            if (this.vertexBufferObjectsEnabled) {
                this.cloudsBuffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                this.renderClouds(bufferBuilder, k, l, m, vec3d);
                bufferBuilder.end();
                bufferBuilder.clear();
                this.cloudsBuffer.set(bufferBuilder.getByteBuffer());
            } else {
                this.cloudsDisplayList = GlAllocationUtils.genLists(1);
                GlStateManager.newList(this.cloudsDisplayList, 4864);
                this.renderClouds(bufferBuilder, k, l, m, vec3d);
                tessellator.draw();
                GlStateManager.endList();
            }
        }
        GlStateManager.disableCull();
        this.textureManager.bindTexture(CLOUDS);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(12.0f, 1.0f, 12.0f);
        GlStateManager.translatef(-n, o, -p);
        if (this.vertexBufferObjectsEnabled && this.cloudsBuffer != null) {
            int t;
            this.cloudsBuffer.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
            GlStateManager.enableClientState(32885);
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            for (int u = t = this.field_4080 == CloudRenderMode.FANCY ? 0 : 1; u < 2; ++u) {
                if (u == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                } else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                this.cloudsBuffer.draw(7);
            }
            VertexBuffer.unbind();
            GlStateManager.disableClientState(32884);
            GlStateManager.disableClientState(32888);
            GlStateManager.disableClientState(32886);
            GlStateManager.disableClientState(32885);
        } else if (this.cloudsDisplayList >= 0) {
            int t;
            for (int u = t = this.field_4080 == CloudRenderMode.FANCY ? 0 : 1; u < 2; ++u) {
                if (u == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                } else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                GlStateManager.callList(this.cloudsDisplayList);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.clearCurrentColor();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    private void renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
        float f = 4.0f;
        float g = 0.00390625f;
        int i = 8;
        int j = 4;
        float h = 9.765625E-4f;
        float k = (float)MathHelper.floor(x) * 0.00390625f;
        float l = (float)MathHelper.floor(z) * 0.00390625f;
        float m = (float)color.x;
        float n = (float)color.y;
        float o = (float)color.z;
        float p = m * 0.9f;
        float q = n * 0.9f;
        float r = o * 0.9f;
        float s = m * 0.7f;
        float t = n * 0.7f;
        float u = o * 0.7f;
        float v = m * 0.8f;
        float w = n * 0.8f;
        float aa = o * 0.8f;
        builder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float ab = (float)Math.floor(y / 4.0) * 4.0f;
        if (this.field_4080 == CloudRenderMode.FANCY) {
            for (int ac = -3; ac <= 4; ++ac) {
                for (int ad = -3; ad <= 4; ++ad) {
                    int ag;
                    float ae = ac * 8;
                    float af = ad * 8;
                    if (ab > -5.0f) {
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + 8.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + 8.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    }
                    if (ab <= 5.0f) {
                        builder.vertex(ae + 0.0f, ab + 4.0f - 9.765625E-4f, af + 8.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f - 9.765625E-4f, af + 8.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f - 9.765625E-4f, af + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 0.0f, ab + 4.0f - 9.765625E-4f, af + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                    }
                    if (ac > -1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float)ag + 0.0f, ab + 0.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 4.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 4.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 0.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ac <= 1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 0.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 4.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 4.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 0.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ad > -1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0f, ab + 4.0f, af + (float)ag + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 8.0f, ab + 4.0f, af + (float)ag + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 8.0f, ab + 0.0f, af + (float)ag + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 0.0f, ab + 0.0f, af + (float)ag + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                        }
                    }
                    if (ad > 1) continue;
                    for (ag = 0; ag < 8; ++ag) {
                        builder.vertex(ae + 0.0f, ab + 4.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                    }
                }
            }
        } else {
            boolean ac = true;
            int ad = 32;
            for (int ah = -32; ah < 32; ah += 32) {
                for (int ai = -32; ai < 32; ai += 32) {
                    builder.vertex(ah + 0, ab, ai + 32).texture((float)(ah + 0) * 0.00390625f + k, (float)(ai + 32) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 32, ab, ai + 32).texture((float)(ah + 32) * 0.00390625f + k, (float)(ai + 32) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 32, ab, ai + 0).texture((float)(ah + 32) * 0.00390625f + k, (float)(ai + 0) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 0, ab, ai + 0).texture((float)(ah + 0) * 0.00390625f + k, (float)(ai + 0) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                }
            }
        }
    }

    public void updateChunks(long limitTime) {
        this.needsTerrainUpdate |= this.chunkBuilder.runTasksSync(limitTime);
        if (!this.chunksToRebuild.isEmpty()) {
            ChunkRenderer chunkRenderer;
            boolean bl;
            Iterator<ChunkRenderer> iterator = this.chunksToRebuild.iterator();
            while (iterator.hasNext() && (bl = (chunkRenderer = iterator.next()).shouldRebuildOnClientThread() ? this.chunkBuilder.rebuildSync(chunkRenderer) : this.chunkBuilder.rebuild(chunkRenderer))) {
                chunkRenderer.unscheduleRebuild();
                iterator.remove();
                long l = limitTime - Util.getMeasuringTimeNano();
                if (l >= 0L) continue;
                break;
            }
        }
    }

    public void renderWorldBorder(Camera camera, float delta) {
        float v;
        double u;
        double t;
        float s;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        WorldBorder worldBorder = this.world.getWorldBorder();
        double d = this.client.options.viewDistance * 16;
        if (camera.getPos().x < worldBorder.getBoundEast() - d && camera.getPos().x > worldBorder.getBoundWest() + d && camera.getPos().z < worldBorder.getBoundSouth() - d && camera.getPos().z > worldBorder.getBoundNorth() + d) {
            return;
        }
        double e = 1.0 - worldBorder.getDistanceInsideBorder(camera.getPos().x, camera.getPos().z) / d;
        e = Math.pow(e, 4.0);
        double f = camera.getPos().x;
        double g = camera.getPos().y;
        double h = camera.getPos().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.textureManager.bindTexture(FORCEFIELD);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        int i = worldBorder.getStage().getColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        GlStateManager.color4f(j, k, l, (float)e);
        GlStateManager.polygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableCull();
        float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float n = 0.0f;
        float o = 0.0f;
        float p = 128.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.setOffset(-f, -g, -h);
        double q = Math.max((double)MathHelper.floor(h - d), worldBorder.getBoundNorth());
        double r = Math.min((double)MathHelper.ceil(h + d), worldBorder.getBoundSouth());
        if (f > worldBorder.getBoundEast() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, t).texture(m + s, m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, t + u).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, t + u).texture(m + v + s, m + 128.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, t).texture(m + s, m + 128.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        if (f < worldBorder.getBoundWest() + d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, t).texture(m + s, m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, t + u).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, t + u).texture(m + v + s, m + 128.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, t).texture(m + s, m + 128.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        q = Math.max((double)MathHelper.floor(f - d), worldBorder.getBoundWest());
        r = Math.min((double)MathHelper.ceil(f + d), worldBorder.getBoundEast());
        if (h > worldBorder.getBoundSouth() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(t, 256.0, worldBorder.getBoundSouth()).texture(m + s, m + 0.0f).next();
                bufferBuilder.vertex(t + u, 256.0, worldBorder.getBoundSouth()).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(t + u, 0.0, worldBorder.getBoundSouth()).texture(m + v + s, m + 128.0f).next();
                bufferBuilder.vertex(t, 0.0, worldBorder.getBoundSouth()).texture(m + s, m + 128.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        if (h < worldBorder.getBoundNorth() + d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(t, 256.0, worldBorder.getBoundNorth()).texture(m + s, m + 0.0f).next();
                bufferBuilder.vertex(t + u, 256.0, worldBorder.getBoundNorth()).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(t + u, 0.0, worldBorder.getBoundNorth()).texture(m + v + s, m + 128.0f).next();
                bufferBuilder.vertex(t, 0.0, worldBorder.getBoundNorth()).texture(m + s, m + 128.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        tessellator.draw();
        bufferBuilder.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }

    private void enableBlockOverlayRendering() {
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.polygonOffset(-1.0f, -10.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
    }

    private void disableBlockOverlayRendering() {
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public void renderPartiallyBrokenBlocks(Tessellator tesselator, BufferBuilder builder, Camera camera) {
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        if (!this.partiallyBrokenBlocks.isEmpty()) {
            this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            this.enableBlockOverlayRendering();
            builder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
            builder.setOffset(-d, -e, -f);
            builder.disableColor();
            Iterator<BlockBreakingInfo> iterator = this.partiallyBrokenBlocks.values().iterator();
            while (iterator.hasNext()) {
                double i;
                double h;
                BlockBreakingInfo blockBreakingInfo = iterator.next();
                BlockPos blockPos = blockBreakingInfo.getPos();
                Block block = this.world.getBlockState(blockPos).getBlock();
                if (block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof AbstractSignBlock || block instanceof AbstractSkullBlock) continue;
                double g = (double)blockPos.getX() - d;
                if (g * g + (h = (double)blockPos.getY() - e) * h + (i = (double)blockPos.getZ() - f) * i > 1024.0) {
                    iterator.remove();
                    continue;
                }
                BlockState blockState = this.world.getBlockState(blockPos);
                if (blockState.isAir()) continue;
                int j = blockBreakingInfo.getStage();
                Sprite sprite = this.destroyStages[j];
                BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
                blockRenderManager.tesselateDamage(blockState, blockPos, sprite, this.world);
            }
            tesselator.draw();
            builder.setOffset(0.0, 0.0, 0.0);
            this.disableBlockOverlayRendering();
        }
    }

    public void drawHighlightedBlockOutline(Camera camera, HitResult hit, int renderPass) {
        BlockPos blockPos;
        BlockState blockState;
        if (renderPass == 0 && hit.getType() == HitResult.Type.BLOCK && !(blockState = this.world.getBlockState(blockPos = ((BlockHitResult)hit).getBlockPos())).isAir() && this.world.getWorldBorder().contains(blockPos)) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5f, (float)this.client.window.getFramebufferWidth() / 1920.0f * 2.5f));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, 1.0f, 0.999f);
            double d = camera.getPos().x;
            double e = camera.getPos().y;
            double f = camera.getPos().z;
            WorldRenderer.drawShapeOutline(blockState.getOutlineShape(this.world, blockPos, EntityContext.of(camera.getFocusedEntity())), (double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f, 0.0f, 0.0f, 0.0f, 0.4f);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
        }
    }

    public static void drawDebugShapeOutline(VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
        List<Box> list = shape.getBoundingBoxes();
        int i = MathHelper.ceil((double)list.size() / 3.0);
        for (int j = 0; j < list.size(); ++j) {
            Box box = list.get(j);
            float f = ((float)j % (float)i + 1.0f) / (float)i;
            float g = j / i;
            float h = f * (float)(g == 0.0f ? 1 : 0);
            float k = f * (float)(g == 1.0f ? 1 : 0);
            float l = f * (float)(g == 2.0f ? 1 : 0);
            WorldRenderer.drawShapeOutline(VoxelShapes.cuboid(box.offset(0.0, 0.0, 0.0)), x, y, z, h, k, l, 1.0f);
        }
    }

    public static void drawShapeOutline(VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
        shape.forEachEdge((k, l, m, n, o, p) -> {
            bufferBuilder.vertex(k + x, l + y, m + z).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(n + x, o + y, p + z).color(red, green, blue, alpha).next();
        });
        tessellator.draw();
    }

    public static void drawBoxOutline(Box box, float red, float green, float blue, float alpha) {
        WorldRenderer.drawBoxOutline(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, red, green, blue, alpha);
    }

    public static void drawBoxOutline(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        WorldRenderer.drawBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBox(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        buffer.vertex(x1, y1, z1).color(red, green, blue, 0.0f).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, 0.0f).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, 0.0f).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, 0.0f).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, 0.0f).next();
    }

    public static void buildBox(BufferBuilder builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
    }

    public void updateBlock(BlockView view, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.scheduleSectionRender(pos, (flags & 8) != 0);
    }

    private void scheduleSectionRender(BlockPos pos, boolean important) {
        for (int i = pos.getZ() - 1; i <= pos.getZ() + 1; ++i) {
            for (int j = pos.getX() - 1; j <= pos.getX() + 1; ++j) {
                for (int k = pos.getY() - 1; k <= pos.getY() + 1; ++k) {
                    this.scheduleChunkRender(j >> 4, k >> 4, i >> 4, important);
                }
            }
        }
    }

    public void scheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = minZ - 1; i <= maxZ + 1; ++i) {
            for (int j = minX - 1; j <= maxX + 1; ++j) {
                for (int k = minY - 1; k <= maxY + 1; ++k) {
                    this.scheduleBlockRender(j >> 4, k >> 4, i >> 4);
                }
            }
        }
    }

    public void method_21596(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        if (this.client.getBakedModelManager().method_21611(blockState, blockState2)) {
            this.scheduleBlockRenders(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }

    public void scheduleBlockRenders(int x, int y, int z) {
        for (int i = z - 1; i <= z + 1; ++i) {
            for (int j = x - 1; j <= x + 1; ++j) {
                for (int k = y - 1; k <= y + 1; ++k) {
                    this.scheduleBlockRender(j, k, i);
                }
            }
        }
    }

    public void scheduleBlockRender(int x, int y, int z) {
        this.scheduleChunkRender(x, y, z, false);
    }

    private void scheduleChunkRender(int x, int y, int z, boolean important) {
        this.chunks.scheduleRebuild(x, y, z, important);
    }

    public void playSong(@Nullable SoundEvent song, BlockPos songPosition) {
        SoundInstance soundInstance = this.playingSongs.get(songPosition);
        if (soundInstance != null) {
            this.client.getSoundManager().stop(soundInstance);
            this.playingSongs.remove(songPosition);
        }
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription().asFormattedString());
            }
            soundInstance = PositionedSoundInstance.record(song, songPosition.getX(), songPosition.getY(), songPosition.getZ());
            this.playingSongs.put(songPosition, soundInstance);
            this.client.getSoundManager().play(soundInstance);
        }
        this.updateEntitiesForSong(this.world, songPosition, song != null);
    }

    private void updateEntitiesForSong(World world, BlockPos pos, boolean playing) {
        List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(3.0));
        for (LivingEntity livingEntity : list) {
            livingEntity.setNearbySongPlaying(pos, playing);
        }
    }

    public void addParticle(ParticleEffect parameters, boolean shouldAlwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, shouldAlwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(ParticleEffect parameters, boolean shouldAlwaysSpawn, boolean isImportant, double x, double y, double z, double velocityX, double velocityY, double velocityY2) {
        try {
            this.spawnParticle(parameters, shouldAlwaysSpawn, isImportant, x, y, z, velocityX, velocityY, velocityY2);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
            crashReportSection.add("ID", Registry.PARTICLE_TYPE.getId(parameters.getType()));
            crashReportSection.add("Parameters", parameters.asString());
            crashReportSection.add("Position", () -> CrashReportSection.createPositionString(x, y, z));
            throw new CrashException(crashReport);
        }
    }

    private <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return this.spawnParticle(parameters, alwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Camera camera = this.client.gameRenderer.getCamera();
        if (this.client == null || !camera.isReady() || this.client.particleManager == null) {
            return null;
        }
        ParticlesOption particlesOption = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
        if (alwaysSpawn) {
            return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
        if (camera.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
            return null;
        }
        if (particlesOption == ParticlesOption.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    private ParticlesOption getRandomParticleSpawnChance(boolean canSpawnOnMinimal) {
        ParticlesOption particlesOption = this.client.options.particles;
        if (canSpawnOnMinimal && particlesOption == ParticlesOption.MINIMAL && this.world.random.nextInt(10) == 0) {
            particlesOption = ParticlesOption.DECREASED;
        }
        if (particlesOption == ParticlesOption.DECREASED && this.world.random.nextInt(3) == 0) {
            particlesOption = ParticlesOption.MINIMAL;
        }
        return particlesOption;
    }

    public void method_3267() {
    }

    public void playGlobalEvent(int eventId, BlockPos pos, int i) {
        switch (eventId) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera camera = this.client.gameRenderer.getCamera();
                if (!camera.isReady()) break;
                double d = (double)pos.getX() - camera.getPos().x;
                double e = (double)pos.getY() - camera.getPos().y;
                double f = (double)pos.getZ() - camera.getPos().z;
                double g = Math.sqrt(d * d + e * e + f * f);
                double h = camera.getPos().x;
                double j = camera.getPos().y;
                double k = camera.getPos().z;
                if (g > 0.0) {
                    h += d / g * 2.0;
                    j += e / g * 2.0;
                    k += f / g * 2.0;
                }
                if (eventId == 1023) {
                    this.world.playSound(h, j, k, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (eventId == 1038) {
                    this.world.playSound(h, j, k, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(h, j, k, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void playLevelEvent(PlayerEntity source, int type, BlockPos pos, int data) {
        Random random = this.world.random;
        switch (type) {
            case 1035: {
                this.world.playSound(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSound(pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSound(pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1001: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.world.playSound(pos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction direction = Direction.byId(data);
                int i = direction.getOffsetX();
                int j = direction.getOffsetY();
                int k = direction.getOffsetZ();
                double d = (double)pos.getX() + (double)i * 0.6 + 0.5;
                double e = (double)pos.getY() + (double)j * 0.6 + 0.5;
                double f = (double)pos.getZ() + (double)k * 0.6 + 0.5;
                for (int l = 0; l < 10; ++l) {
                    double g = random.nextDouble() * 0.2 + 0.01;
                    double h = d + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
                    double m = e + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
                    double n = f + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
                    double o = (double)i * g + random.nextGaussian() * 0.01;
                    double p = (double)j * g + random.nextGaussian() * 0.01;
                    double q = (double)k * g + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, h, m, n, o, p, q);
                }
                break;
            }
            case 2003: {
                double r = (double)pos.getX() + 0.5;
                double s = pos.getY();
                double d = (double)pos.getZ() + 0.5;
                for (int t = 0; t < 8; ++t) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), r, s, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                for (double e = 0.0; e < Math.PI * 2; e += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                double r = pos.getX();
                double s = pos.getY();
                double d = pos.getZ();
                for (int t = 0; t < 8; ++t) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), r, s, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                float u = (float)(data >> 16 & 0xFF) / 255.0f;
                float v = (float)(data >> 8 & 0xFF) / 255.0f;
                float w = (float)(data >> 0 & 0xFF) / 255.0f;
                DefaultParticleType particleEffect = type == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int l = 0; l < 100; ++l) {
                    double g = random.nextDouble() * 4.0;
                    double h = random.nextDouble() * Math.PI * 2.0;
                    double m = Math.cos(h) * g;
                    double n = 0.01 + random.nextDouble() * 0.5;
                    double o = Math.sin(h) * g;
                    Particle particle = this.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), r + m * 0.1, s + 0.3, d + o * 0.1, m, n, o);
                    if (particle == null) continue;
                    float x = 0.75f + random.nextFloat() * 0.25f;
                    particle.setColor(u * x, v * x, w * x);
                    particle.move((float)g);
                }
                this.world.playSound(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState blockState = Block.getStateFromRawId(data);
                if (!blockState.isAir()) {
                    BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                    this.world.playSound(pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f, false);
                }
                this.client.particleManager.addBlockBreakParticles(pos, blockState);
                break;
            }
            case 2004: {
                for (int i = 0; i < 20; ++i) {
                    double s = (double)pos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double d = (double)pos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double e = (double)pos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, s, d, e, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, s, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.createParticles(this.world, pos, data);
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, pos, data > 0);
                break;
            }
            case 1501: {
                this.world.playSound(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8f, false);
                for (int i = 0; i < 8; ++i) {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)pos.getX() + Math.random(), (double)pos.getY() + 1.2, (double)pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSound(pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8f, false);
                for (int i = 0; i < 5; ++i) {
                    double s = (double)pos.getX() + random.nextDouble() * 0.6 + 0.2;
                    double d = (double)pos.getY() + random.nextDouble() * 0.6 + 0.2;
                    double e = (double)pos.getZ() + random.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.SMOKE, s, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSound(pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int i = 0; i < 16; ++i) {
                    double s = (float)pos.getX() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double d = (float)pos.getY() + 0.8125f;
                    double e = (float)pos.getZ() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double f = 0.0;
                    double y = 0.0;
                    double z = 0.0;
                    this.world.addParticle(ParticleTypes.SMOKE, s, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int i = 0; i < 200; ++i) {
                    float aa = random.nextFloat() * 4.0f;
                    float ab = random.nextFloat() * ((float)Math.PI * 2);
                    double d = MathHelper.cos(ab) * aa;
                    double e = 0.01 + random.nextDouble() * 0.5;
                    double f = MathHelper.sin(ab) * aa;
                    Particle particle2 = this.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + d * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + f * 0.1, d, e, f);
                    if (particle2 == null) continue;
                    particle2.move(aa);
                }
                this.world.playSound(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.world.playSound(pos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.world.playSound(pos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.world.playSound(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSound(pos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byRawId(data) instanceof MusicDiscItem) {
                    this.playSong(((MusicDiscItem)Item.byRawId(data)).getSound(), pos);
                    break;
                }
                this.playSong(null, pos);
                break;
            }
            case 1015: {
                this.world.playSound(pos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSound(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSound(pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSound(pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSound(pos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSound(pos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSound(pos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSound(pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSound(pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(pos, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
            }
        }
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int stage) {
        if (stage < 0 || stage >= 10) {
            this.partiallyBrokenBlocks.remove(entityId);
        } else {
            BlockBreakingInfo blockBreakingInfo = this.partiallyBrokenBlocks.get(entityId);
            if (blockBreakingInfo == null || blockBreakingInfo.getPos().getX() != pos.getX() || blockBreakingInfo.getPos().getY() != pos.getY() || blockBreakingInfo.getPos().getZ() != pos.getZ()) {
                blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
                this.partiallyBrokenBlocks.put(entityId, blockBreakingInfo);
            }
            blockBreakingInfo.setStage(stage);
            blockBreakingInfo.setLastUpdateTick(this.ticks);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunksToRebuild.isEmpty() && this.chunkBuilder.isEmpty();
    }

    public void scheduleTerrainUpdate() {
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateNoCullingBlockEntities(Collection<BlockEntity> removed, Collection<BlockEntity> added) {
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.removeAll(removed);
            this.noCullingBlockEntities.addAll(added);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ChunkInfo {
        private final ChunkRenderer renderer;
        private final Direction field_4125;
        private byte field_4126;
        private final int field_4122;

        private ChunkInfo(@Nullable ChunkRenderer chunkRenderer, Direction direction, int i) {
            this.renderer = chunkRenderer;
            this.field_4125 = direction;
            this.field_4122 = i;
        }

        public void method_3299(byte b, Direction direction) {
            this.field_4126 = (byte)(this.field_4126 | (b | 1 << direction.ordinal()));
        }

        public boolean method_3298(Direction direction) {
            return (this.field_4126 & 1 << direction.ordinal()) > 0;
        }
    }
}

