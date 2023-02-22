/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.lang.UCharacter
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.Bidi
 *  com.ibm.icu.text.BidiRun
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TextReorderingProcessor;

@Environment(value=EnvType.CLIENT)
public class ReorderingUtil {
    public static OrderedText reorder(StringVisitable text, boolean rightToLeft) {
        TextReorderingProcessor textReorderingProcessor = TextReorderingProcessor.create(text, UCharacter::getMirror, ReorderingUtil::shapeArabic);
        Bidi bidi = new Bidi(textReorderingProcessor.getString(), rightToLeft ? 127 : 126);
        bidi.setReorderingMode(0);
        ArrayList list = Lists.newArrayList();
        int i = bidi.countRuns();
        for (int j = 0; j < i; ++j) {
            BidiRun bidiRun = bidi.getVisualRun(j);
            list.addAll(textReorderingProcessor.process(bidiRun.getStart(), bidiRun.getLength(), bidiRun.isOddRun()));
        }
        return OrderedText.concat(list);
    }

    private static String shapeArabic(String string) {
        try {
            return new ArabicShaping(8).shape(string);
        }
        catch (Exception exception) {
            return string;
        }
    }
}

