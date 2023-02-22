/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  org.apache.commons.lang3.math.NumberUtils
 */
package net.minecraft.datafixer.fix;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.datafixer.fix.EntityBlockStateFix;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix
extends DataFix {
    private static final String field_29905 = "generatorOptions";
    @VisibleForTesting
    static final String field_29904 = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
    private static final Splitter SPLIT_ON_SEMICOLON = Splitter.on((char)';').limit(5);
    private static final Splitter SPLIT_ON_COMMA = Splitter.on((char)',');
    private static final Splitter SPLIT_ON_LOWER_X = Splitter.on((char)'x').limit(2);
    private static final Splitter SPLIT_ON_ASTERISK = Splitter.on((char)'*').limit(2);
    private static final Splitter SPLIT_ON_COLON = Splitter.on((char)':').limit(3);

    public LevelFlatGeneratorInfoFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), this::fixGeneratorOptions));
    }

    private Dynamic<?> fixGeneratorOptions(Dynamic<?> dynamic2) {
        if (dynamic2.get("generatorName").asString("").equalsIgnoreCase("flat")) {
            return dynamic2.update(field_29905, dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asString().map(this::fixFlatGeneratorOptions).map(arg_0 -> ((Dynamic)dynamic).createString(arg_0)).result(), (Object)dynamic));
        }
        return dynamic2;
    }

    @VisibleForTesting
    String fixFlatGeneratorOptions(String generatorOptions) {
        String string2;
        int i;
        if (generatorOptions.isEmpty()) {
            return field_29904;
        }
        Iterator iterator = SPLIT_ON_SEMICOLON.split((CharSequence)generatorOptions).iterator();
        String string3 = (String)iterator.next();
        if (iterator.hasNext()) {
            i = NumberUtils.toInt((String)string3, (int)0);
            string2 = (String)iterator.next();
        } else {
            i = 0;
            string2 = string3;
        }
        if (i < 0 || i > 3) {
            return field_29904;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Splitter splitter = i < 3 ? SPLIT_ON_LOWER_X : SPLIT_ON_ASTERISK;
        stringBuilder.append(StreamSupport.stream(SPLIT_ON_COMMA.split((CharSequence)string2).spliterator(), false).map(string -> {
            String string2;
            int j;
            List list = splitter.splitToList((CharSequence)string);
            if (list.size() == 2) {
                j = NumberUtils.toInt((String)((String)list.get(0)));
                string2 = (String)list.get(1);
            } else {
                j = 1;
                string2 = (String)list.get(0);
            }
            List list2 = SPLIT_ON_COLON.splitToList((CharSequence)string2);
            int k = ((String)list2.get(0)).equals("minecraft") ? 1 : 0;
            String string3 = (String)list2.get(k);
            int l = i == 3 ? EntityBlockStateFix.getNumericalBlockId("minecraft:" + string3) : NumberUtils.toInt((String)string3, (int)0);
            int m = k + 1;
            int n = list2.size() > m ? NumberUtils.toInt((String)((String)list2.get(m)), (int)0) : 0;
            return (String)(j == 1 ? "" : j + "*") + BlockStateFlattening.lookupState(l << 4 | n).get("Name").asString("");
        }).collect(Collectors.joining(",")));
        while (iterator.hasNext()) {
            stringBuilder.append(';').append((String)iterator.next());
        }
        return stringBuilder.toString();
    }
}

