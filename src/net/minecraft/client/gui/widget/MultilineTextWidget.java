/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MultilineTextWidget
extends AbstractTextWidget {
    private OptionalInt maxWidth = OptionalInt.empty();
    private OptionalInt maxRows = OptionalInt.empty();
    private final CachedMapper<CacheKey, MultilineText> cacheKeyToText = Util.cachedMapper(cacheKey -> {
        if (cacheKey.maxRows.isPresent()) {
            return MultilineText.create(textRenderer, (StringVisitable)cacheKey.message, cacheKey.maxWidth, cacheKey.maxRows.getAsInt());
        }
        return MultilineText.create(textRenderer, (StringVisitable)cacheKey.message, cacheKey.maxWidth);
    });
    private boolean centered = false;

    public MultilineTextWidget(Text message, TextRenderer textRenderer) {
        this(0, 0, message, textRenderer);
    }

    public MultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer) {
        super(x, y, 0, 0, message, textRenderer);
        this.active = false;
    }

    @Override
    public MultilineTextWidget setTextColor(int i) {
        super.setTextColor(i);
        return this;
    }

    public MultilineTextWidget setMaxWidth(int maxWidth) {
        this.maxWidth = OptionalInt.of(maxWidth);
        return this;
    }

    public MultilineTextWidget setMaxRows(int maxRows) {
        this.maxRows = OptionalInt.of(maxRows);
        return this;
    }

    public MultilineTextWidget setCentered(boolean centered) {
        this.centered = centered;
        return this;
    }

    @Override
    public int getWidth() {
        return this.cacheKeyToText.map(this.getCacheKey()).getMaxWidth();
    }

    @Override
    public int getHeight() {
        return this.cacheKeyToText.map(this.getCacheKey()).count() * this.getTextRenderer().fontHeight;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MultilineText multilineText = this.cacheKeyToText.map(this.getCacheKey());
        int i = this.getX();
        int j = this.getY();
        int k = this.getTextRenderer().fontHeight;
        int l = this.getTextColor();
        if (this.centered) {
            multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j, k, l);
        } else {
            multilineText.drawWithShadow(context, i, j, k, l);
        }
    }

    private CacheKey getCacheKey() {
        return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
    }

    @Override
    public /* synthetic */ AbstractTextWidget setTextColor(int textColor) {
        return this.setTextColor(textColor);
    }

    @Environment(value=EnvType.CLIENT)
    static final class CacheKey
    extends Record {
        final Text message;
        final int maxWidth;
        final OptionalInt maxRows;

        CacheKey(Text text, int i, OptionalInt optionalInt) {
            this.message = text;
            this.maxWidth = i;
            this.maxRows = optionalInt;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this, object);
        }

        public Text message() {
            return this.message;
        }

        public int maxWidth() {
            return this.maxWidth;
        }

        public OptionalInt maxRows() {
            return this.maxRows;
        }
    }
}

