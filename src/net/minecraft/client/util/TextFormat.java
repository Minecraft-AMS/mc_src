/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum TextFormat {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    private static final Map<Character, TextFormat> FORMATTING_BY_CHAR;
    private static final Map<String, TextFormat> FORMATTING_BY_NAME;
    private static final Pattern STRIP_FORMATTING_PATTERN;
    private final char code;
    private final boolean isFormat;
    private final String toString;

    private TextFormat(char code) {
        this(code, false);
    }

    private TextFormat(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = "\u00a7" + code;
    }

    public char getChar() {
        return this.code;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    static {
        FORMATTING_BY_CHAR = Arrays.stream(TextFormat.values()).collect(Collectors.toMap(TextFormat::getChar, textFormat -> textFormat));
        FORMATTING_BY_NAME = Arrays.stream(TextFormat.values()).collect(Collectors.toMap(TextFormat::getName, textFormat -> textFormat));
        STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

