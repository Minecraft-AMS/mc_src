/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class OptionsAmbientOcclusionFix
extends DataFix {
    public OptionsAmbientOcclusionFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAmbientOcclusionFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.get("ao").asString().map(string -> dynamic.set("ao", dynamic.createString(OptionsAmbientOcclusionFix.fixValue(string)))).result(), (Object)dynamic)));
    }

    private static String fixValue(String oldValue) {
        return switch (oldValue) {
            case "0" -> "false";
            case "1", "2" -> "true";
            default -> oldValue;
        };
    }
}

