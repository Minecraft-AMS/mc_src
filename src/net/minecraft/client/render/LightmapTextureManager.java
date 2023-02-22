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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean isDirty;
    private float prevFlicker;
    private float flicker;
    private final GameRenderer worldRenderer;
    private final MinecraftClient client;

    public LightmapTextureManager(GameRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.client = worldRenderer.getClient();
        this.texture = new NativeImageBackedTexture(16, 16, false);
        this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
        this.image = this.texture.getImage();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void tick() {
        this.flicker = (float)((double)this.flicker + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.flicker = (float)((double)this.flicker * 0.9);
        this.prevFlicker += this.flicker - this.prevFlicker;
        this.isDirty = true;
    }

    public void disable() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    public void enable() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 0.00390625f;
        GlStateManager.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        GlStateManager.translatef(8.0f, 8.0f, 8.0f);
        GlStateManager.matrixMode(5888);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        GlStateManager.texParameter(3553, 10241, 9729);
        GlStateManager.texParameter(3553, 10240, 9729);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    public void update(float delta) {
        if (!this.isDirty) {
            return;
        }
        this.client.getProfiler().push("lightTex");
        ClientWorld world = this.client.world;
        if (world == null) {
            return;
        }
        float f = world.getAmbientLight(1.0f);
        float g = f * 0.95f + 0.05f;
        float h = this.client.player.method_3140();
        float i = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? this.worldRenderer.getNightVisionStrength(this.client.player, delta) : (h > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? h : 0.0f);
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                float w;
                float l = world.dimension.getLightLevelToBrightness()[j] * g;
                float m = world.dimension.getLightLevelToBrightness()[k] * (this.prevFlicker * 0.1f + 1.5f);
                if (world.getTicksSinceLightning() > 0) {
                    l = world.dimension.getLightLevelToBrightness()[j];
                }
                float n = l * (f * 0.65f + 0.35f);
                float o = l * (f * 0.65f + 0.35f);
                float p = l;
                float q = m;
                float r = m * ((m * 0.6f + 0.4f) * 0.6f + 0.4f);
                float s = m * (m * m * 0.6f + 0.4f);
                float t = n + q;
                float u = o + r;
                float v = p + s;
                t = t * 0.96f + 0.03f;
                u = u * 0.96f + 0.03f;
                v = v * 0.96f + 0.03f;
                if (this.worldRenderer.getSkyDarkness(delta) > 0.0f) {
                    w = this.worldRenderer.getSkyDarkness(delta);
                    t = t * (1.0f - w) + t * 0.7f * w;
                    u = u * (1.0f - w) + u * 0.6f * w;
                    v = v * (1.0f - w) + v * 0.6f * w;
                }
                if (world.dimension.getType() == DimensionType.THE_END) {
                    t = 0.22f + q * 0.75f;
                    u = 0.28f + r * 0.75f;
                    v = 0.25f + s * 0.75f;
                }
                if (i > 0.0f) {
                    w = 1.0f / t;
                    if (w > 1.0f / u) {
                        w = 1.0f / u;
                    }
                    if (w > 1.0f / v) {
                        w = 1.0f / v;
                    }
                    t = t * (1.0f - i) + t * w * i;
                    u = u * (1.0f - i) + u * w * i;
                    v = v * (1.0f - i) + v * w * i;
                }
                if (t > 1.0f) {
                    t = 1.0f;
                }
                if (u > 1.0f) {
                    u = 1.0f;
                }
                if (v > 1.0f) {
                    v = 1.0f;
                }
                w = (float)this.client.options.gamma;
                float x = 1.0f - t;
                float y = 1.0f - u;
                float z = 1.0f - v;
                x = 1.0f - x * x * x * x;
                y = 1.0f - y * y * y * y;
                z = 1.0f - z * z * z * z;
                t = t * (1.0f - w) + x * w;
                u = u * (1.0f - w) + y * w;
                v = v * (1.0f - w) + z * w;
                t = t * 0.96f + 0.03f;
                u = u * 0.96f + 0.03f;
                v = v * 0.96f + 0.03f;
                if (t > 1.0f) {
                    t = 1.0f;
                }
                if (u > 1.0f) {
                    u = 1.0f;
                }
                if (v > 1.0f) {
                    v = 1.0f;
                }
                if (t < 0.0f) {
                    t = 0.0f;
                }
                if (u < 0.0f) {
                    u = 0.0f;
                }
                if (v < 0.0f) {
                    v = 0.0f;
                }
                int aa = 255;
                int ab = (int)(t * 255.0f);
                int ac = (int)(u * 255.0f);
                int ad = (int)(v * 255.0f);
                this.image.setPixelRgba(k, j, 0xFF000000 | ad << 16 | ac << 8 | ab);
            }
        }
        this.texture.upload();
        this.isDirty = false;
        this.client.getProfiler().pop();
    }
}

