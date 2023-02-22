/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashScreen
extends Overlay {
    private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
    private final MinecraftClient client;
    private final ResourceReloadMonitor reloadMonitor;
    private final Runnable field_18218;
    private final boolean field_18219;
    private float field_17770;
    private long field_17771 = -1L;
    private long field_18220 = -1L;

    public SplashScreen(MinecraftClient minecraftClient, ResourceReloadMonitor resourceReloadMonitor, Runnable runnable, boolean bl) {
        this.client = minecraftClient;
        this.reloadMonitor = resourceReloadMonitor;
        this.field_18218 = runnable;
        this.field_18219 = bl;
    }

    public static void method_18819(MinecraftClient minecraftClient) {
        minecraftClient.getTextureManager().registerTexture(LOGO, new class_4070());
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        float h;
        int k;
        float g;
        int i = this.client.window.getScaledWidth();
        int j = this.client.window.getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.field_18219 && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.field_18220 == -1L) {
            this.field_18220 = l;
        }
        float f = this.field_17771 > -1L ? (float)(l - this.field_17771) / 1000.0f : -1.0f;
        float f2 = g = this.field_18220 > -1L ? (float)(l - this.field_18220) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(0, 0, delta);
            }
            k = MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f);
            SplashScreen.fill(0, 0, i, j, 0xFFFFFF | k << 24);
            h = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else if (this.field_18219) {
            if (this.client.currentScreen != null && g < 1.0f) {
                this.client.currentScreen.render(mouseX, mouseY, delta);
            }
            k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
            SplashScreen.fill(0, 0, i, j, 0xFFFFFF | k << 24);
            h = MathHelper.clamp(g, 0.0f, 1.0f);
        } else {
            SplashScreen.fill(0, 0, i, j, -1);
            h = 1.0f;
        }
        k = (this.client.window.getScaledWidth() - 256) / 2;
        int m = (this.client.window.getScaledHeight() - 256) / 2;
        this.client.getTextureManager().bindTexture(LOGO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, h);
        this.blit(k, m, 0, 0, 256, 256);
        float n = this.reloadMonitor.getProgress();
        this.field_17770 = this.field_17770 * 0.95f + n * 0.050000012f;
        if (f < 1.0f) {
            this.renderProgressBar(i / 2 - 150, j / 4 * 3, i / 2 + 150, j / 4 * 3 + 10, this.field_17770, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
        }
        if (f >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.field_17771 == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.field_18219 || g >= 2.0f)) {
            this.reloadMonitor.throwExceptions();
            this.field_17771 = Util.getMeasuringTimeMs();
            this.field_18218.run();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.window.getScaledWidth(), this.client.window.getScaledHeight());
            }
        }
    }

    private void renderProgressBar(int minX, int minY, int maxX, int maxY, float progress, float fadeAmount) {
        int i = MathHelper.ceil((float)(maxX - minX - 2) * progress);
        SplashScreen.fill(minX - 1, minY - 1, maxX + 1, maxY + 1, 0xFF000000 | Math.round((1.0f - fadeAmount) * 255.0f) << 16 | Math.round((1.0f - fadeAmount) * 255.0f) << 8 | Math.round((1.0f - fadeAmount) * 255.0f));
        SplashScreen.fill(minX, minY, maxX, maxY, -1);
        SplashScreen.fill(minX + 1, minY + 1, minX + i, maxY - 1, 0xFF000000 | (int)MathHelper.lerp(1.0f - fadeAmount, 226.0f, 255.0f) << 16 | (int)MathHelper.lerp(1.0f - fadeAmount, 40.0f, 255.0f) << 8 | (int)MathHelper.lerp(1.0f - fadeAmount, 55.0f, 255.0f));
    }

    @Override
    public boolean pausesGame() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4070
    extends ResourceTexture {
        public class_4070() {
            super(LOGO);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackDownloader().getPack();
            try (InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, LOGO);){
                ResourceTexture.TextureData textureData = new ResourceTexture.TextureData(null, NativeImage.read(inputStream));
                return textureData;
            }
            catch (IOException iOException) {
                return new ResourceTexture.TextureData(iOException);
            }
        }
    }
}

