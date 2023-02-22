/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

@FunctionalInterface
public interface OrderedText {
    public static final OrderedText EMPTY = visitor -> true;

    public boolean accept(CharacterVisitor var1);

    public static OrderedText styled(int codePoint, Style style) {
        return visitor -> visitor.accept(0, style, codePoint);
    }

    public static OrderedText styledForwardsVisitedString(String string, Style style) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitForwards(string, style, visitor);
    }

    public static OrderedText styledForwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitForwards(string, style, OrderedText.map(visitor, codePointMapper));
    }

    public static OrderedText styledBackwardsVisitedString(String string, Style style) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitBackwards(string, style, visitor);
    }

    public static OrderedText styledBackwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitBackwards(string, style, OrderedText.map(visitor, codePointMapper));
    }

    public static CharacterVisitor map(CharacterVisitor visitor, Int2IntFunction codePointMapper) {
        return (charIndex, style, charPoint) -> visitor.accept(charIndex, style, (Integer)codePointMapper.apply((Object)charPoint));
    }

    public static OrderedText empty() {
        return EMPTY;
    }

    public static OrderedText of(OrderedText text) {
        return text;
    }

    public static OrderedText concat(OrderedText first, OrderedText second) {
        return OrderedText.innerConcat(first, second);
    }

    public static OrderedText concat(OrderedText ... texts) {
        return OrderedText.innerConcat((List<OrderedText>)ImmutableList.copyOf((Object[])texts));
    }

    public static OrderedText concat(List<OrderedText> texts) {
        int i = texts.size();
        switch (i) {
            case 0: {
                return EMPTY;
            }
            case 1: {
                return texts.get(0);
            }
            case 2: {
                return OrderedText.innerConcat(texts.get(0), texts.get(1));
            }
        }
        return OrderedText.innerConcat((List<OrderedText>)ImmutableList.copyOf(texts));
    }

    public static OrderedText innerConcat(OrderedText text1, OrderedText text2) {
        return visitor -> text1.accept(visitor) && text2.accept(visitor);
    }

    public static OrderedText innerConcat(List<OrderedText> texts) {
        return visitor -> {
            for (OrderedText orderedText : texts) {
                if (orderedText.accept(visitor)) continue;
                return false;
            }
            return true;
        };
    }
}

