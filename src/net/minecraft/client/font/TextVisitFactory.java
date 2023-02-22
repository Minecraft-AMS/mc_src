/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class TextVisitFactory {
    private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

    private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
        if (Character.isSurrogate(c)) {
            return visitor.accept(index, style, 65533);
        }
        return visitor.accept(index, style, c);
    }

    public static boolean visitForwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = 0; j < i; ++j) {
            char c = text.charAt(j);
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (visitor.accept(j, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (visitor.accept(j, style, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitBackwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = i - 1; j >= 0; --j) {
            char c = text.charAt(j);
            if (Character.isLowSurrogate(c)) {
                if (j - 1 < 0) {
                    if (visitor.accept(0, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j - 1);
                if (!(Character.isHighSurrogate(d) ? !visitor.accept(--j, style, Character.toCodePoint(d, c)) : !visitor.accept(j, style, 65533))) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(String text, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, 0, style, visitor);
    }

    public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, startIndex, style, style, visitor);
    }

    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        int i = text.length();
        Style style = startingStyle;
        for (int j = startIndex; j < i; ++j) {
            char d;
            char c = text.charAt(j);
            if (c == '\u00a7') {
                if (j + 1 >= i) break;
                d = text.charAt(j + 1);
                Formatting formatting = Formatting.byCode(d);
                if (formatting != null) {
                    style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
                }
                ++j;
                continue;
            }
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (visitor.accept(j, style, 65533)) break;
                    return false;
                }
                d = text.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (visitor.accept(j, style, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(StringVisitable text, Style style2, CharacterVisitor visitor) {
        return !text.visit((style, string) -> TextVisitFactory.visitFormatted(string, 0, style, visitor) ? Optional.empty() : VISIT_TERMINATED, style2).isPresent();
    }

    public static String validateSurrogates(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitForwards(text, Style.EMPTY, (i, style, j) -> {
            stringBuilder.appendCodePoint(j);
            return true;
        });
        return stringBuilder.toString();
    }

    public static String method_31402(StringVisitable stringVisitable) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitFormatted(stringVisitable, Style.EMPTY, (int i, Style style, int j) -> {
            stringBuilder.appendCodePoint(j);
            return true;
        });
        return stringBuilder.toString();
    }
}
