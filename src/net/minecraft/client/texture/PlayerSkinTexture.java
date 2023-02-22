/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinTexture
extends ResourceTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WIDTH = 64;
    private static final int HEIGHT = 64;
    private static final int OLD_HEIGHT = 32;
    @Nullable
    private final File cacheFile;
    private final String url;
    private final boolean convertLegacy;
    @Nullable
    private final Runnable loadedCallback;
    @Nullable
    private CompletableFuture<?> loader;
    private boolean loaded;

    public PlayerSkinTexture(@Nullable File cacheFile, String url, Identifier fallbackSkin, boolean convertLegacy, @Nullable Runnable callback) {
        super(fallbackSkin);
        this.cacheFile = cacheFile;
        this.url = url;
        this.convertLegacy = convertLegacy;
        this.loadedCallback = callback;
    }

    private void onTextureLoaded(NativeImage image) {
        if (this.loadedCallback != null) {
            this.loadedCallback.run();
        }
        MinecraftClient.getInstance().execute(() -> {
            this.loaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.uploadTexture(image));
            } else {
                this.uploadTexture(image);
            }
        });
    }

    private void uploadTexture(NativeImage image) {
        TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, true);
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        NativeImage nativeImage;
        MinecraftClient.getInstance().execute(() -> {
            if (!this.loaded) {
                try {
                    super.load(manager);
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to load texture: {}", (Object)this.location, (Object)iOException);
                }
                this.loaded = true;
            }
        });
        if (this.loader != null) {
            return;
        }
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
            FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
            nativeImage = this.loadTexture(fileInputStream);
        } else {
            nativeImage = null;
        }
        if (nativeImage != null) {
            this.onTextureLoaded(nativeImage);
            return;
        }
        this.loader = CompletableFuture.runAsync(() -> {
            HttpURLConnection httpURLConnection = null;
            LOGGER.debug("Downloading http texture from {} to {}", (Object)this.url, (Object)this.cacheFile);
            try {
                InputStream inputStream;
                httpURLConnection = (HttpURLConnection)new URL(this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 != 2) {
                    return;
                }
                if (this.cacheFile != null) {
                    FileUtils.copyInputStreamToFile((InputStream)httpURLConnection.getInputStream(), (File)this.cacheFile);
                    inputStream = new FileInputStream(this.cacheFile);
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
                MinecraftClient.getInstance().execute(() -> {
                    NativeImage nativeImage = this.loadTexture(inputStream);
                    if (nativeImage != null) {
                        this.onTextureLoaded(nativeImage);
                    }
                });
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't download http texture", (Throwable)exception);
            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }, Util.getMainWorkerExecutor());
    }

    @Nullable
    private NativeImage loadTexture(InputStream stream) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(stream);
            if (this.convertLegacy) {
                nativeImage = this.remapTexture(nativeImage);
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Error while loading the skin texture", (Throwable)exception);
        }
        return nativeImage;
    }

    @Nullable
    private NativeImage remapTexture(NativeImage nativeImage) {
        boolean bl;
        int i = nativeImage.getHeight();
        int j = nativeImage.getWidth();
        if (j != 64 || i != 32 && i != 64) {
            nativeImage.close();
            LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", (Object)j, (Object)i, (Object)this.url);
            return null;
        }
        boolean bl2 = bl = i == 32;
        if (bl) {
            NativeImage nativeImage2 = new NativeImage(64, 64, true);
            nativeImage2.copyFrom(nativeImage);
            nativeImage.close();
            nativeImage = nativeImage2;
            nativeImage.fillRect(0, 32, 64, 32, 0);
            nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        PlayerSkinTexture.stripAlpha(nativeImage, 0, 0, 32, 16);
        if (bl) {
            PlayerSkinTexture.stripColor(nativeImage, 32, 0, 64, 32);
        }
        PlayerSkinTexture.stripAlpha(nativeImage, 0, 16, 64, 32);
        PlayerSkinTexture.stripAlpha(nativeImage, 16, 48, 48, 64);
        return nativeImage;
    }

    private static void stripColor(NativeImage image, int x1, int y1, int x2, int y2) {
        int j;
        int i;
        for (i = x1; i < x2; ++i) {
            for (j = y1; j < y2; ++j) {
                int k = image.getColor(i, j);
                if ((k >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (i = x1; i < x2; ++i) {
            for (j = y1; j < y2; ++j) {
                image.setColor(i, j, image.getColor(i, j) & 0xFFFFFF);
            }
        }
    }

    private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
        for (int i = x1; i < x2; ++i) {
            for (int j = y1; j < y2; ++j) {
                image.setColor(i, j, image.getColor(i, j) | 0xFF000000);
            }
        }
    }
}

