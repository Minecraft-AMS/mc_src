/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TextureUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static int generateTextureId() {
        return GlStateManager.genTexture();
    }

    public static void releaseTextureId(int i) {
        GlStateManager.deleteTexture(i);
    }

    public static void prepareImage(int texture, int width, int height) {
        TextureUtil.prepareImage(NativeImage.GLFormat.RGBA, texture, 0, width, height);
    }

    public static void prepareImage(NativeImage.GLFormat pixelFormat, int texture, int width, int height) {
        TextureUtil.prepareImage(pixelFormat, texture, 0, width, height);
    }

    public static void prepareImage(int texture, int levels, int width, int height) {
        TextureUtil.prepareImage(NativeImage.GLFormat.RGBA, texture, levels, width, height);
    }

    public static void prepareImage(NativeImage.GLFormat pixelFormat, int texture, int levels, int width, int height) {
        TextureUtil.bind(texture);
        if (levels >= 0) {
            GlStateManager.texParameter(3553, 33085, levels);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, levels);
            GlStateManager.texParameter(3553, 34049, 0.0f);
        }
        for (int i = 0; i <= levels; ++i) {
            GlStateManager.texImage2D(3553, i, pixelFormat.getGlConstant(), width >> i, height >> i, 0, 6408, 5121, null);
        }
    }

    private static void bind(int texture) {
        GlStateManager.bindTexture(texture);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String readResourceAsString(InputStream inputStream) {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            int i = byteBuffer.position();
            byteBuffer.rewind();
            String string = MemoryUtil.memASCII((ByteBuffer)byteBuffer, (int)i);
            return string;
        }
        catch (IOException iOException) {
        }
        finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
        return null;
    }

    public static void writeAsPNG(String string, int i, int j, int k, int l) {
        TextureUtil.bind(i);
        for (int m = 0; m <= j; ++m) {
            String string2 = string + "_" + m + ".png";
            int n = k >> m;
            int o = l >> m;
            try (NativeImage nativeImage = new NativeImage(n, o, false);){
                nativeImage.loadFromTextureImage(m, false);
                nativeImage.writeFile(string2);
                LOGGER.debug("Exported png to: {}", (Object)new File(string2).getAbsolutePath());
                continue;
            }
            catch (IOException iOException) {
                LOGGER.debug("Unable to write: ", (Throwable)iOException);
            }
        }
    }

    public static void initTexture(IntBuffer intBuffer, int i, int j) {
        GL11.glPixelStorei((int)3312, (int)0);
        GL11.glPixelStorei((int)3313, (int)0);
        GL11.glPixelStorei((int)3314, (int)0);
        GL11.glPixelStorei((int)3315, (int)0);
        GL11.glPixelStorei((int)3316, (int)0);
        GL11.glPixelStorei((int)3317, (int)4);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)i, (int)j, (int)0, (int)32993, (int)33639, (IntBuffer)intBuffer);
        GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
    }
}

