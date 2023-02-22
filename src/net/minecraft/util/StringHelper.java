/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class StringHelper {
    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_BREAK = Pattern.compile("\\r\\n|\\v");
    private static final Pattern ENDS_WITH_LINE_BREAK = Pattern.compile("(?:\\r\\n|\\v)$");

    public static String formatTicks(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        i %= 60;
        int k = j / 60;
        j %= 60;
        if (k > 0) {
            return String.format(Locale.ROOT, "%02d:%02d:%02d", k, j, i);
        }
        return String.format(Locale.ROOT, "%02d:%02d", j, i);
    }

    public static String stripTextFormat(String text) {
        return FORMATTING_CODE.matcher(text).replaceAll("");
    }

    public static boolean isEmpty(@Nullable String text) {
        return StringUtils.isEmpty((CharSequence)text);
    }

    public static String truncate(String text, int maxLength, boolean addEllipsis) {
        if (text.length() <= maxLength) {
            return text;
        }
        if (addEllipsis && maxLength > 3) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text.substring(0, maxLength);
    }

    public static int countLines(String text) {
        if (text.isEmpty()) {
            return 0;
        }
        Matcher matcher = LINE_BREAK.matcher(text);
        int i = 1;
        while (matcher.find()) {
            ++i;
        }
        return i;
    }

    public static boolean endsWithLineBreak(String text) {
        return ENDS_WITH_LINE_BREAK.matcher(text).find();
    }

    public static String truncateChat(String text) {
        return StringHelper.truncate(text, 256, false);
    }
}

