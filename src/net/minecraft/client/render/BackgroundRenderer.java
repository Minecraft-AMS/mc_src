/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private final FloatBuffer blackColorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
    private final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
    private float red;
    private float green;
    private float blue;
    private float bufferRed = -1.0f;
    private float bufferGreen = -1.0f;
    private float bufferBlue = -1.0f;
    private int waterFogColor = -1;
    private int nextWaterFogColor = -1;
    private long lastWaterFogColorUpdateTime = -1L;
    private final GameRenderer gameRenderer;
    private final MinecraftClient client;

    public BackgroundRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.client = gameRenderer.getClient();
        this.blackColorBuffer.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
    }

    public void renderBackground(Camera camera, float tickDelta) {
        ClientWorld world = this.client.world;
        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.matches(FluidTags.WATER)) {
            this.updateColorInWater(camera, world);
        } else if (fluidState.matches(FluidTags.LAVA)) {
            this.red = 0.6f;
            this.green = 0.1f;
            this.blue = 0.0f;
            this.lastWaterFogColorUpdateTime = -1L;
        } else {
            this.updateColorNotInWater(camera, world, tickDelta);
            this.lastWaterFogColorUpdateTime = -1L;
        }
        double d = camera.getPos().y * world.dimension.getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int i = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            d = i < 20 ? (d *= (double)(1.0f - (float)i / 20.0f)) : 0.0;
        }
        if (d < 1.0) {
            if (d < 0.0) {
                d = 0.0;
            }
            d *= d;
            this.red = (float)((double)this.red * d);
            this.green = (float)((double)this.green * d);
            this.blue = (float)((double)this.blue * d);
        }
        if (this.gameRenderer.getSkyDarkness(tickDelta) > 0.0f) {
            float f = this.gameRenderer.getSkyDarkness(tickDelta);
            this.red = this.red * (1.0f - f) + this.red * 0.7f * f;
            this.green = this.green * (1.0f - f) + this.green * 0.6f * f;
            this.blue = this.blue * (1.0f - f) + this.blue * 0.6f * f;
        }
        if (fluidState.matches(FluidTags.WATER)) {
            float g;
            float f = 0.0f;
            if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                f = clientPlayerEntity.method_3140();
            }
            if ((g = 1.0f / this.red) > 1.0f / this.green) {
                g = 1.0f / this.green;
            }
            if (g > 1.0f / this.blue) {
                g = 1.0f / this.blue;
            }
            this.red = this.red * (1.0f - f) + this.red * g * f;
            this.green = this.green * (1.0f - f) + this.green * g * f;
            this.blue = this.blue * (1.0f - f) + this.blue * g * f;
        } else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float f = this.gameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
            float g = 1.0f / this.red;
            if (g > 1.0f / this.green) {
                g = 1.0f / this.green;
            }
            if (g > 1.0f / this.blue) {
                g = 1.0f / this.blue;
            }
            this.red = this.red * (1.0f - f) + this.red * g * f;
            this.green = this.green * (1.0f - f) + this.green * g * f;
            this.blue = this.blue * (1.0f - f) + this.blue * g * f;
        }
        GlStateManager.clearColor(this.red, this.green, this.blue, 0.0f);
    }

    private void updateColorNotInWater(Camera camera, World world, float tickDelta) {
        float l;
        float f = 0.25f + 0.75f * (float)this.client.options.viewDistance / 32.0f;
        f = 1.0f - (float)Math.pow(f, 0.25);
        Vec3d vec3d = world.getSkyColor(camera.getBlockPos(), tickDelta);
        float g = (float)vec3d.x;
        float h = (float)vec3d.y;
        float i = (float)vec3d.z;
        Vec3d vec3d2 = world.getFogColor(tickDelta);
        this.red = (float)vec3d2.x;
        this.green = (float)vec3d2.y;
        this.blue = (float)vec3d2.z;
        if (this.client.options.viewDistance >= 4) {
            float[] fs;
            double d = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0f ? -1.0 : 1.0;
            Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
            float j = (float)camera.getHorizontalPlane().dotProduct(vec3d3);
            if (j < 0.0f) {
                j = 0.0f;
            }
            if (j > 0.0f && (fs = world.dimension.getBackgroundColor(world.getSkyAngle(tickDelta), tickDelta)) != null) {
                this.red = this.red * (1.0f - (j *= fs[3])) + fs[0] * j;
                this.green = this.green * (1.0f - j) + fs[1] * j;
                this.blue = this.blue * (1.0f - j) + fs[2] * j;
            }
        }
        this.red += (g - this.red) * f;
        this.green += (h - this.green) * f;
        this.blue += (i - this.blue) * f;
        float k = world.getRainGradient(tickDelta);
        if (k > 0.0f) {
            l = 1.0f - k * 0.5f;
            float m = 1.0f - k * 0.4f;
            this.red *= l;
            this.green *= l;
            this.blue *= m;
        }
        if ((l = world.getThunderGradient(tickDelta)) > 0.0f) {
            float m = 1.0f - l * 0.5f;
            this.red *= m;
            this.green *= m;
            this.blue *= m;
        }
    }

    private void updateColorInWater(Camera camera, CollisionView world) {
        long l = Util.getMeasuringTimeMs();
        int i = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
        if (this.lastWaterFogColorUpdateTime < 0L) {
            this.waterFogColor = i;
            this.nextWaterFogColor = i;
            this.lastWaterFogColorUpdateTime = l;
        }
        int j = this.waterFogColor >> 16 & 0xFF;
        int k = this.waterFogColor >> 8 & 0xFF;
        int m = this.waterFogColor & 0xFF;
        int n = this.nextWaterFogColor >> 16 & 0xFF;
        int o = this.nextWaterFogColor >> 8 & 0xFF;
        int p = this.nextWaterFogColor & 0xFF;
        float f = MathHelper.clamp((float)(l - this.lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
        float g = MathHelper.lerp(f, n, j);
        float h = MathHelper.lerp(f, o, k);
        float q = MathHelper.lerp(f, p, m);
        this.red = g / 255.0f;
        this.green = h / 255.0f;
        this.blue = q / 255.0f;
        if (this.waterFogColor != i) {
            this.waterFogColor = i;
            this.nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
            this.lastWaterFogColorUpdateTime = l;
        }
    }

    public void applyFog(Camera camera, int i) {
        this.setFogBlack(false);
        GlStateManager.normal3f(0.0f, -1.0f, 0.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        FluidState fluidState = camera.getSubmergedFluidState();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            float f = 5.0f;
            int j = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            if (j < 20) {
                f = MathHelper.lerp(1.0f - (float)j / 20.0f, 5.0f, this.gameRenderer.getViewDistance());
            }
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (i == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(f * 0.8f);
            } else {
                GlStateManager.fogStart(f * 0.25f);
                GlStateManager.fogEnd(f);
            }
            GLX.setupNvFogDistance();
        } else if (fluidState.matches(FluidTags.WATER)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
            if (camera.getFocusedEntity() instanceof LivingEntity) {
                if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                    float g = 0.05f - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03f;
                    Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
                    if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                        g += 0.005f;
                    }
                    GlStateManager.fogDensity(g);
                } else {
                    GlStateManager.fogDensity(0.05f);
                }
            } else {
                GlStateManager.fogDensity(0.1f);
            }
        } else if (fluidState.matches(FluidTags.LAVA)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(2.0f);
        } else {
            float f = this.gameRenderer.getViewDistance();
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (i == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(f);
            } else {
                GlStateManager.fogStart(f * 0.75f);
                GlStateManager.fogEnd(f);
            }
            GLX.setupNvFogDistance();
            if (this.client.world.dimension.isFogThick(MathHelper.floor(camera.getPos().x), MathHelper.floor(camera.getPos().z)) || this.client.inGameHud.getBossBarHud().shouldThickenFog()) {
                GlStateManager.fogStart(f * 0.05f);
                GlStateManager.fogEnd(Math.min(f, 192.0f) * 0.5f);
            }
        }
        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }

    public void setFogBlack(boolean fogBlack) {
        if (fogBlack) {
            GlStateManager.fog(2918, this.blackColorBuffer);
        } else {
            GlStateManager.fog(2918, this.getColorAsBuffer());
        }
    }

    private FloatBuffer getColorAsBuffer() {
        if (this.bufferRed != this.red || this.bufferGreen != this.green || this.bufferBlue != this.blue) {
            this.colorBuffer.clear();
            this.colorBuffer.put(this.red).put(this.green).put(this.blue).put(1.0f);
            this.colorBuffer.flip();
            this.bufferRed = this.red;
            this.bufferGreen = this.green;
            this.bufferBlue = this.blue;
        }
        return this.colorBuffer;
    }
}

