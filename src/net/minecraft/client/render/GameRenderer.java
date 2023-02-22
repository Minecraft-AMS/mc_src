/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.FrustumWithOrigin;
import net.minecraft.client.render.GlMatrixFrustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameRenderer
implements AutoCloseable,
SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier RAIN_LOC = new Identifier("textures/environment/rain.png");
    private static final Identifier SNOW_LOC = new Identifier("textures/environment/snow.png");
    private final MinecraftClient client;
    private final ResourceManager resourceContainer;
    private final Random random = new Random();
    private float viewDistance;
    public final HeldItemRenderer firstPersonRenderer;
    private final MapRenderer mapRenderer;
    private int ticks;
    private float movementFovMultiplier;
    private float lastMovementFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand = true;
    private boolean blockOutlineEnabled = true;
    private long lastWorldIconUpdate;
    private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
    private final LightmapTextureManager lightmapTextureManager;
    private int field_3995;
    private final float[] field_3991 = new float[1024];
    private final float[] field_3989 = new float[1024];
    private final BackgroundRenderer backgroundRenderer;
    private boolean field_4001;
    private double field_4005 = 1.0;
    private double field_3988;
    private double field_4004;
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    private ShaderEffect shader;
    private static final Identifier[] SHADERS_LOCATIONS = new Identifier[]{new Identifier("shaders/post/notch.json"), new Identifier("shaders/post/fxaa.json"), new Identifier("shaders/post/art.json"), new Identifier("shaders/post/bumpy.json"), new Identifier("shaders/post/blobs2.json"), new Identifier("shaders/post/pencil.json"), new Identifier("shaders/post/color_convolve.json"), new Identifier("shaders/post/deconverge.json"), new Identifier("shaders/post/flip.json"), new Identifier("shaders/post/invert.json"), new Identifier("shaders/post/ntsc.json"), new Identifier("shaders/post/outline.json"), new Identifier("shaders/post/phosphor.json"), new Identifier("shaders/post/scan_pincushion.json"), new Identifier("shaders/post/sobel.json"), new Identifier("shaders/post/bits.json"), new Identifier("shaders/post/desaturate.json"), new Identifier("shaders/post/green.json"), new Identifier("shaders/post/blur.json"), new Identifier("shaders/post/wobble.json"), new Identifier("shaders/post/blobs.json"), new Identifier("shaders/post/antialias.json"), new Identifier("shaders/post/creeper.json"), new Identifier("shaders/post/spider.json")};
    public static final int SHADER_COUNT = SHADERS_LOCATIONS.length;
    private int forcedShaderIndex = SHADER_COUNT;
    private boolean shadersEnabled;
    private int field_4021;
    private final Camera camera = new Camera();

    public GameRenderer(MinecraftClient client, ResourceManager resourceManager) {
        this.client = client;
        this.resourceContainer = resourceManager;
        this.firstPersonRenderer = client.getHeldItemRenderer();
        this.mapRenderer = new MapRenderer(client.getTextureManager());
        this.lightmapTextureManager = new LightmapTextureManager(this);
        this.backgroundRenderer = new BackgroundRenderer(this);
        this.shader = null;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.sqrt(f * f + g * g);
                this.field_3991[i << 5 | j] = -g / h;
                this.field_3989[i << 5 | j] = f / h;
            }
        }
    }

    @Override
    public void close() {
        this.lightmapTextureManager.close();
        this.mapRenderer.close();
        this.disableShader();
    }

    public boolean isShaderEnabled() {
        return GLX.usePostProcess && this.shader != null;
    }

    public void disableShader() {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        this.forcedShaderIndex = SHADER_COUNT;
    }

    public void toggleShadersEnabled() {
        this.shadersEnabled = !this.shadersEnabled;
    }

    public void onCameraEntitySet(@Nullable Entity entity) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (entity instanceof CreeperEntity) {
            this.loadShader(new Identifier("shaders/post/creeper.json"));
        } else if (entity instanceof SpiderEntity) {
            this.loadShader(new Identifier("shaders/post/spider.json"));
        } else if (entity instanceof EndermanEntity) {
            this.loadShader(new Identifier("shaders/post/invert.json"));
        }
    }

    private void loadShader(Identifier identifier) {
        if (this.shader != null) {
            this.shader.close();
        }
        try {
            this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), identifier);
            this.shader.setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
            this.shadersEnabled = true;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)iOException);
            this.forcedShaderIndex = SHADER_COUNT;
            this.shadersEnabled = false;
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)jsonSyntaxException);
            this.forcedShaderIndex = SHADER_COUNT;
            this.shadersEnabled = false;
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (this.forcedShaderIndex == SHADER_COUNT) {
            this.onCameraEntitySet(this.client.getCameraEntity());
        } else {
            this.loadShader(SHADERS_LOCATIONS[this.forcedShaderIndex]);
        }
    }

    public void tick() {
        if (GLX.usePostProcess && GlProgramManager.getInstance() == null) {
            GlProgramManager.init();
        }
        this.updateMovementFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.camera.updateEyeHeight();
        ++this.ticks;
        this.firstPersonRenderer.updateHeldItems();
        this.renderRain();
        this.lastSkyDarkness = this.skyDarkness;
        if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05f;
            if (this.skyDarkness > 1.0f) {
                this.skyDarkness = 1.0f;
            }
        } else if (this.skyDarkness > 0.0f) {
            this.skyDarkness -= 0.0125f;
        }
        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }

    public ShaderEffect getShader() {
        return this.shader;
    }

    public void onResized(int i, int j) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.shader != null) {
            this.shader.setupDimensions(i, j);
        }
        this.client.worldRenderer.onResized(i, j);
    }

    public void updateTargetedEntity(float tickDelta) {
        Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        if (this.client.world == null) {
            return;
        }
        this.client.getProfiler().push("pick");
        this.client.targetedEntity = null;
        double d = this.client.interactionManager.getReachDistance();
        this.client.crosshairTarget = entity2.rayTrace(d, tickDelta, false);
        Vec3d vec3d = entity2.getCameraPosVec(tickDelta);
        boolean bl = false;
        int i = 3;
        double e = d;
        if (this.client.interactionManager.hasExtendedReach()) {
            d = e = 6.0;
        } else {
            if (e > 3.0) {
                bl = true;
            }
            d = e;
        }
        e *= e;
        if (this.client.crosshairTarget != null) {
            e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
        }
        Vec3d vec3d2 = entity2.getRotationVec(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f = 1.0f;
        Box box = entity2.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.collides(), e);
        if (entityHitResult != null) {
            Entity entity22 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (bl && g > 9.0) {
                this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
            } else if (g < e || this.client.crosshairTarget == null) {
                this.client.crosshairTarget = entityHitResult;
                if (entity22 instanceof LivingEntity || entity22 instanceof ItemFrameEntity) {
                    this.client.targetedEntity = entity22;
                }
            }
        }
        this.client.getProfiler().pop();
    }

    private void updateMovementFovMultiplier() {
        float f = 1.0f;
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
            f = abstractClientPlayerEntity.getSpeed();
        }
        this.lastMovementFovMultiplier = this.movementFovMultiplier;
        this.movementFovMultiplier += (f - this.movementFovMultiplier) * 0.5f;
        if (this.movementFovMultiplier > 1.5f) {
            this.movementFovMultiplier = 1.5f;
        }
        if (this.movementFovMultiplier < 0.1f) {
            this.movementFovMultiplier = 0.1f;
        }
    }

    private double getFov(Camera camera, float tickDelta, boolean changingFov) {
        FluidState fluidState;
        if (this.field_4001) {
            return 90.0;
        }
        double d = 70.0;
        if (changingFov) {
            d = this.client.options.fov;
            d *= (double)MathHelper.lerp(tickDelta, this.lastMovementFovMultiplier, this.movementFovMultiplier);
        }
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).getHealth() <= 0.0f) {
            float f = (float)((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta;
            d /= (double)((1.0f - 500.0f / (f + 500.0f)) * 2.0f + 1.0f);
        }
        if (!(fluidState = camera.getSubmergedFluidState()).isEmpty()) {
            d = d * 60.0 / 70.0;
        }
        return d;
    }

    private void bobViewWhenHurt(float tickDelta) {
        if (this.client.getCameraEntity() instanceof LivingEntity) {
            float g;
            LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
            float f = (float)livingEntity.hurtTime - tickDelta;
            if (livingEntity.getHealth() <= 0.0f) {
                g = (float)livingEntity.deathTime + tickDelta;
                GlStateManager.rotatef(40.0f - 8000.0f / (g + 200.0f), 0.0f, 0.0f, 1.0f);
            }
            if (f < 0.0f) {
                return;
            }
            f /= (float)livingEntity.field_6254;
            f = MathHelper.sin(f * f * f * f * (float)Math.PI);
            g = livingEntity.field_6271;
            GlStateManager.rotatef(-g, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(-f * 14.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(g, 0.0f, 1.0f, 0.0f);
        }
    }

    private void bobView(float tickDelta) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
        float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
        float g = -(playerEntity.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, playerEntity.field_7505, playerEntity.field_7483);
        GlStateManager.translatef(MathHelper.sin(g * (float)Math.PI) * h * 0.5f, -Math.abs(MathHelper.cos(g * (float)Math.PI) * h), 0.0f);
        GlStateManager.rotatef(MathHelper.sin(g * (float)Math.PI) * h * 3.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2f) * h) * 5.0f, 1.0f, 0.0f, 0.0f);
    }

    private void applyCameraTransformations(float tickDelta) {
        float f;
        this.viewDistance = this.client.options.viewDistance * 16;
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        if (this.field_4005 != 1.0) {
            GlStateManager.translatef((float)this.field_3988, (float)(-this.field_4004), 0.0f);
            GlStateManager.scaled(this.field_4005, this.field_4005, 1.0);
        }
        GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(this.camera, tickDelta, true), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.bobViewWhenHurt(tickDelta);
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
        if ((f = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f) {
            int i = 20;
            if (this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
                i = 7;
            }
            float g = 5.0f / (f * f + 5.0f) - f * 0.04f;
            g *= g;
            GlStateManager.rotatef(((float)this.ticks + tickDelta) * (float)i, 0.0f, 1.0f, 1.0f);
            GlStateManager.scalef(1.0f / g, 1.0f, 1.0f);
            GlStateManager.rotatef(-((float)this.ticks + tickDelta) * (float)i, 0.0f, 1.0f, 1.0f);
        }
    }

    private void renderHand(Camera camera, float tickDelta) {
        boolean bl;
        if (this.field_4001) {
            return;
        }
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(camera, tickDelta, false), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 2.0f));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.pushMatrix();
        this.bobViewWhenHurt(tickDelta);
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
        boolean bl2 = bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
        if (this.client.options.perspective == 0 && !bl && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.enableLightmap();
            this.firstPersonRenderer.renderFirstPersonItem(tickDelta);
            this.disableLightmap();
        }
        GlStateManager.popMatrix();
        if (this.client.options.perspective == 0 && !bl) {
            this.firstPersonRenderer.renderOverlays(tickDelta);
            this.bobViewWhenHurt(tickDelta);
        }
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
    }

    public void disableLightmap() {
        this.lightmapTextureManager.disable();
    }

    public void enableLightmap() {
        this.lightmapTextureManager.enable();
    }

    public float getNightVisionStrength(LivingEntity entity, float tickDelta) {
        int i = entity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
        if (i > 200) {
            return 1.0f;
        }
        return 0.7f + MathHelper.sin(((float)i - tickDelta) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(float tickDelta, long startTime, boolean tick) {
        if (this.client.isWindowFocused() || !this.client.options.pauseOnLostFocus || this.client.options.touchscreen && this.client.mouse.wasRightButtonClicked()) {
            this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
        } else if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openPauseMenu(false);
        }
        if (this.client.skipGameRender) {
            return;
        }
        int i = (int)(this.client.mouse.getX() * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth());
        int j = (int)(this.client.mouse.getY() * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight());
        int k = this.client.options.maxFps;
        if (tick && this.client.world != null) {
            this.client.getProfiler().push("level");
            int l = Math.min(MinecraftClient.getCurrentFps(), k);
            l = Math.max(l, 60);
            long m = Util.getMeasuringTimeNano() - startTime;
            long n = Math.max((long)(1000000000 / l / 4) - m, 0L);
            this.renderWorld(tickDelta, Util.getMeasuringTimeNano() + n);
            if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < Util.getMeasuringTimeMs() - 1000L) {
                this.lastWorldIconUpdate = Util.getMeasuringTimeMs();
                if (!this.client.getServer().hasIconFile()) {
                    this.updateWorldIcon();
                }
            }
            if (GLX.usePostProcess) {
                this.client.worldRenderer.drawEntityOutlinesFramebuffer();
                if (this.shader != null && this.shadersEnabled) {
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    this.shader.render(tickDelta);
                    GlStateManager.popMatrix();
                }
                this.client.getFramebuffer().beginWrite(true);
            }
            this.client.getProfiler().swap("gui");
            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
                GlStateManager.alphaFunc(516, 0.1f);
                this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
                this.renderFloatingItem(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), tickDelta);
                this.client.inGameHud.render(tickDelta);
            }
            this.client.getProfiler().pop();
        } else {
            GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
        }
        if (this.client.overlay != null) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            try {
                this.client.overlay.render(i, j, this.client.getLastFrameDuration());
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering overlay");
                CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
                crashReportSection.add("Overlay name", () -> this.client.overlay.getClass().getCanonicalName());
                throw new CrashException(crashReport);
            }
        }
        if (this.client.currentScreen != null) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            try {
                this.client.currentScreen.render(i, j, this.client.getLastFrameDuration());
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering screen");
                CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
                crashReportSection.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                crashReportSection.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()));
                crashReportSection.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.window.getScaleFactor()));
                throw new CrashException(crashReport);
            }
        }
    }

    private void updateWorldIcon() {
        if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete() && !this.client.getServer().hasIconFile()) {
            NativeImage nativeImage = ScreenshotUtils.method_1663(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.getFramebuffer());
            ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
                int i = nativeImage.getWidth();
                int j = nativeImage.getHeight();
                int k = 0;
                int l = 0;
                if (i > j) {
                    k = (i - j) / 2;
                    i = j;
                } else {
                    l = (j - i) / 2;
                    j = i;
                }
                try (NativeImage nativeImage2 = new NativeImage(64, 64, false);){
                    nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
                    nativeImage2.writeFile(this.client.getServer().getIconFile());
                }
                catch (IOException iOException) {
                    LOGGER.warn("Couldn't save auto screenshot", (Throwable)iOException);
                }
                finally {
                    nativeImage.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean bl;
        if (!this.blockOutlineEnabled) {
            return false;
        }
        Entity entity = this.client.getCameraEntity();
        boolean bl2 = bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
        if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
            ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
            HitResult hitResult = this.client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.client.world.getBlockState(blockPos);
                if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                    bl = blockState.createContainerFactory(this.client.world, blockPos) != null;
                } else {
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
                    bl = !itemStack.isEmpty() && (itemStack.canDestroy(this.client.world.getTagManager(), cachedBlockPosition) || itemStack.canPlaceOn(this.client.world.getTagManager(), cachedBlockPosition));
                }
            }
        }
        return bl;
    }

    public void renderWorld(float tickDelta, long endTime) {
        this.lightmapTextureManager.update(tickDelta);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.updateTargetedEntity(tickDelta);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.5f);
        this.client.getProfiler().push("center");
        this.renderCenter(tickDelta, endTime);
        this.client.getProfiler().pop();
    }

    private void renderCenter(float tickDelta, long endTime) {
        WorldRenderer worldRenderer = this.client.worldRenderer;
        ParticleManager particleManager = this.client.particleManager;
        boolean bl = this.shouldRenderBlockOutline();
        GlStateManager.enableCull();
        this.client.getProfiler().swap("camera");
        this.applyCameraTransformations(tickDelta);
        Camera camera = this.camera;
        camera.update(this.client.world, this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity(), this.client.options.perspective > 0, this.client.options.perspective == 2, tickDelta);
        Frustum frustum = GlMatrixFrustum.get();
        worldRenderer.method_21595(camera);
        this.client.getProfiler().swap("clear");
        GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
        this.backgroundRenderer.renderBackground(camera, tickDelta);
        GlStateManager.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        this.client.getProfiler().swap("culling");
        FrustumWithOrigin visibleRegion = new FrustumWithOrigin(frustum);
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        visibleRegion.setOrigin(d, e, f);
        if (this.client.options.viewDistance >= 4) {
            this.backgroundRenderer.applyFog(camera, -1);
            this.client.getProfiler().swap("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(camera, tickDelta, true), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 2.0f));
            GlStateManager.matrixMode(5888);
            worldRenderer.renderSky(tickDelta);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(camera, tickDelta, true), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
        this.backgroundRenderer.applyFog(camera, 0);
        GlStateManager.shadeModel(7425);
        if (camera.getPos().y < 128.0) {
            this.renderAboveClouds(camera, worldRenderer, tickDelta, d, e, f);
        }
        this.client.getProfiler().swap("prepareterrain");
        this.backgroundRenderer.applyFog(camera, 0);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        DiffuseLighting.disable();
        this.client.getProfiler().swap("terrain_setup");
        this.client.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        worldRenderer.setUpTerrain(camera, visibleRegion, this.field_4021++, this.client.player.isSpectator());
        this.client.getProfiler().swap("updatechunks");
        this.client.worldRenderer.updateChunks(endTime);
        this.client.getProfiler().swap("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();
        worldRenderer.renderLayer(RenderLayer.SOLID, camera);
        GlStateManager.enableAlphaTest();
        worldRenderer.renderLayer(RenderLayer.CUTOUT_MIPPED, camera);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        worldRenderer.renderLayer(RenderLayer.CUTOUT, camera);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        DiffuseLighting.enable();
        this.client.getProfiler().swap("entities");
        worldRenderer.renderEntities(camera, visibleRegion, tickDelta);
        DiffuseLighting.disable();
        this.disableLightmap();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        if (bl && this.client.crosshairTarget != null) {
            GlStateManager.disableAlphaTest();
            this.client.getProfiler().swap("outline");
            worldRenderer.drawHighlightedBlockOutline(camera, this.client.crosshairTarget, 0);
            GlStateManager.enableAlphaTest();
        }
        if (this.client.debugRenderer.shouldRender()) {
            this.client.debugRenderer.renderDebuggers(endTime);
        }
        this.client.getProfiler().swap("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        worldRenderer.renderPartiallyBrokenBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), camera);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
        GlStateManager.disableBlend();
        this.enableLightmap();
        this.backgroundRenderer.applyFog(camera, 0);
        this.client.getProfiler().swap("particles");
        particleManager.renderParticles(camera, tickDelta);
        this.disableLightmap();
        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.client.getProfiler().swap("weather");
        this.renderWeather(tickDelta);
        GlStateManager.depthMask(true);
        worldRenderer.renderWorldBorder(camera, tickDelta);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        this.backgroundRenderer.applyFog(camera, 0);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.shadeModel(7425);
        this.client.getProfiler().swap("translucent");
        worldRenderer.renderLayer(RenderLayer.TRANSLUCENT, camera);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();
        if (camera.getPos().y >= 128.0) {
            this.client.getProfiler().swap("aboveClouds");
            this.renderAboveClouds(camera, worldRenderer, tickDelta, d, e, f);
        }
        this.client.getProfiler().swap("hand");
        if (this.renderHand) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            this.renderHand(camera, tickDelta);
        }
    }

    private void renderAboveClouds(Camera camera, WorldRenderer worldRenderer, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
            this.client.getProfiler().swap("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(camera, tickDelta, true), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 4.0f));
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.backgroundRenderer.applyFog(camera, 0);
            worldRenderer.renderClouds(tickDelta, cameraX, cameraY, cameraZ);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.method_4929(this.getFov(camera, tickDelta, true), (float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
    }

    private void renderRain() {
        float f = this.client.world.getRainGradient(1.0f);
        if (!this.client.options.fancyGraphics) {
            f /= 2.0f;
        }
        if (f == 0.0f) {
            return;
        }
        this.random.setSeed((long)this.ticks * 312987231L);
        ClientWorld collisionView = this.client.world;
        BlockPos blockPos = new BlockPos(this.camera.getPos());
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
            BlockPos blockPos2 = collisionView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            Biome biome = collisionView.getBiome(blockPos2);
            BlockPos blockPos3 = blockPos2.down();
            if (blockPos2.getY() > blockPos.getY() + 10 || blockPos2.getY() < blockPos.getY() - 10 || biome.getPrecipitation() != Biome.Precipitation.RAIN || !(biome.getTemperature(blockPos2) >= 0.15f)) continue;
            double h = this.random.nextDouble();
            double m = this.random.nextDouble();
            BlockState blockState = collisionView.getBlockState(blockPos3);
            FluidState fluidState = collisionView.getFluidState(blockPos2);
            VoxelShape voxelShape = blockState.getCollisionShape(collisionView, blockPos3);
            double n = voxelShape.method_1102(Direction.Axis.Y, h, m);
            if (n >= (o = (double)fluidState.getHeight(collisionView, blockPos2))) {
                p = n;
                q = voxelShape.method_1093(Direction.Axis.Y, h, m);
            } else {
                p = 0.0;
                q = 0.0;
            }
            if (!(p > -1.7976931348623157E308)) continue;
            if (fluidState.matches(FluidTags.LAVA) || blockState.getBlock() == Blocks.MAGMA_BLOCK || blockState.getBlock() == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT).booleanValue()) {
                this.client.world.addParticle(ParticleTypes.SMOKE, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1f) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0);
                continue;
            }
            if (this.random.nextInt(++j) == 0) {
                d = (double)blockPos3.getX() + h;
                e = (double)((float)blockPos3.getY() + 0.1f) + p - 1.0;
                g = (double)blockPos3.getZ() + m;
            }
            this.client.world.addParticle(ParticleTypes.RAIN, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1f) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0);
        }
        if (j > 0 && this.random.nextInt(3) < this.field_3995++) {
            this.field_3995 = 0;
            if (e > (double)(blockPos.getY() + 1) && collisionView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor(blockPos.getY())) {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    protected void renderWeather(float f) {
        float g = this.client.world.getRainGradient(f);
        if (g <= 0.0f) {
            return;
        }
        this.enableLightmap();
        ClientWorld world = this.client.world;
        int i = MathHelper.floor(this.camera.getPos().x);
        int j = MathHelper.floor(this.camera.getPos().y);
        int k = MathHelper.floor(this.camera.getPos().z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.disableCull();
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        double d = this.camera.getPos().x;
        double e = this.camera.getPos().y;
        double h = this.camera.getPos().z;
        int l = MathHelper.floor(e);
        int m = 5;
        if (this.client.options.fancyGraphics) {
            m = 10;
        }
        int n = -1;
        float o = (float)this.ticks + f;
        bufferBuilder.setOffset(-d, -e, -h);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int p = k - m; p <= k + m; ++p) {
            for (int q = i - m; q <= i + m; ++q) {
                double ab;
                double aa;
                double z;
                int x;
                int r = (p - k + 16) * 32 + q - i + 16;
                double s = (double)this.field_3991[r] * 0.5;
                double t = (double)this.field_3989[r] * 0.5;
                mutable.set(q, 0, p);
                Biome biome = world.getBiome(mutable);
                if (biome.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int u = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
                int v = j - m;
                int w = j + m;
                if (v < u) {
                    v = u;
                }
                if (w < u) {
                    w = u;
                }
                if ((x = u) < l) {
                    x = l;
                }
                if (v == w) continue;
                this.random.setSeed(q * q * 3121 + q * 45238971 ^ p * p * 418711 + p * 13761);
                mutable.set(q, v, p);
                float y = biome.getTemperature(mutable);
                if (y >= 0.15f) {
                    if (n != 0) {
                        if (n >= 0) {
                            tessellator.draw();
                        }
                        n = 0;
                        this.client.getTextureManager().bindTexture(RAIN_LOC);
                        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    }
                    z = -((double)(this.ticks + q * q * 3121 + q * 45238971 + p * p * 418711 + p * 13761 & 0x1F) + (double)f) / 32.0 * (3.0 + this.random.nextDouble());
                    aa = (double)((float)q + 0.5f) - this.camera.getPos().x;
                    ab = (double)((float)p + 0.5f) - this.camera.getPos().z;
                    float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)m;
                    float ad = ((1.0f - ac * ac) * 0.5f + 0.5f) * g;
                    mutable.set(q, x, p);
                    int ae = world.getLightmapIndex(mutable, 0);
                    int af = ae >> 16 & 0xFFFF;
                    int ag = ae & 0xFFFF;
                    bufferBuilder.vertex((double)q - s + 0.5, w, (double)p - t + 0.5).texture(0.0, (double)v * 0.25 + z).color(1.0f, 1.0f, 1.0f, ad).texture(af, ag).next();
                    bufferBuilder.vertex((double)q + s + 0.5, w, (double)p + t + 0.5).texture(1.0, (double)v * 0.25 + z).color(1.0f, 1.0f, 1.0f, ad).texture(af, ag).next();
                    bufferBuilder.vertex((double)q + s + 0.5, v, (double)p + t + 0.5).texture(1.0, (double)w * 0.25 + z).color(1.0f, 1.0f, 1.0f, ad).texture(af, ag).next();
                    bufferBuilder.vertex((double)q - s + 0.5, v, (double)p - t + 0.5).texture(0.0, (double)w * 0.25 + z).color(1.0f, 1.0f, 1.0f, ad).texture(af, ag).next();
                    continue;
                }
                if (n != 1) {
                    if (n >= 0) {
                        tessellator.draw();
                    }
                    n = 1;
                    this.client.getTextureManager().bindTexture(SNOW_LOC);
                    bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                }
                z = -((float)(this.ticks & 0x1FF) + f) / 512.0f;
                aa = this.random.nextDouble() + (double)o * 0.01 * (double)((float)this.random.nextGaussian());
                ab = this.random.nextDouble() + (double)(o * (float)this.random.nextGaussian()) * 0.001;
                double ah = (double)((float)q + 0.5f) - this.camera.getPos().x;
                double ai = (double)((float)p + 0.5f) - this.camera.getPos().z;
                float aj = MathHelper.sqrt(ah * ah + ai * ai) / (float)m;
                float ak = ((1.0f - aj * aj) * 0.3f + 0.5f) * g;
                mutable.set(q, x, p);
                int al = (world.getLightmapIndex(mutable, 0) * 3 + 0xF000F0) / 4;
                int am = al >> 16 & 0xFFFF;
                int an = al & 0xFFFF;
                bufferBuilder.vertex((double)q - s + 0.5, w, (double)p - t + 0.5).texture(0.0 + aa, (double)v * 0.25 + z + ab).color(1.0f, 1.0f, 1.0f, ak).texture(am, an).next();
                bufferBuilder.vertex((double)q + s + 0.5, w, (double)p + t + 0.5).texture(1.0 + aa, (double)v * 0.25 + z + ab).color(1.0f, 1.0f, 1.0f, ak).texture(am, an).next();
                bufferBuilder.vertex((double)q + s + 0.5, v, (double)p + t + 0.5).texture(1.0 + aa, (double)w * 0.25 + z + ab).color(1.0f, 1.0f, 1.0f, ak).texture(am, an).next();
                bufferBuilder.vertex((double)q - s + 0.5, v, (double)p - t + 0.5).texture(0.0 + aa, (double)w * 0.25 + z + ab).color(1.0f, 1.0f, 1.0f, ak).texture(am, an).next();
            }
        }
        if (n >= 0) {
            tessellator.draw();
        }
        bufferBuilder.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        this.disableLightmap();
    }

    public void setFogBlack(boolean fogBlack) {
        this.backgroundRenderer.setFogBlack(fogBlack);
    }

    public void reset() {
        this.floatingItem = null;
        this.mapRenderer.clearStateTextures();
        this.camera.reset();
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public static void renderFloatingText(TextRenderer textRenderer, String text, float x, float y, float z, int verticalOffset, float yaw, float pitch, boolean translucent) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, z);
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        if (!translucent) {
            GlStateManager.disableDepthTest();
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = textRenderer.getStringWidth(text) / 2;
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        float f = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        bufferBuilder.vertex(-i - 1, -1 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, f).next();
        bufferBuilder.vertex(-i - 1, 8 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, f).next();
        bufferBuilder.vertex(i + 1, 8 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, f).next();
        bufferBuilder.vertex(i + 1, -1 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, f).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        if (!translucent) {
            textRenderer.draw(text, -textRenderer.getStringWidth(text) / 2, verticalOffset, 0x20FFFFFF);
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        textRenderer.draw(text, -textRenderer.getStringWidth(text) / 2, verticalOffset, translucent ? 0x20FFFFFF : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemHeight = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {
        if (this.floatingItem == null || this.floatingItemTimeLeft <= 0) {
            return;
        }
        int i = 40 - this.floatingItemTimeLeft;
        float f = ((float)i + tickDelta) / 40.0f;
        float g = f * f;
        float h = f * g;
        float j = 10.25f * h * g - 24.95f * g * g + 25.5f * h - 13.8f * g + 4.0f * f;
        float k = j * (float)Math.PI;
        float l = this.floatingItemWidth * (float)(scaledWidth / 4);
        float m = this.floatingItemHeight * (float)(scaledHeight / 4);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
        GlStateManager.pushLightingAttributes();
        GlStateManager.enableDepthTest();
        GlStateManager.disableCull();
        DiffuseLighting.enable();
        GlStateManager.translatef((float)(scaledWidth / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0f)), (float)(scaledHeight / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0f)), -50.0f);
        float n = 50.0f + 175.0f * MathHelper.sin(k);
        GlStateManager.scalef(n, -n, n);
        GlStateManager.rotatef(900.0f * MathHelper.abs(MathHelper.sin(k)), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(6.0f * MathHelper.cos(f * 8.0f), 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(6.0f * MathHelper.cos(f * 8.0f), 0.0f, 0.0f, 1.0f);
        this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformation.Type.FIXED);
        GlStateManager.popAttributes();
        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.enableCull();
        GlStateManager.disableDepthTest();
    }

    public MinecraftClient getClient() {
        return this.client;
    }

    public float getSkyDarkness(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
    }

    public float getViewDistance() {
        return this.viewDistance;
    }

    public Camera getCamera() {
        return this.camera;
    }
}
