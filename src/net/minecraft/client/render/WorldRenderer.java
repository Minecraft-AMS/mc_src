/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.FpsSmoother;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.TransformingVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
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
import net.minecraft.tag.FluidTags;
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
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
    private static final Identifier RAIN = new Identifier("textures/environment/rain.png");
    private static final Identifier SNOW = new Identifier("textures/environment/snow.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BufferBuilderStorage bufferBuilders;
    private ClientWorld world;
    private Set<ChunkBuilder.BuiltChunk> chunksToRebuild = Sets.newLinkedHashSet();
    private final ObjectList<ChunkInfo> visibleChunks = new ObjectArrayList(69696);
    private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
    private BuiltChunkStorage chunks;
    private final VertexFormat skyVertexFormat = VertexFormats.POSITION;
    @Nullable
    private VertexBuffer starsBuffer;
    @Nullable
    private VertexBuffer lightSkyBuffer;
    @Nullable
    private VertexBuffer darkSkyBuffer;
    private boolean cloudsDirty = true;
    @Nullable
    private VertexBuffer cloudsBuffer;
    private FpsSmoother chunkUpdateSmoother = new FpsSmoother(100);
    private int ticks;
    private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap();
    private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap();
    private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
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
    private int lastCloudsBlockX = Integer.MIN_VALUE;
    private int lastCloudsBlockY = Integer.MIN_VALUE;
    private int lastCloudsBlockZ = Integer.MIN_VALUE;
    private Vec3d lastCloudsColor = Vec3d.ZERO;
    private CloudRenderMode lastCloudsRenderMode;
    private ChunkBuilder chunkBuilder;
    private final VertexFormat vertexFormat = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    private int renderDistance = -1;
    private int regularEntityCount;
    private int blockEntityCount;
    private boolean shouldCaptureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    private final Vector4f[] capturedFrustrumOrientation = new Vector4f[8];
    private final Vector3d capturedFrustumPosition = new Vector3d(0.0, 0.0, 0.0);
    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean needsTerrainUpdate = true;
    private int frame;
    private int field_20793;
    private final float[] field_20794 = new float[1024];
    private final float[] field_20795 = new float[1024];

    public WorldRenderer(MinecraftClient client, BufferBuilderStorage bufferBuilders) {
        this.client = client;
        this.entityRenderDispatcher = client.getEntityRenderManager();
        this.bufferBuilders = bufferBuilders;
        this.textureManager = client.getTextureManager();
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.sqrt(f * f + g * g);
                this.field_20794[i << 5 | j] = -g / h;
                this.field_20795[i << 5 | j] = f / h;
            }
        }
        this.renderStars();
        this.renderLightSky();
        this.renderDarkSky();
    }

    private void renderWeather(LightmapTextureManager manager, float f, double d, double e, double g) {
        float h = this.client.world.getRainGradient(f);
        if (h <= 0.0f) {
            return;
        }
        manager.enable();
        ClientWorld world = this.client.world;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(g);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        int l = 5;
        if (this.client.options.fancyGraphics) {
            l = 10;
        }
        int m = -1;
        float n = (float)this.ticks + f;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int o = k - l; o <= k + l; ++o) {
            for (int p = i - l; p <= i + l; ++p) {
                float ad;
                float z;
                int w;
                int q = (o - k + 16) * 32 + p - i + 16;
                double r = (double)this.field_20794[q] * 0.5;
                double s = (double)this.field_20795[q] * 0.5;
                mutable.set(p, 0, o);
                Biome biome = world.getBiome(mutable);
                if (biome.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int t = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
                int u = j - l;
                int v = j + l;
                if (u < t) {
                    u = t;
                }
                if (v < t) {
                    v = t;
                }
                if ((w = t) < j) {
                    w = j;
                }
                if (u == v) continue;
                Random random = new Random(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761);
                mutable.set(p, u, o);
                float x = biome.getTemperature(mutable);
                if (x >= 0.15f) {
                    if (m != 0) {
                        if (m >= 0) {
                            tessellator.draw();
                        }
                        m = 0;
                        this.client.getTextureManager().bindTexture(RAIN);
                        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    }
                    int y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 0x1F;
                    z = -((float)y + f) / 32.0f * (3.0f + random.nextFloat());
                    double aa = (double)((float)p + 0.5f) - d;
                    double ab = (double)((float)o + 0.5f) - g;
                    float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)l;
                    ad = ((1.0f - ac * ac) * 0.5f + 0.5f) * h;
                    mutable.set(p, w, o);
                    int ae = WorldRenderer.getLightmapCoordinates(world, mutable);
                    bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    continue;
                }
                if (m != 1) {
                    if (m >= 0) {
                        tessellator.draw();
                    }
                    m = 1;
                    this.client.getTextureManager().bindTexture(SNOW);
                    bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                }
                float af = -((float)(this.ticks & 0x1FF) + f) / 512.0f;
                z = (float)(random.nextDouble() + (double)n * 0.01 * (double)((float)random.nextGaussian()));
                float ag = (float)(random.nextDouble() + (double)(n * (float)random.nextGaussian()) * 0.001);
                double ah = (double)((float)p + 0.5f) - d;
                double ai = (double)((float)o + 0.5f) - g;
                ad = MathHelper.sqrt(ah * ah + ai * ai) / (float)l;
                float aj = ((1.0f - ad * ad) * 0.3f + 0.5f) * h;
                mutable.set(p, w, o);
                int ak = WorldRenderer.getLightmapCoordinates(world, mutable);
                int al = ak >> 16 & 0xFFFF;
                int am = (ak & 0xFFFF) * 3;
                int an = (al * 3 + 240) / 4;
                int ao = (am * 3 + 240) / 4;
                bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f + z, (float)u * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f + z, (float)u * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f + z, (float)v * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f + z, (float)v * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
            }
        }
        if (m >= 0) {
            tessellator.draw();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        manager.disable();
    }

    public void method_22713(Camera camera) {
        float f = this.client.world.getRainGradient(1.0f);
        if (!this.client.options.fancyGraphics) {
            f /= 2.0f;
        }
        if (f == 0.0f) {
            return;
        }
        Random random = new Random((long)this.ticks * 312987231L);
        ClientWorld worldView = this.client.world;
        BlockPos blockPos = new BlockPos(camera.getPos());
        int i = 10;
        double d = 0.0;
        double e = 0.0;
        double g = 0.0;
        int j = 0;
        int k = (int)(100.0f * f * f);
        if (this.client.options.particles == ParticlesOption.DECREASED) {
            k >>= 1;
        } else if (this.client.options.particles == ParticlesOption.MINIMAL) {
            k = 0;
        }
        for (int l = 0; l < k; ++l) {
            double q;
            double p;
            double o;
            BlockPos blockPos2 = worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(random.nextInt(10) - random.nextInt(10), 0, random.nextInt(10) - random.nextInt(10)));
            Biome biome = worldView.getBiome(blockPos2);
            BlockPos blockPos3 = blockPos2.down();
            if (blockPos2.getY() > blockPos.getY() + 10 || blockPos2.getY() < blockPos.getY() - 10 || biome.getPrecipitation() != Biome.Precipitation.RAIN || !(biome.getTemperature(blockPos2) >= 0.15f)) continue;
            double h = random.nextDouble();
            double m = random.nextDouble();
            BlockState blockState = worldView.getBlockState(blockPos3);
            FluidState fluidState = worldView.getFluidState(blockPos2);
            VoxelShape voxelShape = blockState.getCollisionShape(worldView, blockPos3);
            double n = voxelShape.getEndingCoord(Direction.Axis.Y, h, m);
            if (n >= (o = (double)fluidState.getHeight(worldView, blockPos2))) {
                p = n;
                q = voxelShape.getBeginningCoord(Direction.Axis.Y, h, m);
            } else {
                p = 0.0;
                q = 0.0;
            }
            if (!(p > -1.7976931348623157E308)) continue;
            if (fluidState.matches(FluidTags.LAVA) || blockState.getBlock() == Blocks.MAGMA_BLOCK || blockState.getBlock() == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT).booleanValue()) {
                this.client.world.addParticle(ParticleTypes.SMOKE, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1f) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0);
                continue;
            }
            if (random.nextInt(++j) == 0) {
                d = (double)blockPos3.getX() + h;
                e = (double)((float)blockPos3.getY() + 0.1f) + p - 1.0;
                g = (double)blockPos3.getZ() + m;
            }
            this.client.world.addParticle(ParticleTypes.RAIN, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1f) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0);
        }
        if (j > 0 && random.nextInt(3) < this.field_20793++) {
            this.field_20793 = 0;
            if (e > (double)(blockPos.getY() + 1) && worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor(blockPos.getY())) {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
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
        RenderSystem.texParameter(3553, 10242, 10497);
        RenderSystem.texParameter(3553, 10243, 10497);
        RenderSystem.bindTexture(0);
        this.loadEntityOutlineShader();
    }

    public void loadEntityOutlineShader() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
        Identifier identifier = new Identifier("shaders/post/entity_outline.json");
        try {
            this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
            this.entityOutlineShader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
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
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            this.entityOutlinesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }

    private void renderDarkSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.darkSkyBuffer != null) {
            this.darkSkyBuffer.close();
        }
        this.darkSkyBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderSkyHalf(bufferBuilder, -16.0f, true);
        bufferBuilder.end();
        this.darkSkyBuffer.upload(bufferBuilder);
    }

    private void renderLightSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.lightSkyBuffer != null) {
            this.lightSkyBuffer.close();
        }
        this.lightSkyBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderSkyHalf(bufferBuilder, 16.0f, false);
        bufferBuilder.end();
        this.lightSkyBuffer.upload(bufferBuilder);
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
            this.starsBuffer.close();
        }
        this.starsBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderStars(bufferBuilder);
        bufferBuilder.end();
        this.starsBuffer.upload(bufferBuilder);
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
            this.visibleChunks.clear();
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
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getServerWorkerExecutor(), this.client.is64Bit(), this.bufferBuilders.getBlockBufferBuilders());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
        RenderLayers.setFancyGraphics(this.client.options.fancyGraphics);
        this.renderDistance = this.client.options.viewDistance;
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.clearChunkRenderers();
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.clear();
        }
        this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.viewDistance, this);
        if (this.world != null && (entity = this.client.getCameraEntity()) != null) {
            this.chunks.updateCameraPosition(entity.getX(), entity.getZ());
        }
    }

    protected void clearChunkRenderers() {
        this.chunksToRebuild.clear();
        this.chunkBuilder.reset();
    }

    public void onResized(int i, int j) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(i, j);
        }
    }

    public String getChunksDebugString() {
        int i = this.chunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.renderDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    protected int getCompletedChunkCount() {
        int i = 0;
        for (ChunkInfo chunkInfo : this.visibleChunks) {
            if (chunkInfo.chunk.getData().isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount;
    }

    private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator) {
        Vec3d vec3d = camera.getPos();
        if (this.client.options.viewDistance != this.renderDistance) {
            this.reload();
        }
        this.world.getProfiler().push("camera");
        double d = this.client.player.getX() - this.lastCameraChunkUpdateX;
        double e = this.client.player.getY() - this.lastCameraChunkUpdateY;
        double f = this.client.player.getZ() - this.lastCameraChunkUpdateZ;
        if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || d * d + e * e + f * f > 16.0) {
            this.lastCameraChunkUpdateX = this.client.player.getX();
            this.lastCameraChunkUpdateY = this.client.player.getY();
            this.lastCameraChunkUpdateZ = this.client.player.getZ();
            this.cameraChunkX = this.client.player.chunkX;
            this.cameraChunkY = this.client.player.chunkY;
            this.cameraChunkZ = this.client.player.chunkZ;
            this.chunks.updateCameraPosition(this.client.player.getX(), this.client.player.getZ());
        }
        this.chunkBuilder.setCameraPosition(vec3d);
        this.world.getProfiler().swap("cull");
        this.client.getProfiler().swap("culling");
        BlockPos blockPos = camera.getBlockPos();
        ChunkBuilder.BuiltChunk builtChunk = this.chunks.getRenderedChunk(blockPos);
        int i = 16;
        BlockPos blockPos2 = new BlockPos(MathHelper.floor(vec3d.x / 16.0) * 16, MathHelper.floor(vec3d.y / 16.0) * 16, MathHelper.floor(vec3d.z / 16.0) * 16);
        float g = camera.getPitch();
        float h = camera.getYaw();
        this.needsTerrainUpdate = this.needsTerrainUpdate || !this.chunksToRebuild.isEmpty() || vec3d.x != this.lastCameraX || vec3d.y != this.lastCameraY || vec3d.z != this.lastCameraZ || (double)g != this.lastCameraPitch || (double)h != this.lastCameraYaw;
        this.lastCameraX = vec3d.x;
        this.lastCameraY = vec3d.y;
        this.lastCameraZ = vec3d.z;
        this.lastCameraPitch = g;
        this.lastCameraYaw = h;
        this.client.getProfiler().swap("update");
        if (!hasForcedFrustum && this.needsTerrainUpdate) {
            this.needsTerrainUpdate = false;
            this.visibleChunks.clear();
            ArrayDeque queue = Queues.newArrayDeque();
            Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5));
            boolean bl = this.client.chunkCullingEnabled;
            if (builtChunk == null) {
                int j = blockPos.getY() > 0 ? 248 : 8;
                int k = MathHelper.floor(vec3d.x / 16.0) * 16;
                int l = MathHelper.floor(vec3d.z / 16.0) * 16;
                ArrayList list = Lists.newArrayList();
                for (int m = -this.renderDistance; m <= this.renderDistance; ++m) {
                    for (int n = -this.renderDistance; n <= this.renderDistance; ++n) {
                        ChunkBuilder.BuiltChunk builtChunk2 = this.chunks.getRenderedChunk(new BlockPos(k + (m << 4) + 8, j, l + (n << 4) + 8));
                        if (builtChunk2 == null || !frustum.isVisible(builtChunk2.boundingBox)) continue;
                        builtChunk2.setRebuildFrame(frame);
                        list.add(new ChunkInfo(builtChunk2, null, 0));
                    }
                }
                list.sort(Comparator.comparingDouble(chunkInfo -> blockPos.getSquaredDistance(((ChunkInfo)chunkInfo).chunk.getOrigin().add(8, 8, 8))));
                queue.addAll(list);
            } else {
                boolean bl2 = false;
                ChunkInfo chunkInfo2 = new ChunkInfo(builtChunk, null, 0);
                Set<Direction> set = this.getOpenChunkFaces(blockPos);
                if (set.size() == 1) {
                    Direction[] vector3f = camera.getHorizontalPlane();
                    Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ()).getOpposite();
                    set.remove(direction);
                }
                if (set.isEmpty()) {
                    bl2 = true;
                }
                if (!bl2 || spectator) {
                    if (spectator && this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos)) {
                        bl = false;
                    }
                    builtChunk.setRebuildFrame(frame);
                    queue.add(chunkInfo2);
                } else {
                    this.visibleChunks.add((Object)chunkInfo2);
                }
            }
            this.client.getProfiler().push("iteration");
            while (!queue.isEmpty()) {
                ChunkInfo chunkInfo2 = (ChunkInfo)queue.poll();
                ChunkBuilder.BuiltChunk builtChunk3 = chunkInfo2.chunk;
                Direction direction2 = chunkInfo2.direction;
                this.visibleChunks.add((Object)chunkInfo2);
                for (Direction direction3 : DIRECTIONS) {
                    ChunkBuilder.BuiltChunk builtChunk4 = this.getAdjacentChunk(blockPos2, builtChunk3, direction3);
                    if (bl && chunkInfo2.canCull(direction3.getOpposite()) || bl && direction2 != null && !builtChunk3.getData().isVisibleThrough(direction2.getOpposite(), direction3) || builtChunk4 == null || !builtChunk4.shouldBuild() || !builtChunk4.setRebuildFrame(frame) || !frustum.isVisible(builtChunk4.boundingBox)) continue;
                    ChunkInfo chunkInfo3 = new ChunkInfo(builtChunk4, direction3, chunkInfo2.propagationLevel + 1);
                    chunkInfo3.updateCullingState(chunkInfo2.cullingState, direction3);
                    queue.add(chunkInfo3);
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().swap("rebuildNear");
        Set<ChunkBuilder.BuiltChunk> set2 = this.chunksToRebuild;
        this.chunksToRebuild = Sets.newLinkedHashSet();
        for (ChunkInfo chunkInfo2 : this.visibleChunks) {
            boolean bl3;
            ChunkBuilder.BuiltChunk builtChunk3 = chunkInfo2.chunk;
            if (!builtChunk3.needsRebuild() && !set2.contains(builtChunk3)) continue;
            this.needsTerrainUpdate = true;
            BlockPos blockPos3 = builtChunk3.getOrigin().add(8, 8, 8);
            boolean bl = bl3 = blockPos3.getSquaredDistance(blockPos) < 768.0;
            if (builtChunk3.needsImportantRebuild() || bl3) {
                this.client.getProfiler().push("build near");
                this.chunkBuilder.rebuild(builtChunk3);
                builtChunk3.cancelRebuild();
                this.client.getProfiler().pop();
                continue;
            }
            this.chunksToRebuild.add(builtChunk3);
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
    private ChunkBuilder.BuiltChunk getAdjacentChunk(BlockPos pos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
        BlockPos blockPos = chunk.getNeighborPosition(direction);
        if (MathHelper.abs(pos.getX() - blockPos.getX()) > this.renderDistance * 16) {
            return null;
        }
        if (blockPos.getY() < 0 || blockPos.getY() >= 256) {
            return null;
        }
        if (MathHelper.abs(pos.getZ() - blockPos.getZ()) > this.renderDistance * 16) {
            return null;
        }
        return this.chunks.getRenderedChunk(blockPos);
    }

    private void captureFrustum(Matrix4f modelMatrix, Matrix4f matrix4f, double x, double y, double z, Frustum frustum) {
        this.capturedFrustum = frustum;
        Matrix4f matrix4f2 = matrix4f.copy();
        matrix4f2.multiply(modelMatrix);
        matrix4f2.invert();
        this.capturedFrustumPosition.x = x;
        this.capturedFrustumPosition.y = y;
        this.capturedFrustumPosition.z = z;
        this.capturedFrustrumOrientation[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            this.capturedFrustrumOrientation[i].transform(matrix4f2);
            this.capturedFrustrumOrientation[i].normalizeProjectiveCoordinates();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f) {
        int u;
        boolean bl2;
        Frustum frustum;
        boolean bl;
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        Profiler profiler = this.world.getProfiler();
        profiler.swap("light_updates");
        this.client.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        Matrix4f matrix4f2 = matrices.peek().getModel();
        profiler.swap("culling");
        boolean bl3 = bl = this.capturedFrustum != null;
        if (bl) {
            frustum = this.capturedFrustum;
            frustum.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
        } else {
            frustum = new Frustum(matrix4f2, matrix4f);
            frustum.setPosition(d, e, f);
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.captureFrustum(matrix4f2, matrix4f, vec3d.x, vec3d.y, vec3d.z, bl ? new Frustum(matrix4f2, matrix4f) : frustum);
            this.shouldCaptureFrustum = false;
        }
        profiler.swap("clear");
        BackgroundRenderer.render(camera, tickDelta, this.client.world, this.client.options.viewDistance, gameRenderer.getSkyDarkness(tickDelta));
        RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        float g = gameRenderer.getViewDistance();
        boolean bl4 = bl2 = this.client.world.dimension.isFogThick(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        if (this.client.options.viewDistance >= 4) {
            BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, g, bl2);
            profiler.swap("sky");
            this.renderSky(matrices, tickDelta);
        }
        profiler.swap("fog");
        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g - 16.0f, 32.0f), bl2);
        profiler.swap("terrain_setup");
        this.setupTerrain(camera, frustum, bl, this.frame++, this.client.player.isSpectator());
        profiler.swap("updatechunks");
        int i = 30;
        int j = this.client.options.maxFps;
        long l = 33333333L;
        long m = (double)j == Option.FRAMERATE_LIMIT.getMax() ? 0L : (long)(1000000000 / j);
        long n = Util.getMeasuringTimeNano() - limitTime;
        long o = this.chunkUpdateSmoother.getTargetUsedTime(n);
        long p = o * 3L / 2L;
        long q = MathHelper.clamp(p, m, 33333333L);
        this.updateChunks(limitTime + q);
        profiler.swap("terrain");
        this.renderLayer(RenderLayer.getSolid(), matrices, d, e, f);
        this.renderLayer(RenderLayer.getCutoutMipped(), matrices, d, e, f);
        this.renderLayer(RenderLayer.getCutout(), matrices, d, e, f);
        DiffuseLighting.enableForLevel(matrices.peek().getModel());
        profiler.swap("entities");
        profiler.push("prepare");
        this.regularEntityCount = 0;
        this.blockEntityCount = 0;
        profiler.swap("entities");
        if (this.canDrawEntityOutlines()) {
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.client.getFramebuffer().beginWrite(false);
        }
        boolean bl32 = false;
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        for (Entity entity : this.world.getEntities()) {
            VertexConsumerProvider vertexConsumerProvider;
            if (!this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, f) && !entity.hasPassengerDeep(this.client.player) || entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()) || entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity) continue;
            ++this.regularEntityCount;
            if (entity.age == 0) {
                entity.lastRenderX = entity.getX();
                entity.lastRenderY = entity.getY();
                entity.lastRenderZ = entity.getZ();
            }
            if (this.canDrawEntityOutlines() && entity.isGlowing()) {
                bl32 = true;
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                vertexConsumerProvider = outlineVertexConsumerProvider;
                int k = entity.getTeamColorValue();
                int r = 255;
                int s = k >> 16 & 0xFF;
                int t = k >> 8 & 0xFF;
                u = k & 0xFF;
                outlineVertexConsumerProvider.setColor(s, t, u, 255);
            } else {
                vertexConsumerProvider = immediate;
            }
            this.renderEntity(entity, d, e, f, tickDelta, matrices, vertexConsumerProvider);
        }
        this.checkEmpty(matrices);
        immediate.draw(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        immediate.draw(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        immediate.draw(RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        immediate.draw(RenderLayer.getEntitySmoothCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        profiler.swap("blockentities");
        for (Object chunkInfo : this.visibleChunks) {
            List<BlockEntity> list = ((ChunkInfo)chunkInfo).chunk.getData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity blockEntity : list) {
                BlockPos blockPos = blockEntity.getPos();
                VertexConsumerProvider vertexConsumerProvider2 = immediate;
                matrices.push();
                matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(blockPos.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (u = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    TransformingVertexConsumer vertexConsumer = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(u)), matrices.peek());
                    vertexConsumerProvider2 = renderLayer -> {
                        VertexConsumer vertexConsumer2 = immediate.getBuffer(renderLayer);
                        if (renderLayer.method_23037()) {
                            return VertexConsumers.dual(vertexConsumer, vertexConsumer2);
                        }
                        return vertexConsumer2;
                    };
                }
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, tickDelta, matrices, vertexConsumerProvider2);
                matrices.pop();
            }
        }
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            for (BlockEntity blockEntity2 : this.noCullingBlockEntities) {
                BlockPos blockPos2 = blockEntity2.getPos();
                matrices.push();
                matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, tickDelta, matrices, immediate);
                matrices.pop();
            }
        }
        this.checkEmpty(matrices);
        immediate.draw(RenderLayer.getSolid());
        immediate.draw(TexturedRenderLayers.getEntitySolid());
        immediate.draw(TexturedRenderLayers.getEntityCutout());
        immediate.draw(TexturedRenderLayers.getBeds());
        immediate.draw(TexturedRenderLayers.getShulkerBoxes());
        immediate.draw(TexturedRenderLayers.getSign());
        immediate.draw(TexturedRenderLayers.getChest());
        this.bufferBuilders.getOutlineVertexConsumers().draw();
        if (bl32) {
            this.entityOutlineShader.render(tickDelta);
            this.client.getFramebuffer().beginWrite(false);
        }
        profiler.swap("destroyProgress");
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet2;
            double w;
            double v;
            BlockPos blockPos3 = BlockPos.fromLong(entry.getLongKey());
            double h = (double)blockPos3.getX() - d;
            if (h * h + (v = (double)blockPos3.getY() - e) * v + (w = (double)blockPos3.getZ() - f) * w > 1024.0 || (sortedSet2 = (SortedSet)entry.getValue()) == null || sortedSet2.isEmpty()) continue;
            int x = ((BlockBreakingInfo)sortedSet2.last()).getStage();
            matrices.push();
            matrices.translate((double)blockPos3.getX() - d, (double)blockPos3.getY() - e, (double)blockPos3.getZ() - f);
            TransformingVertexConsumer vertexConsumer2 = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(x)), matrices.peek());
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos3), blockPos3, this.world, matrices, vertexConsumer2);
            matrices.pop();
        }
        this.checkEmpty(matrices);
        profiler.pop();
        HitResult hitResult = this.client.crosshairTarget;
        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            profiler.swap("outline");
            BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos4);
            if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos4)) {
                VertexConsumer vertexConsumer3 = immediate.getBuffer(RenderLayer.getLines());
                this.drawBlockOutline(matrices, vertexConsumer3, camera.getFocusedEntity(), d, e, f, blockPos4, blockState);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrices.peek().getModel());
        this.client.debugRenderer.render(matrices, immediate, d, e, f);
        this.renderWorldBorder(camera);
        RenderSystem.popMatrix();
        immediate.draw(TexturedRenderLayers.getEntityTranslucent());
        immediate.draw(TexturedRenderLayers.getBannerPatterns());
        immediate.draw(TexturedRenderLayers.getShieldPatterns());
        immediate.draw(RenderLayer.getGlint());
        immediate.draw(RenderLayer.getEntityGlint());
        immediate.draw(RenderLayer.getWaterMask());
        this.bufferBuilders.getEffectVertexConsumers().draw();
        immediate.draw(RenderLayer.getLines());
        immediate.draw();
        profiler.swap("translucent");
        this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f);
        profiler.swap("particles");
        this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrices.peek().getModel());
        profiler.swap("cloudsLayers");
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
            profiler.swap("clouds");
            this.renderClouds(matrices, tickDelta, d, e, f);
        }
        RenderSystem.depthMask(false);
        profiler.swap("weather");
        this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
        RenderSystem.depthMask(true);
        this.renderChunkDebugInfo(camera);
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        BackgroundRenderer.method_23792();
    }

    private void checkEmpty(MatrixStack matrix) {
        if (!matrix.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers) {
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        float g = MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw);
        this.entityRenderDispatcher.render(entity, d - cameraX, e - cameraY, f - cameraZ, g, tickDelta, matrix, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
    }

    private void renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f) {
        renderLayer.startDrawing();
        if (renderLayer == RenderLayer.getTranslucent()) {
            this.client.getProfiler().push("translucent_sort");
            double g = d - this.lastTranslucentSortX;
            double h = e - this.lastTranslucentSortY;
            double i = f - this.lastTranslucentSortZ;
            if (g * g + h * h + i * i > 1.0) {
                this.lastTranslucentSortX = d;
                this.lastTranslucentSortY = e;
                this.lastTranslucentSortZ = f;
                int j = 0;
                for (ChunkInfo chunkInfo : this.visibleChunks) {
                    if (j >= 15 || !chunkInfo.chunk.scheduleSort(renderLayer, this.chunkBuilder)) continue;
                    ++j;
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        this.client.getProfiler().swap(() -> "render_" + renderLayer);
        boolean bl = renderLayer != RenderLayer.getTranslucent();
        ObjectListIterator objectListIterator = this.visibleChunks.listIterator(bl ? 0 : this.visibleChunks.size());
        while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            ChunkInfo chunkInfo2 = bl ? (ChunkInfo)objectListIterator.next() : (ChunkInfo)objectListIterator.previous();
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo2.chunk;
            if (builtChunk.getData().isEmpty(renderLayer)) continue;
            VertexBuffer vertexBuffer = builtChunk.getBuffer(renderLayer);
            matrixStack.push();
            BlockPos blockPos = builtChunk.getOrigin();
            matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
            vertexBuffer.bind();
            this.vertexFormat.startDrawing(0L);
            vertexBuffer.draw(matrixStack.peek().getModel(), 7);
            matrixStack.pop();
        }
        VertexBuffer.unbind();
        RenderSystem.clearCurrentColor();
        this.vertexFormat.endDrawing();
        this.client.getProfiler().pop();
        renderLayer.endDrawing();
    }

    private void renderChunkDebugInfo(Camera camera) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.client.debugChunkInfo || this.client.debugChunkOcculsion) {
            double d = camera.getPos().getX();
            double e = camera.getPos().getY();
            double f = camera.getPos().getZ();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (ChunkInfo chunkInfo : this.visibleChunks) {
                int i;
                ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
                RenderSystem.pushMatrix();
                BlockPos blockPos = builtChunk.getOrigin();
                RenderSystem.translated((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                if (this.client.debugChunkInfo) {
                    bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    Direction direction = chunkInfo.direction;
                    if (direction != null) {
                        bufferBuilder.vertex(8.0, 8.0, 8.0).color(j, k, l, 255).next();
                        bufferBuilder.vertex(8 - 16 * direction.getOffsetX(), 8 - 16 * direction.getOffsetY(), 8 - 16 * direction.getOffsetZ()).color(j, k, l, 255).next();
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.client.debugChunkOcculsion && !builtChunk.getData().isEmpty()) {
                    bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    i = 0;
                    for (Direction direction : Direction.values()) {
                        for (Direction direction2 : Direction.values()) {
                            boolean bl = builtChunk.getData().isVisibleThrough(direction, direction2);
                            if (bl) continue;
                            ++i;
                            bufferBuilder.vertex(8 + 8 * direction.getOffsetX(), 8 + 8 * direction.getOffsetY(), 8 + 8 * direction.getOffsetZ()).color(1, 0, 0, 1).next();
                            bufferBuilder.vertex(8 + 8 * direction2.getOffsetX(), 8 + 8 * direction2.getOffsetY(), 8 + 8 * direction2.getOffsetZ()).color(1, 0, 0, 1).next();
                        }
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                    if (i > 0) {
                        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                        float g = 0.5f;
                        float h = 0.2f;
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        tessellator.draw();
                    }
                }
                RenderSystem.popMatrix();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }
        if (this.capturedFrustum != null) {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(10.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.capturedFrustumPosition.x - camera.getPos().x), (float)(this.capturedFrustumPosition.y - camera.getPos().y), (float)(this.capturedFrustumPosition.z - camera.getPos().z));
            RenderSystem.depthMask(true);
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            this.method_22985(bufferBuilder, 0, 1, 2, 3, 0, 1, 1);
            this.method_22985(bufferBuilder, 4, 5, 6, 7, 1, 0, 0);
            this.method_22985(bufferBuilder, 0, 1, 5, 4, 1, 1, 0);
            this.method_22985(bufferBuilder, 2, 3, 7, 6, 0, 0, 1);
            this.method_22985(bufferBuilder, 0, 4, 7, 3, 0, 1, 0);
            this.method_22985(bufferBuilder, 1, 5, 6, 2, 1, 0, 1);
            tessellator.draw();
            RenderSystem.depthMask(false);
            bufferBuilder.begin(1, VertexFormats.POSITION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.method_22984(bufferBuilder, 0);
            this.method_22984(bufferBuilder, 1);
            this.method_22984(bufferBuilder, 1);
            this.method_22984(bufferBuilder, 2);
            this.method_22984(bufferBuilder, 2);
            this.method_22984(bufferBuilder, 3);
            this.method_22984(bufferBuilder, 3);
            this.method_22984(bufferBuilder, 0);
            this.method_22984(bufferBuilder, 4);
            this.method_22984(bufferBuilder, 5);
            this.method_22984(bufferBuilder, 5);
            this.method_22984(bufferBuilder, 6);
            this.method_22984(bufferBuilder, 6);
            this.method_22984(bufferBuilder, 7);
            this.method_22984(bufferBuilder, 7);
            this.method_22984(bufferBuilder, 4);
            this.method_22984(bufferBuilder, 0);
            this.method_22984(bufferBuilder, 4);
            this.method_22984(bufferBuilder, 1);
            this.method_22984(bufferBuilder, 5);
            this.method_22984(bufferBuilder, 2);
            this.method_22984(bufferBuilder, 6);
            this.method_22984(bufferBuilder, 3);
            this.method_22984(bufferBuilder, 7);
            tessellator.draw();
            RenderSystem.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void method_22984(VertexConsumer vertexConsumer, int i) {
        vertexConsumer.vertex(this.capturedFrustrumOrientation[i].getX(), this.capturedFrustrumOrientation[i].getY(), this.capturedFrustrumOrientation[i].getZ()).next();
    }

    private void method_22985(VertexConsumer vertexConsumer, int i, int j, int k, int l, int m, int n, int o) {
        float f = 0.25f;
        vertexConsumer.vertex(this.capturedFrustrumOrientation[i].getX(), this.capturedFrustrumOrientation[i].getY(), this.capturedFrustrumOrientation[i].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustrumOrientation[j].getX(), this.capturedFrustrumOrientation[j].getY(), this.capturedFrustrumOrientation[j].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustrumOrientation[k].getX(), this.capturedFrustrumOrientation[k].getY(), this.capturedFrustrumOrientation[k].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustrumOrientation[l].getX(), this.capturedFrustrumOrientation[l].getY(), this.capturedFrustrumOrientation[l].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator iterator = this.blockBreakingInfos.values().iterator();
        while (iterator.hasNext()) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
            int i = blockBreakingInfo.getLastUpdateTick();
            if (this.ticks - i <= 400) continue;
            iterator.remove();
            this.removeBlockBreakingInfo(blockBreakingInfo);
        }
    }

    private void removeBlockBreakingInfo(BlockBreakingInfo blockBreakingInfo) {
        long l = blockBreakingInfo.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(blockBreakingInfo);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderEndSky(MatrixStack matrixStack) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        this.textureManager.bindTexture(END_SKY);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int i = 0; i < 6; ++i) {
            matrixStack.push();
            if (i == 1) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            }
            if (i == 2) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            }
            if (i == 3) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
            }
            if (i == 4) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            }
            if (i == 5) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0f));
            }
            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrixStack.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void renderSky(MatrixStack matrixStack, float f) {
        float r;
        float q;
        float p;
        int n;
        float l;
        float j;
        if (this.client.world.dimension.getType() == DimensionType.THE_END) {
            this.renderEndSky(matrixStack);
            return;
        }
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3d vec3d = this.world.method_23777(this.client.gameRenderer.getCamera().getBlockPos(), f);
        float g = (float)vec3d.x;
        float h = (float)vec3d.y;
        float i = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(g, h, i);
        this.lightSkyBuffer.bind();
        this.skyVertexFormat.startDrawing(0L);
        this.lightSkyBuffer.draw(matrixStack.peek().getModel(), 7);
        VertexBuffer.unbind();
        this.skyVertexFormat.endDrawing();
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(f), f);
        if (fs != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            j = MathHelper.sin(this.world.getSkyAngleRadians(f)) < 0.0f ? 180.0f : 0.0f;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(j));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            float k = fs[0];
            l = fs[1];
            float m = fs[2];
            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(k, l, m, fs[3]).next();
            n = 16;
            for (int o = 0; o <= 16; ++o) {
                p = (float)o * ((float)Math.PI * 2) / 16.0f;
                q = MathHelper.sin(p);
                r = MathHelper.cos(p);
                bufferBuilder.vertex(matrix4f, q * 120.0f, r * 120.0f, -r * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrixStack.pop();
            RenderSystem.shadeModel(7424);
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrixStack.push();
        j = 1.0f - this.world.getRainGradient(f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, j);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(f) * 360.0f));
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        l = 30.0f;
        this.textureManager.bindTexture(SUN);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -l, 100.0f, -l).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, l, 100.0f, -l).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, l, 100.0f, l).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -l, 100.0f, l).texture(0.0f, 1.0f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        l = 20.0f;
        this.textureManager.bindTexture(MOON_PHASES);
        int s = this.world.getMoonPhase();
        int t = s % 4;
        n = s / 4 % 2;
        float u = (float)(t + 0) / 4.0f;
        p = (float)(n + 0) / 2.0f;
        q = (float)(t + 1) / 4.0f;
        r = (float)(n + 1) / 2.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -l, -100.0f, l).texture(q, r).next();
        bufferBuilder.vertex(matrix4f2, l, -100.0f, l).texture(u, r).next();
        bufferBuilder.vertex(matrix4f2, l, -100.0f, -l).texture(u, p).next();
        bufferBuilder.vertex(matrix4f2, -l, -100.0f, -l).texture(q, p).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableTexture();
        float v = this.world.method_23787(f) * j;
        if (v > 0.0f) {
            RenderSystem.color4f(v, v, v, v);
            this.starsBuffer.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.starsBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStack.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0f, 0.0f, 0.0f);
        double d = this.client.player.getCameraPosVec((float)f).y - this.world.getSkyDarknessHeight();
        if (d < 0.0) {
            matrixStack.push();
            matrixStack.translate(0.0, 12.0, 0.0);
            this.darkSkyBuffer.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.darkSkyBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();
            matrixStack.pop();
        }
        if (this.world.dimension.hasGround()) {
            RenderSystem.color3f(g * 0.2f + 0.04f, h * 0.2f + 0.04f, i * 0.6f + 0.1f);
        } else {
            RenderSystem.color3f(g, h, i);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    public void renderClouds(MatrixStack matrices, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        float f = 12.0f;
        float g = 4.0f;
        double d = 2.0E-4;
        double e = ((float)this.ticks + tickDelta) * 0.03f;
        double h = (cameraX + e) / 12.0;
        double i = this.world.dimension.getCloudHeight() - (float)cameraY + 0.33f;
        double j = cameraZ / 12.0 + (double)0.33f;
        h -= (double)(MathHelper.floor(h / 2048.0) * 2048);
        j -= (double)(MathHelper.floor(j / 2048.0) * 2048);
        float k = (float)(h - (double)MathHelper.floor(h));
        float l = (float)(i / 4.0 - (double)MathHelper.floor(i / 4.0)) * 4.0f;
        float m = (float)(j - (double)MathHelper.floor(j));
        Vec3d vec3d = this.world.getCloudsColor(tickDelta);
        int n = (int)Math.floor(h);
        int o = (int)Math.floor(i / 4.0);
        int p = (int)Math.floor(j);
        if (n != this.lastCloudsBlockX || o != this.lastCloudsBlockY || p != this.lastCloudsBlockZ || this.client.options.getCloudRenderMode() != this.lastCloudsRenderMode || this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4) {
            this.lastCloudsBlockX = n;
            this.lastCloudsBlockY = o;
            this.lastCloudsBlockZ = p;
            this.lastCloudsColor = vec3d;
            this.lastCloudsRenderMode = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.close();
            }
            this.cloudsBuffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            this.renderClouds(bufferBuilder, h, i, j, vec3d);
            bufferBuilder.end();
            this.cloudsBuffer.upload(bufferBuilder);
        }
        this.textureManager.bindTexture(CLOUDS);
        matrices.push();
        matrices.scale(12.0f, 1.0f, 12.0f);
        matrices.translate(-k, l, -m);
        if (this.cloudsBuffer != null) {
            int q;
            this.cloudsBuffer.bind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.startDrawing(0L);
            for (int r = q = this.lastCloudsRenderMode == CloudRenderMode.FANCY ? 0 : 1; r < 2; ++r) {
                if (r == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                this.cloudsBuffer.draw(matrices.peek().getModel(), 7);
            }
            VertexBuffer.unbind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.endDrawing();
        }
        matrices.pop();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableFog();
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
        if (this.lastCloudsRenderMode == CloudRenderMode.FANCY) {
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

    private void updateChunks(long limitTime) {
        this.needsTerrainUpdate |= this.chunkBuilder.upload();
        long l = Util.getMeasuringTimeNano();
        int i = 0;
        if (!this.chunksToRebuild.isEmpty()) {
            Iterator<ChunkBuilder.BuiltChunk> iterator = this.chunksToRebuild.iterator();
            while (iterator.hasNext()) {
                long n;
                long o;
                ChunkBuilder.BuiltChunk builtChunk = iterator.next();
                if (builtChunk.needsImportantRebuild()) {
                    this.chunkBuilder.rebuild(builtChunk);
                } else {
                    builtChunk.scheduleRebuild(this.chunkBuilder);
                }
                builtChunk.cancelRebuild();
                iterator.remove();
                long m = Util.getMeasuringTimeNano();
                long p = limitTime - m;
                if (p >= (o = (n = m - l) / (long)(++i))) continue;
                break;
            }
        }
    }

    private void renderWorldBorder(Camera camera) {
        float v;
        double u;
        double t;
        float s;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
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
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        this.textureManager.bindTexture(FORCEFIELD);
        RenderSystem.depthMask(false);
        RenderSystem.pushMatrix();
        int i = worldBorder.getStage().getColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        RenderSystem.color4f(j, k, l, (float)e);
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float n = 0.0f;
        float o = 0.0f;
        float p = 128.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        double q = Math.max((double)MathHelper.floor(h - d), worldBorder.getBoundNorth());
        double r = Math.min((double)MathHelper.ceil(h + d), worldBorder.getBoundSouth());
        if (f > worldBorder.getBoundEast() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 256, t, m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 256, t + u, m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 0, t + u, m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 0, t, m + s, m + 128.0f);
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
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 256, t, m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 256, t + u, m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 0, t + u, m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 0, t, m + s, m + 128.0f);
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
                this.method_22978(bufferBuilder, f, g, h, t, 256, worldBorder.getBoundSouth(), m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 256, worldBorder.getBoundSouth(), m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 0, worldBorder.getBoundSouth(), m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, t, 0, worldBorder.getBoundSouth(), m + s, m + 128.0f);
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
                this.method_22978(bufferBuilder, f, g, h, t, 256, worldBorder.getBoundNorth(), m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 256, worldBorder.getBoundNorth(), m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 0, worldBorder.getBoundNorth(), m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, t, 0, worldBorder.getBoundNorth(), m + s, m + 128.0f);
                t += 1.0;
                s += 0.5f;
            }
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
    }

    private void method_22978(BufferBuilder bufferBuilder, double d, double e, double f, double g, int i, double h, float j, float k) {
        bufferBuilder.vertex(g - d, (double)i - e, h - f).texture(j, k).next();
    }

    private void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState) {
        WorldRenderer.drawShapeOutline(matrixStack, vertexConsumer, blockState.getOutlineShape(this.world, blockPos, EntityContext.of(entity)), (double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void method_22983(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        List<Box> list = voxelShape.getBoundingBoxes();
        int k = MathHelper.ceil((double)list.size() / 3.0);
        for (int l = 0; l < list.size(); ++l) {
            Box box = list.get(l);
            float m = ((float)l % (float)k + 1.0f) / (float)k;
            float n = l / k;
            float o = m * (float)(n == 0.0f ? 1 : 0);
            float p = m * (float)(n == 1.0f ? 1 : 0);
            float q = m * (float)(n == 2.0f ? 1 : 0);
            WorldRenderer.drawShapeOutline(matrixStack, vertexConsumer, VoxelShapes.cuboid(box.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0f);
        }
    }

    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        Matrix4f matrix4f = matrixStack.peek().getModel();
        voxelShape.forEachEdge((k, l, m, n, o, p) -> {
            vertexConsumer.vertex(matrix4f, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).next();
            vertexConsumer.vertex(matrix4f, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).next();
        });
    }

    public static void drawBox(VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        WorldRenderer.drawBox(new MatrixStack(), vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Box box, float f, float g, float h, float i) {
        WorldRenderer.drawBox(matrixStack, vertexConsumer, box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, f, g, h, i, f, g, h);
    }

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        WorldRenderer.drawBox(matrixStack, vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    public static void drawBox(MatrixStack matrix, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float f, float g, float alpha, float h, float green, float blue) {
        Matrix4f matrix4f = matrix.peek().getModel();
        float i = (float)x1;
        float j = (float)y1;
        float k = (float)z1;
        float l = (float)x2;
        float m = (float)y2;
        float n = (float)z2;
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(h, f, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, k).color(h, f, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(h, green, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, n).color(h, green, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, i, m, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, j, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, n).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, k).color(red, f, g, alpha).next();
        vertexConsumer.vertex(matrix4f, l, m, n).color(red, f, g, alpha).next();
    }

    public static void drawBox(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
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

    public void checkBlockRerender(BlockPos pos, BlockState old, BlockState updated) {
        if (this.client.getBakedModelManager().shouldRerender(old, updated)) {
            this.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
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
            case 2009: {
                for (int i = 0; i < 8; ++i) {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)pos.getX() + Math.random(), (double)pos.getY() + 1.2, (double)pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
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
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.remove(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
        } else {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.get(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
            if (blockBreakingInfo == null || blockBreakingInfo.getPos().getX() != pos.getX() || blockBreakingInfo.getPos().getY() != pos.getY() || blockBreakingInfo.getPos().getZ() != pos.getZ()) {
                blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
                this.blockBreakingInfos.put(entityId, (Object)blockBreakingInfo);
            }
            blockBreakingInfo.setStage(stage);
            blockBreakingInfo.setLastUpdateTick(this.ticks);
            ((SortedSet)this.blockBreakingProgressions.computeIfAbsent(blockBreakingInfo.getPos().asLong(), l -> Sets.newTreeSet())).add(blockBreakingInfo);
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

    public static int getLightmapCoordinates(BlockRenderView view, BlockPos pos) {
        return WorldRenderer.getLightmapCoordinates(view, view.getBlockState(pos), pos);
    }

    public static int getLightmapCoordinates(BlockRenderView view, BlockState state, BlockPos pos) {
        int k;
        if (state.hasEmissiveLighting()) {
            return 0xF000F0;
        }
        int i = view.getLightLevel(LightType.SKY, pos);
        int j = view.getLightLevel(LightType.BLOCK, pos);
        if (j < (k = state.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
    }

    public Framebuffer getEntityOutlinesFramebuffer() {
        return this.entityOutlinesFramebuffer;
    }

    @Environment(value=EnvType.CLIENT)
    class ChunkInfo {
        private final ChunkBuilder.BuiltChunk chunk;
        private final Direction direction;
        private byte cullingState;
        private final int propagationLevel;

        private ChunkInfo(@Nullable ChunkBuilder.BuiltChunk chunk, Direction direction, int propagationLevel) {
            this.chunk = chunk;
            this.direction = direction;
            this.propagationLevel = propagationLevel;
        }

        public void updateCullingState(byte parentCullingState, Direction from) {
            this.cullingState = (byte)(this.cullingState | (parentCullingState | 1 << from.ordinal()));
        }

        public boolean canCull(Direction from) {
            return (this.cullingState & 1 << from.ordinal()) > 0;
        }
    }
}

