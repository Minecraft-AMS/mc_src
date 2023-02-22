/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.CharArraySet
 *  it.unimi.dsi.fastutil.chars.CharSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
    private final ByteBuffer field_21839;
    private final STBTTFontinfo info;
    private final float oversample;
    private final CharSet excludedCharacters = new CharArraySet();
    private final float shiftX;
    private final float shiftY;
    private final float scaleFactor;
    private final float ascent;

    public TrueTypeFont(ByteBuffer byteBuffer, STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i2, String string) {
        this.field_21839 = byteBuffer;
        this.info = sTBTTFontinfo;
        this.oversample = g;
        string.chars().forEach(i -> this.excludedCharacters.add((char)(i & 0xFFFF)));
        this.shiftX = h * g;
        this.shiftY = i2 * g;
        this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight((STBTTFontinfo)sTBTTFontinfo, (float)(f * g));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics((STBTTFontinfo)sTBTTFontinfo, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3);
            this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
        }
    }

    @Override
    @Nullable
    public TtfGlyph getGlyph(char c) {
        if (this.excludedCharacters.contains(c)) {
            return null;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            IntBuffer intBuffer4 = memoryStack.mallocInt(1);
            int i = STBTruetype.stbtt_FindGlyphIndex((STBTTFontinfo)this.info, (int)c);
            if (i == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel((STBTTFontinfo)this.info, (int)i, (float)this.scaleFactor, (float)this.scaleFactor, (float)this.shiftX, (float)this.shiftY, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (IntBuffer)intBuffer4);
            int j = intBuffer3.get(0) - intBuffer.get(0);
            int k = intBuffer4.get(0) - intBuffer2.get(0);
            if (j == 0 || k == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            IntBuffer intBuffer5 = memoryStack.mallocInt(1);
            IntBuffer intBuffer6 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics((STBTTFontinfo)this.info, (int)i, (IntBuffer)intBuffer5, (IntBuffer)intBuffer6);
            TtfGlyph ttfGlyph = new TtfGlyph(intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), (float)intBuffer5.get(0) * this.scaleFactor, (float)intBuffer6.get(0) * this.scaleFactor, i);
            return ttfGlyph;
        }
    }

    @Override
    public void close() {
        this.info.free();
        MemoryUtil.memFree((Buffer)this.field_21839);
    }

    @Override
    @Nullable
    public /* synthetic */ RenderableGlyph getGlyph(char character) {
        return this.getGlyph(character);
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

        private TtfGlyph(int xMin, int xMax, int yMax, int yMin, float advance, float bearing, int index) {
            this.width = xMax - xMin;
            this.height = yMax - yMin;
            this.advance = advance / TrueTypeFont.this.oversample;
            this.bearingX = (bearing + (float)xMin + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
            this.ascent = (TrueTypeFont.this.ascent - (float)yMax + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
            this.glyphIndex = index;
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

