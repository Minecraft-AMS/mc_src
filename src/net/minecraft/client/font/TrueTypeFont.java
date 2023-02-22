/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    private final ByteBuffer buffer;
    final STBTTFontinfo info;
    final float oversample;
    private final IntSet excludedCharacters = new IntArraySet();
    final float shiftX;
    final float shiftY;
    final float scaleFactor;
    final float ascent;

    public TrueTypeFont(ByteBuffer buffer, STBTTFontinfo info, float f, float oversample, float g, float h, String excludedCharacters) {
        this.buffer = buffer;
        this.info = info;
        this.oversample = oversample;
        excludedCharacters.codePoints().forEach(arg_0 -> ((IntSet)this.excludedCharacters).add(arg_0));
        this.shiftX = g * oversample;
        this.shiftY = h * oversample;
        this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight((STBTTFontinfo)info, (float)(f * oversample));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics((STBTTFontinfo)info, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3);
            this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
        }
    }

    @Override
    @Nullable
    public TtfGlyph getGlyph(int i) {
        if (this.excludedCharacters.contains(i)) {
            return null;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            IntBuffer intBuffer4 = memoryStack.mallocInt(1);
            int j = STBTruetype.stbtt_FindGlyphIndex((STBTTFontinfo)this.info, (int)i);
            if (j == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel((STBTTFontinfo)this.info, (int)j, (float)this.scaleFactor, (float)this.scaleFactor, (float)this.shiftX, (float)this.shiftY, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (IntBuffer)intBuffer4);
            int k = intBuffer3.get(0) - intBuffer.get(0);
            int l = intBuffer4.get(0) - intBuffer2.get(0);
            if (k <= 0 || l <= 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            IntBuffer intBuffer5 = memoryStack.mallocInt(1);
            IntBuffer intBuffer6 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics((STBTTFontinfo)this.info, (int)j, (IntBuffer)intBuffer5, (IntBuffer)intBuffer6);
            TtfGlyph ttfGlyph = new TtfGlyph(intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), (float)intBuffer5.get(0) * this.scaleFactor, (float)intBuffer6.get(0) * this.scaleFactor, j);
            return ttfGlyph;
        }
    }

    @Override
    public void close() {
        this.info.free();
        MemoryUtil.memFree((Buffer)this.buffer);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return (IntSet)IntStream.range(0, 65535).filter(codePoint -> !this.excludedCharacters.contains(codePoint)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    @Override
    @Nullable
    public /* synthetic */ RenderableGlyph getGlyph(int codePoint) {
        return this.getGlyph(codePoint);
    }

    @Environment(value=EnvType.CLIENT)
    class TtfGlyph
    implements RenderableGlyph {
        private final int width;
        private final int height;
        private final float bearingX;
        private final float ascent;
        private final float advance;
        private final int glyphIndex;

        TtfGlyph(int i, int j, int k, int l, float f, float g, int m) {
            this.width = j - i;
            this.height = k - l;
            this.advance = f / TrueTypeFont.this.oversample;
            this.bearingX = (g + (float)i + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
            this.ascent = (TrueTypeFont.this.ascent - (float)k + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
            this.glyphIndex = m;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public float getOversample() {
            return TrueTypeFont.this.oversample;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public float getBearingX() {
            return this.bearingX;
        }

        @Override
        public float getAscent() {
            return this.ascent;
        }

        @Override
        public void upload(int x, int y) {
            NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
            nativeImage.makeGlyphBitmapSubpixel(TrueTypeFont.this.info, this.glyphIndex, this.width, this.height, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.shiftX, TrueTypeFont.this.shiftY, 0, 0);
            nativeImage.upload(0, x, y, 0, 0, this.width, this.height, false, true);
        }

        @Override
        public boolean hasColor() {
            return false;
        }
    }
}

