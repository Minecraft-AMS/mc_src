/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class TextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static int generateTextureId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (SharedConstants.isDevelopment) {
            int[] is = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManager._genTextures(is);
            int i = GlStateManager._genTexture();
            GlStateManager._deleteTextures(is);
            return i;
        }
        return GlStateManager._genTexture();
    }

    public static void releaseTextureId(int id) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._deleteTexture(id);
    }

    public static void prepareImage(int id, int width, int height) {
        TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, 0, width, height);
    }

    public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int width, int height) {
        TextureUtil.prepareImage(internalFormat, id, 0, width, height);
    }

    public static void prepareImage(int id, int maxLevel, int width, int height) {
        TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, maxLevel, width, height);
    }

    public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int maxLevel, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        TextureUtil.bind(id);
        if (maxLevel >= 0) {
            GlStateManager._texParameter(3553, 33085, maxLevel);
            GlStateManager._texParameter(3553, 33082, 0);
            GlStateManager._texParameter(3553, 33083, maxLevel);
            GlStateManager._texParameter(3553, 34049, 0.0f);
        }
        for (int i = 0; i <= maxLevel; ++i) {
            GlStateManager._texImage2D(3553, i, internalFormat.getValue(), width >> i, height >> i, 0, 6408, 5121, null);
        }
    }

    private static void bind(int id) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._bindTexture(id);
    }

    public static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer;
        if (inputStream instanceof FileInputStream) {
            FileInputStream fileInputStream = (FileInputStream)inputStream;
            FileChannel fileChannel = fileInputStream.getChannel();
            byteBuffer = MemoryUtil.memAlloc((int)((int)fileChannel.size() + 1));
            while (fileChannel.read(byteBuffer) != -1) {
            }
        } else {
            byteBuffer = MemoryUtil.memAlloc((int)8192);
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
            while (readableByteChannel.read(byteBuffer) != -1) {
                if (byteBuffer.remaining() != 0) continue;
                byteBuffer = MemoryUtil.memRealloc((ByteBuffer)byteBuffer, (int)(byteBuffer.capacity() * 2));
            }
        }
        return byteBuffer;
    }

    public static void writeAsPNG(Path directory, String prefix, int textureId, int scales, int width, int height) {
        RenderSystem.assertOnRenderThread();
        TextureUtil.bind(textureId);
        for (int i = 0; i <= scales; ++i) {
            int j = width >> i;
            int k = height >> i;
            try (NativeImage nativeImage = new NativeImage(j, k, false);){
                nativeImage.loadFromTextureImage(i, false);
                Path path = directory.resolve(prefix + "_" + i + ".png");
                nativeImage.writeTo(path);
                LOGGER.debug("Exported png to: {}", (Object)path.toAbsolutePath());
                continue;
            }
            catch (IOException iOException) {
                LOGGER.debug("Unable to write: ", (Throwable)iOException);
            }
        }
    }

    public static void initTexture(IntBuffer imageData, int width, int height) {
        RenderSystem.assertOnRenderThread();
        GL11.glPixelStorei((int)3312, (int)0);
        GL11.glPixelStorei((int)3313, (int)0);
        GL11.glPixelStorei((int)3314, (int)0);
        GL11.glPixelStorei((int)3315, (int)0);
        GL11.glPixelStorei((int)3316, (int)0);
        GL11.glPixelStorei((int)3317, (int)4);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)width, (int)height, (int)0, (int)32993, (int)33639, (IntBuffer)imageData);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
    }

    public static Path getDebugTexturePath(Path path) {
        return path.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath(Path.of(".", new String[0]));
    }
}

