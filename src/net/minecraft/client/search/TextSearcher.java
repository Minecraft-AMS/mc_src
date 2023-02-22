/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SuffixArray;

@Environment(value=EnvType.CLIENT)
public interface TextSearcher<T> {
    public static <T> TextSearcher<T> of() {
        return text -> List.of();
    }

    public static <T> TextSearcher<T> of(List<T> values, Function<T, Stream<String>> textsGetter) {
        if (values.isEmpty()) {
            return TextSearcher.of();
        }
        SuffixArray suffixArray = new SuffixArray();
        for (Object object : values) {
            textsGetter.apply(object).forEach(text -> suffixArray.add(object, text.toLowerCase(Locale.ROOT)));
        }
        suffixArray.build();
        return suffixArray::findAll;
    }

    public List<T> search(String var1);
}

