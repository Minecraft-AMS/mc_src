/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TextRenderer
implements AutoCloseable {
    public final int fontHeight = 9;
    public final Random random = new Random();
    private final TextureManager textureManager;
    private final FontStorage fontStorage;
    private boolean rightToLeft;

    public TextRenderer(TextureManager textureManager, FontStorage fontStorage) {
        this.textureManager = textureManager;
        this.fontStorage = fontStorage;
    }

    public void setFonts(List<Font> fonts) {
        this.fontStorage.setFonts(fonts);
    }

    @Override
    public void close() {
        this.fontStorage.close();
    }

    public int drawWithShadow(String text, float x, float y, int color) {
        GlStateManager.enableAlphaTest();
        return this.draw(text, x, y, color, true);
    }

    public int draw(String text, float x, float y, int color) {
        GlStateManager.enableAlphaTest();
        return this.draw(text, x, y, color, false);
    }

    public String mirror(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return text;
        }
    }

    private int draw(String str, float x, float y, int color, boolean withShadow) {
        if (str == null) {
            return 0;
        }
        if (this.rightToLeft) {
            str = this.mirror(str);
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (withShadow) {
            this.drawLayer(str, x, y, color, true);
        }
        x = this.drawLayer(str, x, y, color, false);
        return (int)x + (withShadow ? 1 : 0);
    }

    private float drawLayer(String str, float x, float y, int color, boolean isShadow) {
        float f = isShadow ? 0.25f : 1.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f * f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f * f;
        float i = (float)(color & 0xFF) / 255.0f * f;
        float j = g;
        float k = h;
        float l = i;
        float m = (float)(color >> 24 & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Identifier identifier = null;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        ArrayList list = Lists.newArrayList();
        for (int n = 0; n < str.length(); ++n) {
            float q;
            float p;
            char c = str.charAt(n);
            if (c == '\u00a7' && n + 1 < str.length()) {
                Formatting formatting = Formatting.byCode(str.charAt(n + 1));
                if (formatting != null) {
                    if (formatting.affectsGlyphWidth()) {
                        bl = false;
                        bl2 = false;
                        bl5 = false;
                        bl4 = false;
                        bl3 = false;
                        j = g;
                        k = h;
                        l = i;
                    }
                    if (formatting.getColorValue() != null) {
                        int o = formatting.getColorValue();
                        j = (float)(o >> 16 & 0xFF) / 255.0f * f;
                        k = (float)(o >> 8 & 0xFF) / 255.0f * f;
                        l = (float)(o & 0xFF) / 255.0f * f;
                    } else if (formatting == Formatting.OBFUSCATED) {
                        bl = true;
                    } else if (formatting == Formatting.BOLD) {
                        bl2 = true;
                    } else if (formatting == Formatting.STRIKETHROUGH) {
                        bl5 = true;
                    } else if (formatting == Formatting.UNDERLINE) {
                        bl4 = true;
                    } else if (formatting == Formatting.ITALIC) {
                        bl3 = true;
                    }
                }
                ++n;
                continue;
            }
            Glyph glyph = this.fontStorage.getGlyph(c);
            GlyphRenderer glyphRenderer = bl && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
            Identifier identifier2 = glyphRenderer.getId();
            if (identifier2 != null) {
                if (identifier != identifier2) {
                    tessellator.draw();
                    this.textureManager.bindTexture(identifier2);
                    bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                    identifier = identifier2;
                }
                p = bl2 ? glyph.getBoldOffset() : 0.0f;
                q = isShadow ? glyph.getShadowOffset() : 0.0f;
                this.drawGlyph(glyphRenderer, bl2, bl3, p, x + q, y + q, bufferBuilder, j, k, l, m);
            }
            p = glyph.getAdvance(bl2);
            float f2 = q = isShadow ? 1.0f : 0.0f;
            if (bl5) {
                list.add(new Rectangle(x + q - 1.0f, y + q + 4.5f, x + q + p, y + q + 4.5f - 1.0f, j, k, l, m));
            }
            if (bl4) {
                list.add(new Rectangle(x + q - 1.0f, y + q + 9.0f, x + q + p, y + q + 9.0f - 1.0f, j, k, l, m));
            }
            x += p;
        }
        tessellator.draw();
        if (!list.isEmpty()) {
            GlStateManager.disableTexture();
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            for (Rectangle rectangle : list) {
                rectangle.draw(bufferBuilder);
            }
            tessellator.draw();
            GlStateManager.enableTexture();
        }
        return x;
    }

    private void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean strikethrough, float boldOffset, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
        glyphRenderer.draw(this.textureManager, strikethrough, x, y, buffer, red, green, blue, alpha);
        if (bold) {
            glyphRenderer.draw(this.textureManager, strikethrough, x + boldOffset, y, buffer, red, green, blue, alpha);
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        float f = 0.0f;
        boolean bl = false;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i < text.length() - 1) {
                Formatting formatting;
                if ((formatting = Formatting.byCode(text.charAt(++i))) == Formatting.BOLD) {
                    bl = true;
                    continue;
                }
                if (formatting == null || !formatting.affectsGlyphWidth()) continue;
                bl = false;
                continue;
            }
            f += this.fontStorage.getGlyph(c).getAdvance(bl);
        }
        return MathHelper.ceil(f);
    }

    public float getCharWidth(char character) {
        if (character == '\u00a7') {
            return 0.0f;
        }
        return this.fontStorage.getGlyph(character).getAdvance(false);
    }

    public String trimToWidth(String text, int width) {
        return this.trimToWidth(text, width, false);
    }

    public String trimToWidth(String text, int width, boolean rightToLeft) {
        StringBuilder stringBuilder = new StringBuilder();
        float f = 0.0f;
        int i = rightToLeft ? text.length() - 1 : 0;
        int j = rightToLeft ? -1 : 1;
        boolean bl = false;
        boolean bl2 = false;
        for (int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
            char c = text.charAt(k);
            if (bl) {
                bl = false;
                Formatting formatting = Formatting.byCode(c);
                if (formatting == Formatting.BOLD) {
                    bl2 = true;
                } else if (formatting != null && formatting.affectsGlyphWidth()) {
                    bl2 = false;
                }
            } else if (c == '\u00a7') {
                bl = true;
            } else {
                f += this.getCharWidth(c);
                if (bl2) {
                    f += 1.0f;
                }
            }
            if (f > (float)width) break;
            if (rightToLeft) {
                stringBuilder.insert(0, c);
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private String trimEndNewlines(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public void drawTrimmed(String text, int x, int y, int maxWidth, int color) {
        text = this.trimEndNewlines(text);
        this.drawWrapped(text, x, y, maxWidth, color);
    }

    private void drawWrapped(String text, int x, int y, int maxWidth, int color) {
        List<String> list = this.wrapStringToWidthAsList(text, maxWidth);
        for (String string : list) {
            float f = x;
            if (this.rightToLeft) {
                int i = this.getStringWidth(this.mirror(string));
                f += (float)(maxWidth - i);
            }
            this.draw(string, f, y, color, false);
            y += 9;
        }
    }

    public int getStringBoundedHeight(String text, int maxWidth) {
        return 9 * this.wrapStringToWidthAsList(text, maxWidth).size();
    }

    public void setRightToLeft(boolean rightToLeft) {
        this.rightToLeft = rightToLeft;
    }

    public List<String> wrapStringToWidthAsList(String text, int width) {
        return Arrays.asList(this.wrapStringToWidth(text, width).split("\n"));
    }

    public String wrapStringToWidth(String text, int width) {
        String string = "";
        while (!text.isEmpty()) {
            int i = this.getCharacterCountForWidth(text, width);
            if (text.length() <= i) {
                return string + text;
            }
            String string2 = text.substring(0, i);
            char c = text.charAt(i);
            boolean bl = c == ' ' || c == '\n';
            text = Formatting.getFormatAtEnd(string2) + text.substring(i + (bl ? 1 : 0));
            string = string + string2 + "\n";
        }
        return string;
    }

    public int getCharacterCountForWidth(String text, int offset) {
        int k;
        int i = Math.max(1, offset);
        int j = text.length();
        float f = 0.0f;
        int l = -1;
        boolean bl = false;
        boolean bl2 = true;
        for (k = 0; k < j; ++k) {
            char c = text.charAt(k);
            switch (c) {
                case '\u00a7': {
                    Formatting formatting;
                    if (k >= j - 1) break;
                    if ((formatting = Formatting.byCode(text.charAt(++k))) == Formatting.BOLD) {
                        bl = true;
                        break;
                    }
                    if (formatting == null || !formatting.affectsGlyphWidth()) break;
                    bl = false;
                    break;
                }
                case '\n': {
                    --k;
                    break;
                }
                case ' ': {
                    l = k;
                }
                default: {
                    if (f != 0.0f) {
                        bl2 = false;
                    }
                    f += this.getCharWidth(c);
                    if (!bl) break;
                    f += 1.0f;
                }
            }
            if (c == '\n') {
                l = ++k;
                break;
            }
            if (!(f > (float)i)) continue;
            if (!bl2) break;
            ++k;
            break;
        }
        if (k != j && l != -1 && l < k) {
            return l;
        }
        return k;
    }

    public int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {
        int i = position;
        boolean bl = direction < 0;
        int j = Math.abs(direction);
        for (int k = 0; k < j; ++k) {
            if (bl) {
                while (skipWhitespaceToRightOfWord && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
                    --i;
                }
                while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
                    --i;
                }
                continue;
            }
            int l = text.length();
            int m = text.indexOf(32, i);
            int n = text.indexOf(10, i);
            i = m == -1 && n == -1 ? -1 : (m != -1 && n != -1 ? Math.min(m, n) : (m != -1 ? m : n));
            if (i == -1) {
                i = l;
                continue;
            }
            while (skipWhitespaceToRightOfWord && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
                ++i;
            }
        }
        return i;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    @Environment(value=EnvType.CLIENT)
    static class Rectangle {
        protected final float xMin;
        protected final float yMin;
        protected final float xMax;
        protected final float yMax;
        protected final float red;
        protected final float green;
        protected final float blue;
        protected final float alpha;

        private Rectangle(float xMin, float yMin, float xMax, float yMax, float red, float green, float blue, float alpha) {
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public void draw(BufferBuilder bufferBuilder) {
            bufferBuilder.vertex(this.xMin, this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMax, this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMax, this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMin, this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
        }
    }
}

