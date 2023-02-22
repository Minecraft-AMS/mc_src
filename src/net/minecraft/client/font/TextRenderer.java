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
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class TextRenderer {
    private static final float Z_INDEX = 0.01f;
    private static final Vector3f FORWARD_SHIFT = new Vector3f(0.0f, 0.0f, 0.03f);
    public static final int ARABIC_SHAPING_LETTERS_SHAPE = 8;
    public final int fontHeight = 9;
    public final Random random = Random.create();
    private final Function<Identifier, FontStorage> fontStorageAccessor;
    final boolean validateAdvance;
    private final TextHandler handler;

    public TextRenderer(Function<Identifier, FontStorage> fontStorageAccessor, boolean validateAdvance) {
        this.fontStorageAccessor = fontStorageAccessor;
        this.validateAdvance = validateAdvance;
        this.handler = new TextHandler((codePoint, style) -> this.getFontStorage(style.getFont()).getGlyph(codePoint, this.validateAdvance).getAdvance(style.isBold()));
    }

    FontStorage getFontStorage(Identifier id) {
        return this.fontStorageAccessor.apply(id);
    }

    public int drawWithShadow(MatrixStack matrices, String text, float x, float y, int color) {
        return this.draw(text, x, y, color, matrices.peek().getPositionMatrix(), true, this.isRightToLeft());
    }

    public int drawWithShadow(MatrixStack matrices, String text, float x, float y, int color, boolean rightToLeft) {
        return this.draw(text, x, y, color, matrices.peek().getPositionMatrix(), true, rightToLeft);
    }

    public int draw(MatrixStack matrices, String text, float x, float y, int color) {
        return this.draw(text, x, y, color, matrices.peek().getPositionMatrix(), false, this.isRightToLeft());
    }

    public int drawWithShadow(MatrixStack matrices, OrderedText text, float x, float y, int color) {
        return this.draw(text, x, y, color, matrices.peek().getPositionMatrix(), true);
    }

    public int drawWithShadow(MatrixStack matrices, Text text, float x, float y, int color) {
        return this.draw(text.asOrderedText(), x, y, color, matrices.peek().getPositionMatrix(), true);
    }

    public int draw(MatrixStack matrices, OrderedText text, float x, float y, int color) {
        return this.draw(text, x, y, color, matrices.peek().getPositionMatrix(), false);
    }

    public int draw(MatrixStack matrices, Text text, float x, float y, int color) {
        return this.draw(text.asOrderedText(), x, y, color, matrices.peek().getPositionMatrix(), false);
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

    private int draw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow, boolean mirror) {
        if (text == null) {
            return 0;
        }
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int i = this.draw(text, x, y, color, shadow, matrix, immediate, false, 0, 0xF000F0, mirror);
        immediate.draw();
        return i;
    }

    private int draw(OrderedText text, float x, float y, int color, Matrix4f matrix, boolean shadow) {
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int i = this.draw(text, x, y, color, shadow, matrix, (VertexConsumerProvider)immediate, false, 0, 0xF000F0);
        immediate.draw();
        return i;
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        return this.draw(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light, this.isRightToLeft());
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, boolean rightToLeft) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light, rightToLeft);
    }

    public int draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        return this.draw(text.asOrderedText(), x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
    }

    public int draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
    }

    public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        int i = TextRenderer.tweakTransparency(outlineColor);
        Drawer drawer = new Drawer(vertexConsumers, 0.0f, 0.0f, i, false, matrix, TextLayerType.NORMAL, light);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if (j == 0 && k == 0) continue;
                float[] fs = new float[]{x};
                int l = j;
                int m = k;
                text.accept((index, style, codePoint) -> {
                    boolean bl = style.isBold();
                    FontStorage fontStorage = this.getFontStorage(style.getFont());
                    Glyph glyph = fontStorage.getGlyph(codePoint, this.validateAdvance);
                    drawer.x = fs[0] + (float)l * glyph.getShadowOffset();
                    drawer.y = y + (float)m * glyph.getShadowOffset();
                    fs[0] = fs[0] + glyph.getAdvance(bl);
                    return drawer.accept(index, style.withColor(i), codePoint);
                });
            }
        }
        Drawer drawer2 = new Drawer(vertexConsumers, x, y, TextRenderer.tweakTransparency(color), false, matrix, TextLayerType.POLYGON_OFFSET, light);
        text.accept(drawer2);
        drawer2.drawLayer(0, x);
    }

    private static int tweakTransparency(int argb) {
        if ((argb & 0xFC000000) == 0) {
            return argb | 0xFF000000;
        }
        return argb;
    }

    private int drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, boolean mirror) {
        if (mirror) {
            text = this.mirror(text);
        }
        color = TextRenderer.tweakTransparency(color);
        Matrix4f matrix4f = new Matrix4f((Matrix4fc)matrix);
        if (shadow) {
            this.drawLayer(text, x, y, color, true, matrix, vertexConsumers, seeThrough, backgroundColor, light);
            matrix4f.translate((Vector3fc)FORWARD_SHIFT);
        }
        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumers, seeThrough, backgroundColor, light);
        return (int)x + (shadow ? 1 : 0);
    }

    private int drawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {
        color = TextRenderer.tweakTransparency(color);
        Matrix4f matrix4f = new Matrix4f((Matrix4fc)matrix);
        if (shadow) {
            this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
            matrix4f.translate((Vector3fc)FORWARD_SHIFT);
        }
        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, seeThrough, backgroundColor, light);
        return (int)x + (shadow ? 1 : 0);
    }

    private float drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) {
        Drawer drawer = new Drawer(vertexConsumerProvider, x, y, color, shadow, matrix, seeThrough, light);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, (CharacterVisitor)drawer);
        return drawer.drawLayer(underlineColor, x);
    }

    private float drawLayer(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) {
        Drawer drawer = new Drawer(vertexConsumerProvider, x, y, color, shadow, matrix, seeThrough, light);
        text.accept(drawer);
        return drawer.drawLayer(underlineColor, x);
    }

    void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
        glyphRenderer.draw(italic, x, y, matrix, vertexConsumer, red, green, blue, alpha, light);
        if (bold) {
            glyphRenderer.draw(italic, x + weight, y, matrix, vertexConsumer, red, green, blue, alpha, light);
        }
    }

    public int getWidth(String text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public StringVisitable trimToWidth(StringVisitable text, int width) {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }

    public void drawTrimmed(StringVisitable text, int x, int y, int maxWidth, int color) {
        Matrix4f matrix4f = AffineTransformation.identity().getMatrix();
        for (OrderedText orderedText : this.wrapLines(text, maxWidth)) {
            this.draw(orderedText, x, y, color, matrix4f, false);
            y += 9;
        }
    }

    public int getWrappedLinesHeight(String text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public List<OrderedText> wrapLines(StringVisitable text, int width) {
        return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
    }

    public boolean isRightToLeft() {
        return Language.getInstance().isRightToLeft();
    }

    public TextHandler getTextHandler() {
        return this.handler;
    }

    @Environment(value=EnvType.CLIENT)
    class Drawer
    implements CharacterVisitor {
        final VertexConsumerProvider vertexConsumers;
        private final boolean shadow;
        private final float brightnessMultiplier;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final Matrix4f matrix;
        private final TextLayerType layerType;
        private final int light;
        float x;
        float y;
        @Nullable
        private List<GlyphRenderer.Rectangle> rectangles;

        private void addRectangle(GlyphRenderer.Rectangle rectangle) {
            if (this.rectangles == null) {
                this.rectangles = Lists.newArrayList();
            }
            this.rectangles.add(rectangle);
        }

        public Drawer(VertexConsumerProvider vertexConsumers, float x, float y, int color, boolean shadow, Matrix4f matrix, boolean seeThrough, int light) {
            this(vertexConsumers, x, y, color, shadow, matrix, seeThrough ? TextLayerType.SEE_THROUGH : TextLayerType.NORMAL, light);
        }

        public Drawer(VertexConsumerProvider vertexConsumers, float x, float y, int color, boolean shadow, Matrix4f matrix, TextLayerType layerType, int light) {
            this.vertexConsumers = vertexConsumers;
            this.x = x;
            this.y = y;
            this.shadow = shadow;
            this.brightnessMultiplier = shadow ? 0.25f : 1.0f;
            this.red = (float)(color >> 16 & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.green = (float)(color >> 8 & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.blue = (float)(color & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.alpha = (float)(color >> 24 & 0xFF) / 255.0f;
            this.matrix = matrix;
            this.layerType = layerType;
            this.light = light;
        }

        @Override
        public boolean accept(int i, Style style, int j) {
            float n;
            float l;
            float h;
            float g;
            FontStorage fontStorage = TextRenderer.this.getFontStorage(style.getFont());
            Glyph glyph = fontStorage.getGlyph(j, TextRenderer.this.validateAdvance);
            GlyphRenderer glyphRenderer = style.isObfuscated() && j != 32 ? fontStorage.getObfuscatedGlyphRenderer(glyph) : fontStorage.getGlyphRenderer(j);
            boolean bl = style.isBold();
            float f = this.alpha;
            TextColor textColor = style.getColor();
            if (textColor != null) {
                int k = textColor.getRgb();
                g = (float)(k >> 16 & 0xFF) / 255.0f * this.brightnessMultiplier;
                h = (float)(k >> 8 & 0xFF) / 255.0f * this.brightnessMultiplier;
                l = (float)(k & 0xFF) / 255.0f * this.brightnessMultiplier;
            } else {
                g = this.red;
                h = this.green;
                l = this.blue;
            }
            if (!(glyphRenderer instanceof EmptyGlyphRenderer)) {
                float m = bl ? glyph.getBoldOffset() : 0.0f;
                n = this.shadow ? glyph.getShadowOffset() : 0.0f;
                VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));
                TextRenderer.this.drawGlyph(glyphRenderer, bl, style.isItalic(), m, this.x + n, this.y + n, this.matrix, vertexConsumer, g, h, l, f, this.light);
            }
            float m = glyph.getAdvance(bl);
            float f2 = n = this.shadow ? 1.0f : 0.0f;
            if (style.isStrikethrough()) {
                this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0f, this.y + n + 4.5f, this.x + n + m, this.y + n + 4.5f - 1.0f, 0.01f, g, h, l, f));
            }
            if (style.isUnderlined()) {
                this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0f, this.y + n + 9.0f, this.x + n + m, this.y + n + 9.0f - 1.0f, 0.01f, g, h, l, f));
            }
            this.x += m;
            return true;
        }

        public float drawLayer(int underlineColor, float x) {
            if (underlineColor != 0) {
                float f = (float)(underlineColor >> 24 & 0xFF) / 255.0f;
                float g = (float)(underlineColor >> 16 & 0xFF) / 255.0f;
                float h = (float)(underlineColor >> 8 & 0xFF) / 255.0f;
                float i = (float)(underlineColor & 0xFF) / 255.0f;
                this.addRectangle(new GlyphRenderer.Rectangle(x - 1.0f, this.y + 9.0f, this.x + 1.0f, this.y - 1.0f, 0.01f, g, h, i, f));
            }
            if (this.rectangles != null) {
                GlyphRenderer glyphRenderer = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleRenderer();
                VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));
                for (GlyphRenderer.Rectangle rectangle : this.rectangles) {
                    glyphRenderer.drawRectangle(rectangle, this.matrix, vertexConsumer, this.light);
                }
            }
            return this.x;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class TextLayerType
    extends Enum<TextLayerType> {
        public static final /* enum */ TextLayerType NORMAL = new TextLayerType();
        public static final /* enum */ TextLayerType SEE_THROUGH = new TextLayerType();
        public static final /* enum */ TextLayerType POLYGON_OFFSET = new TextLayerType();
        private static final /* synthetic */ TextLayerType[] field_33996;

        public static TextLayerType[] values() {
            return (TextLayerType[])field_33996.clone();
        }

        public static TextLayerType valueOf(String string) {
            return Enum.valueOf(TextLayerType.class, string);
        }

        private static /* synthetic */ TextLayerType[] method_37344() {
            return new TextLayerType[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
        }

        static {
            field_33996 = TextLayerType.method_37344();
        }
    }
}

