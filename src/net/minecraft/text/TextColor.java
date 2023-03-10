/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public final class TextColor {
    private static final String RGB_PREFIX = "#";
    private static final Map<Formatting, TextColor> FORMATTING_TO_COLOR = (Map)Stream.of(Formatting.values()).filter(Formatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), formatting -> new TextColor(formatting.getColorValue(), formatting.getName())));
    private static final Map<String, TextColor> BY_NAME = (Map)FORMATTING_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap(textColor -> textColor.name, Function.identity()));
    private final int rgb;
    @Nullable
    private final String name;

    private TextColor(int rgb, String name) {
        this.rgb = rgb;
        this.name = name;
    }

    private TextColor(int rgb) {
        this.rgb = rgb;
        this.name = null;
    }

    public int getRgb() {
        return this.rgb;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.getHexCode();
    }

    private String getHexCode() {
        return String.format("#%06X", this.rgb);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TextColor textColor = (TextColor)o;
        return this.rgb == textColor.rgb;
    }

    public int hashCode() {
        return Objects.hash(this.rgb, this.name);
    }

    public String toString() {
        return this.name != null ? this.name : this.getHexCode();
    }

    @Nullable
    public static TextColor fromFormatting(Formatting formatting) {
        return FORMATTING_TO_COLOR.get((Object)formatting);
    }

    public static TextColor fromRgb(int rgb) {
        return new TextColor(rgb);
    }

    @Nullable
    public static TextColor parse(String name) {
        if (name.startsWith(RGB_PREFIX)) {
            try {
                int i = Integer.parseInt(name.substring(1), 16);
                return TextColor.fromRgb(i);
            }
            catch (NumberFormatException numberFormatException) {
                return null;
            }
        }
        return BY_NAME.get(name);
    }
}

