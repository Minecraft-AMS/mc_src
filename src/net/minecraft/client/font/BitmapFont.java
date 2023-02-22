/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BitmapFont
implements Font {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NativeImage image;
    private final Int2ObjectMap<BitmapFontGlyph> glyphs;

    private BitmapFont(NativeImage image, Int2ObjectMap<BitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(int codePoint) {
        return (RenderableGlyph)this.glyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.keySet());
    }

    @Environment(value=EnvType.CLIENT)
    static final class BitmapFontGlyph
    implements RenderableGlyph {
        private final float scaleFactor;
        private final NativeImage image;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;

        private BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
            this.scaleFactor = scaleFactor;
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.advance = advance;
            this.ascent = ascent;
        }

        @Override
        public float getOversample() {
            return 1.0f / this.scaleFactor;
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
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public float getAscent() {
            return RenderableGlyph.super.getAscent() + 7.0f - (float)this.ascent;
        }

        @Override
        public void upload(int x, int y) {
            this.image.upload(0, x, y, this.x, this.y, this.width, this.height, false, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        private final Identifier filename;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Loader(Identifier id, int height, int ascent, List<int[]> chars) {
            this.filename = new Identifier(id.getNamespace(), "textures/" + id.getPath());
            this.chars = chars;
            this.height = height;
            this.ascent = ascent;
        }

        public static Loader fromJson(JsonObject json) {
            int i = JsonHelper.getInt(json, "height", 8);
            int j = JsonHelper.getInt(json, "ascent");
            if (j > i) {
                throw new JsonParseException("Ascent " + j + " higher than height " + i);
            }
            ArrayList list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(json, "chars");
            for (int k = 0; k < jsonArray.size(); ++k) {
                int l;
                String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
                int[] is = string.codePoints().toArray();
                if (k > 0 && is.length != (l = ((int[])list.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + is.length + ", expected: " + l + "), pad with space or \\u0000");
                }
                list.add(is);
            }
            if (list.isEmpty() || ((int[])list.get(0)).length == 0) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Loader(new Identifier(JsonHelper.getString(json, "file")), i, j, list);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager manager) {
            try (Resource resource = manager.getResource(this.filename);){
                NativeImage nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());
                int i = nativeImage.getWidth();
                int j = nativeImage.getHeight();
                int k = i / this.chars.get(0).length;
                int l = j / this.chars.size();
                float f = (float)this.height / (float)l;
                Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
                int m = 0;
                while (true) {
                    int n;
                    int[] nArray;
                    int n2;
                    if (m < this.chars.size()) {
                        n2 = 0;
                        nArray = this.chars.get(m);
                        n = nArray.length;
                    } else {
                        BitmapFont bitmapFont = new BitmapFont(nativeImage, (Int2ObjectMap)int2ObjectMap);
                        return bitmapFont;
                    }
                    for (int i2 = 0; i2 < n; ++i2) {
                        int q;
                        BitmapFontGlyph bitmapFontGlyph;
                        int o = nArray[i2];
                        int p = n2++;
                        if (o == 0 || o == 32 || (bitmapFontGlyph = (BitmapFontGlyph)int2ObjectMap.put(o, (Object)new BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(nativeImage, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
                        LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)this.filename);
                    }
                    ++m;
                }
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException.getMessage());
            }
        }

        private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
            int i;
            for (i = characterWidth - 1; i >= 0; --i) {
                int j = charPosX * characterWidth + i;
                for (int k = 0; k < characterHeight; ++k) {
                    int l = charPosY * characterHeight + k;
                    if (image.getPixelOpacity(j, l) == 0) continue;
                    return i + 1;
                }
            }
            return i + 1;
        }
    }
}

