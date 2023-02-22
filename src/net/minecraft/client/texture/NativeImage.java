/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBIWriteCallbackI
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Untracker;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class NativeImage
implements AutoCloseable {
    private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean isStbImage;
    private long pointer;
    private final int sizeBytes;

    public NativeImage(int width, int height, boolean useStb) {
        this(Format.RGBA, width, height, useStb);
    }

    public NativeImage(Format format, int width, int height, boolean useStb) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.sizeBytes = width * height * format.getChannelCount();
        this.isStbImage = false;
        this.pointer = useStb ? MemoryUtil.nmemCalloc((long)1L, (long)this.sizeBytes) : MemoryUtil.nmemAlloc((long)this.sizeBytes);
    }

    private NativeImage(Format format, int width, int height, boolean useStb, long pointer) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.isStbImage = useStb;
        this.pointer = pointer;
        this.sizeBytes = width * height * format.getChannelCount();
    }

    public String toString() {
        return "NativeImage[" + (Object)((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }

    public static NativeImage read(InputStream inputStream) throws IOException {
        return NativeImage.read(Format.RGBA, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(format, byteBuffer);
            return nativeImage;
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
            IOUtils.closeQuietly((InputStream)inputStream);
        }
    }

    public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
        return NativeImage.read(Format.RGBA, byteBuffer);
    }

    public static NativeImage read(@Nullable Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to read format " + (Object)((Object)format));
        }
        if (MemoryUtil.memAddress((ByteBuffer)byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)(format == null ? 0 : format.channelCount));
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage(format == null ? Format.getFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress((ByteBuffer)byteBuffer2));
            return nativeImage;
        }
    }

    private static void setTextureClamp(boolean clamp) {
        if (clamp) {
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
        } else {
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
        }
    }

    private static void setTextureFilter(boolean blur, boolean mipmap) {
        if (blur) {
            GlStateManager.texParameter(3553, 10241, mipmap ? 9987 : 9729);
            GlStateManager.texParameter(3553, 10240, 9729);
        } else {
            GlStateManager.texParameter(3553, 10241, mipmap ? 9986 : 9728);
            GlStateManager.texParameter(3553, 10240, 9728);
        }
    }

    private void checkAllocated() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            if (this.isStbImage) {
                STBImage.nstbi_image_free((long)this.pointer);
            } else {
                MemoryUtil.nmemFree((long)this.pointer);
            }
        }
        this.pointer = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format getFormat() {
        return this.format;
    }

    public int getPixelRgba(int x, int y) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        return MemoryUtil.memIntBuffer((long)this.pointer, (int)this.sizeBytes).get(x + y * this.width);
    }

    public void setPixelRgba(int x, int y, int color) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        MemoryUtil.memIntBuffer((long)this.pointer, (int)this.sizeBytes).put(x + y * this.width, color);
    }

    public byte getPixelOpacity(int x, int y) {
        if (!this.format.hasOpacityChannel()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        return MemoryUtil.memByteBuffer((long)this.pointer, (int)this.sizeBytes).get((x + y * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8);
    }

    public void blendPixel(int x, int y, int radius) {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        int i = this.getPixelRgba(x, y);
        float f = (float)(radius >> 24 & 0xFF) / 255.0f;
        float g = (float)(radius >> 16 & 0xFF) / 255.0f;
        float h = (float)(radius >> 8 & 0xFF) / 255.0f;
        float j = (float)(radius >> 0 & 0xFF) / 255.0f;
        float k = (float)(i >> 24 & 0xFF) / 255.0f;
        float l = (float)(i >> 16 & 0xFF) / 255.0f;
        float m = (float)(i >> 8 & 0xFF) / 255.0f;
        float n = (float)(i >> 0 & 0xFF) / 255.0f;
        float o = f;
        float p = 1.0f - f;
        float q = f * o + k * p;
        float r = g * o + l * p;
        float s = h * o + m * p;
        float t = j * o + n * p;
        if (q > 1.0f) {
            q = 1.0f;
        }
        if (r > 1.0f) {
            r = 1.0f;
        }
        if (s > 1.0f) {
            s = 1.0f;
        }
        if (t > 1.0f) {
            t = 1.0f;
        }
        int u = (int)(q * 255.0f);
        int v = (int)(r * 255.0f);
        int w = (int)(s * 255.0f);
        int z = (int)(t * 255.0f);
        this.setPixelRgba(x, y, u << 24 | v << 16 | w << 8 | z << 0);
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] is = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                int p;
                int k = this.getPixelRgba(j, i);
                int l = k >> 24 & 0xFF;
                int m = k >> 16 & 0xFF;
                int n = k >> 8 & 0xFF;
                int o = k >> 0 & 0xFF;
                is[j + i * this.getWidth()] = p = l << 24 | o << 16 | n << 8 | m;
            }
        }
        return is;
    }

    public void upload(int level, int offsetX, int offsetY, boolean mipmap) {
        this.upload(level, offsetX, offsetY, 0, 0, this.width, this.height, mipmap);
    }

    public void upload(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean mipmap) {
        this.upload(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, width, height, false, false, mipmap);
    }

    public void upload(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, boolean clamp, boolean mipmap) {
        this.checkAllocated();
        NativeImage.setTextureFilter(blur, mipmap);
        NativeImage.setTextureClamp(clamp);
        if (width == this.getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        } else {
            GlStateManager.pixelStore(3314, this.getWidth());
        }
        GlStateManager.pixelStore(3316, unpackSkipPixels);
        GlStateManager.pixelStore(3315, unpackSkipRows);
        this.format.setUnpackAlignment();
        GlStateManager.texSubImage2D(3553, level, xOffset, yOffset, width, height, this.format.getPixelDataFormat(), 5121, this.pointer);
    }

    public void loadFromTextureImage(int level, boolean removeAlpha) {
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager.getTexImage(3553, level, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (removeAlpha && this.format.hasAlphaChannel()) {
            for (int i = 0; i < this.getHeight(); ++i) {
                for (int j = 0; j < this.getWidth(); ++j) {
                    this.setPixelRgba(j, i, this.getPixelRgba(j, i) | 255 << this.format.getAlphaChannelOffset());
                }
            }
        }
    }

    public void loadFromMemory(boolean removeAlpha) {
        this.checkAllocated();
        this.format.setPackAlignment();
        if (removeAlpha) {
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
        }
        GlStateManager.readPixels(0, 0, this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (removeAlpha) {
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
    }

    public void writeFile(String fileName) throws IOException {
        this.writeFile(FileSystems.getDefault().getPath(fileName, new String[0]));
    }

    public void writeFile(File file) throws IOException {
        this.writeFile(file.toPath());
    }

    public void makeGlyphBitmapSubpixel(STBTTFontinfo fontInfo, int glyphIndex, int width, int height, float scaleX, float scaleY, float shiftX, float shiftY, int startX, int startY) {
        if (startX < 0 || startX + width > this.getWidth() || startY < 0 || startY + height > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", startX, startY, width, height, this.getWidth(), this.getHeight()));
        }
        if (this.format.getChannelCount() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel((long)fontInfo.address(), (long)(this.pointer + (long)startX + (long)(startY * this.getWidth())), (int)width, (int)height, (int)this.getWidth(), (float)scaleX, (float)scaleY, (float)shiftX, (float)shiftY, (int)glyphIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeFile(Path path) throws IOException {
        if (!this.format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to write format " + (Object)((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0]);){
            WriteCallback writeCallback = new WriteCallback(writableByteChannel);
            try {
                if (!STBImageWrite.stbi_write_png_to_func((STBIWriteCallbackI)writeCallback, (long)0L, (int)this.getWidth(), (int)this.getHeight(), (int)this.format.getChannelCount(), (ByteBuffer)MemoryUtil.memByteBuffer((long)this.pointer, (int)this.sizeBytes), (int)0)) {
                    throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
                }
            }
            finally {
                writeCallback.free();
            }
            writeCallback.throwStoredException();
        }
    }

    public void copyFrom(NativeImage image) {
        if (image.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int i = this.format.getChannelCount();
        this.checkAllocated();
        image.checkAllocated();
        if (this.width == image.width) {
            MemoryUtil.memCopy((long)image.pointer, (long)this.pointer, (long)Math.min(this.sizeBytes, image.sizeBytes));
        } else {
            int j = Math.min(this.getWidth(), image.getWidth());
            int k = Math.min(this.getHeight(), image.getHeight());
            for (int l = 0; l < k; ++l) {
                int m = l * image.getWidth() * i;
                int n = l * this.getWidth() * i;
                MemoryUtil.memCopy((long)(image.pointer + (long)m), (long)(this.pointer + (long)n), (long)j);
            }
        }
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        for (int i = y; i < y + height; ++i) {
            for (int j = x; j < x + width; ++j) {
                this.setPixelRgba(j, i, color);
            }
        }
    }

    public void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int k = flipX ? width - 1 - j : j;
                int l = flipY ? height - 1 - i : i;
                int m = this.getPixelRgba(x + j, y + i);
                this.setPixelRgba(x + translateX + k, y + translateY + l, m);
            }
        }
    }

    public void method_4319() {
        this.checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = this.format.getChannelCount();
            int j = this.getWidth() * i;
            long l = memoryStack.nmalloc(j);
            for (int k = 0; k < this.getHeight() / 2; ++k) {
                int m = k * this.getWidth() * i;
                int n = (this.getHeight() - 1 - k) * this.getWidth() * i;
                MemoryUtil.memCopy((long)(this.pointer + (long)m), (long)l, (long)j);
                MemoryUtil.memCopy((long)(this.pointer + (long)n), (long)(this.pointer + (long)m), (long)j);
                MemoryUtil.memCopy((long)l, (long)(this.pointer + (long)n), (long)j);
            }
        }
    }

    public void resizeSubRectTo(int x, int y, int width, int height, NativeImage targetImage) {
        this.checkAllocated();
        if (targetImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int i = this.format.getChannelCount();
        STBImageResize.nstbir_resize_uint8((long)(this.pointer + (long)((x + y * this.getWidth()) * i)), (int)width, (int)height, (int)(this.getWidth() * i), (long)targetImage.pointer, (int)targetImage.getWidth(), (int)targetImage.getHeight(), (int)0, (int)i);
    }

    public void untrack() {
        Untracker.untrack(this.pointer);
    }

    public static NativeImage read(String dataUri) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.UTF8((CharSequence)dataUri.replaceAll("\n", ""), false);
            ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
            ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
            byteBuffer3.put(byteBuffer2);
            byteBuffer3.rewind();
            NativeImage nativeImage = NativeImage.read(byteBuffer3);
            return nativeImage;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        private final int channelCount;
        private final int pixelDataFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceChannelOffset;
        private final int alphaChannelOffset;
        private final boolean writeable;

        private Format(int channels, int glFormat, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasLuminance, boolean hasAlpha, int redOffset, int greenOffset, int blueOffset, int luminanceOffset, int alphaOffset, boolean writeable) {
            this.channelCount = channels;
            this.pixelDataFormat = glFormat;
            this.hasRed = hasRed;
            this.hasGreen = hasGreen;
            this.hasBlue = hasBlue;
            this.hasLuminance = hasLuminance;
            this.hasAlpha = hasAlpha;
            this.redOffset = redOffset;
            this.greenOffset = greenOffset;
            this.blueOffset = blueOffset;
            this.luminanceChannelOffset = luminanceOffset;
            this.alphaChannelOffset = alphaOffset;
            this.writeable = writeable;
        }

        public int getChannelCount() {
            return this.channelCount;
        }

        public void setPackAlignment() {
            GlStateManager.pixelStore(3333, this.getChannelCount());
        }

        public void setUnpackAlignment() {
            GlStateManager.pixelStore(3317, this.getChannelCount());
        }

        public int getPixelDataFormat() {
            return this.pixelDataFormat;
        }

        public boolean hasAlphaChannel() {
            return this.hasAlpha;
        }

        public int getAlphaChannelOffset() {
            return this.alphaChannelOffset;
        }

        public boolean hasOpacityChannel() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int getOpacityOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
        }

        public boolean isWriteable() {
            return this.writeable;
        }

        private static Format getFormat(int glFormat) {
            switch (glFormat) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GLFormat {
        RGBA(6408),
        RGB(6407),
        LUMINANCE_ALPHA(6410),
        LUMINANCE(6409),
        INTENSITY(32841);

        private final int glConstant;

        private GLFormat(int glConstant) {
            this.glConstant = glConstant;
        }

        public int getGlConstant() {
            return this.glConstant;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel channel;
        private IOException exception;

        private WriteCallback(WritableByteChannel channel) {
            this.channel = channel;
        }

        public void invoke(long context, long data, int size) {
            ByteBuffer byteBuffer = WriteCallback.getData((long)data, (int)size);
            try {
                this.channel.write(byteBuffer);
            }
            catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwStoredException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

