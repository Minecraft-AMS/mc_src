/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.SkinProcessor;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> textures = Maps.newHashMap();
    private static final Map<String, Boolean> skinFetchStatus = Maps.newHashMap();
    private static final Map<String, String> fetchedSkins = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();

    public static void bindWorldTemplate(String id, String image) {
        if (image == null) {
            RealmsScreen.bind("textures/gui/presets/isles.png");
            return;
        }
        int i = RealmsTextureManager.getTextureId(id, image);
        RenderSystem.bindTexture(i);
    }

    public static void withBoundFace(String uuid, Runnable r) {
        RenderSystem.pushTextureAttributes();
        try {
            RealmsTextureManager.bindFace(uuid);
            r.run();
        }
        finally {
            RenderSystem.popAttributes();
        }
    }

    private static void bindDefaultFace(UUID uuid) {
        RealmsScreen.bind((uuid.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
    }

    private static void bindFace(final String uuid) {
        UUID uUID = UUIDTypeAdapter.fromString((String)uuid);
        if (textures.containsKey(uuid)) {
            RenderSystem.bindTexture(RealmsTextureManager.textures.get((Object)uuid).textureId);
            return;
        }
        if (skinFetchStatus.containsKey(uuid)) {
            if (!skinFetchStatus.get(uuid).booleanValue()) {
                RealmsTextureManager.bindDefaultFace(uUID);
            } else if (fetchedSkins.containsKey(uuid)) {
                int i = RealmsTextureManager.getTextureId(uuid, fetchedSkins.get(uuid));
                RenderSystem.bindTexture(i);
            } else {
                RealmsTextureManager.bindDefaultFace(uUID);
            }
            return;
        }
        skinFetchStatus.put(uuid, false);
        RealmsTextureManager.bindDefaultFace(uUID);
        Thread thread = new Thread("Realms Texture Downloader"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                block17: {
                    block16: {
                        ByteArrayOutputStream byteArrayOutputStream;
                        BufferedImage bufferedImage;
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(uuid);
                        if (!map.containsKey(MinecraftProfileTexture.Type.SKIN)) break block16;
                        MinecraftProfileTexture minecraftProfileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
                        String string = minecraftProfileTexture.getUrl();
                        HttpURLConnection httpURLConnection = null;
                        LOGGER.debug("Downloading http texture from {}", (Object)string);
                        try {
                            httpURLConnection = (HttpURLConnection)new URL(string).openConnection(Realms.getProxy());
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 != 2) {
                                skinFetchStatus.remove(uuid);
                                return;
                            }
                            try {
                                bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
                            }
                            catch (Exception exception) {
                                skinFetchStatus.remove(uuid);
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                return;
                            }
                            finally {
                                IOUtils.closeQuietly((InputStream)httpURLConnection.getInputStream());
                            }
                            bufferedImage = new SkinProcessor().process(bufferedImage);
                            byteArrayOutputStream = new ByteArrayOutputStream();
                        }
                        catch (Exception exception2) {
                            LOGGER.error("Couldn't download http texture", (Throwable)exception2);
                            skinFetchStatus.remove(uuid);
                        }
                        finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }
                        ImageIO.write((RenderedImage)bufferedImage, "png", byteArrayOutputStream);
                        fetchedSkins.put(uuid, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
                        skinFetchStatus.put(uuid, true);
                        break block17;
                    }
                    skinFetchStatus.put(uuid, true);
                    return;
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int getTextureId(String id, String image) {
        int i;
        if (textures.containsKey(id)) {
            RealmsTexture realmsTexture = textures.get(id);
            if (realmsTexture.image.equals(image)) {
                return realmsTexture.textureId;
            }
            RenderSystem.deleteTexture(realmsTexture.textureId);
            i = realmsTexture.textureId;
        } else {
            i = GlStateManager.getTexLevelParameter();
        }
        IntBuffer intBuffer = null;
        int j = 0;
        int k = 0;
        try {
            BufferedImage bufferedImage;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new Base64().decode(image));
            try {
                bufferedImage = ImageIO.read(inputStream);
            }
            finally {
                IOUtils.closeQuietly((InputStream)inputStream);
            }
            j = bufferedImage.getWidth();
            k = bufferedImage.getHeight();
            int[] is = new int[j * k];
            bufferedImage.getRGB(0, 0, j, k, is, 0, j);
            intBuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
            intBuffer.put(is);
            intBuffer.flip();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(i);
        TextureUtil.initTexture(intBuffer, j, k);
        textures.put(id, new RealmsTexture(image, i));
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class RealmsTexture {
        String image;
        int textureId;

        public RealmsTexture(String image, int textureId) {
            this.image = image;
            this.textureId = textureId;
        }
    }
}

