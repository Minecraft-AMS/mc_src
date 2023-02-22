/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockNameFix;

public abstract class JigsawBlockNameFix
extends BlockNameFix {
    private final String name;

    public JigsawBlockNameFix(Schema schema, String string) {
        super(schema, string);
        this.name = string;
    }

    @Override
    public TypeRewriteRule makeRule() {
        DSL.TypeReference typeReference = TypeReferences.BLOCK_ENTITY;
        String string = "minecraft:jigsaw";
        OpticFinder opticFinder = DSL.namedChoice((String)"minecraft:jigsaw", (Type)this.getInputSchema().getChoiceType(typeReference, "minecraft:jigsaw"));
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType(typeReference), this.getOutputSchema().getType(typeReference), typed2 -> typed2.updateTyped(opticFinder, this.getOutputSchema().getChoiceType(typeReference, "minecraft:jigsaw"), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("final_state", dynamic2 -> (Dynamic)DataFixUtils.orElse(dynamic2.asString().result().map(string -> {
            int i = string.indexOf(91);
            int j = string.indexOf(123);
            int k = string.length();
            if (i > 0) {
                k = Math.min(k, i);
            }
            if (j > 0) {
                k = Math.min(k, j);
            }
            String string2 = string.substring(0, k);
            String string3 = this.rename(string2);
            return string3 + string.substring(k);
        }).map(arg_0 -> ((Dynamic)dynamic).createString(arg_0)), (Object)dynamic2)))));
        return TypeRewriteRule.seq((TypeRewriteRule)super.makeRule(), (TypeRewriteRule)typeRewriteRule);
    }

    public static DataFix create(Schema oldSchema, String name, final Function<String, String> rename) {
        return new JigsawBlockNameFix(oldSchema, name){

            @Override
            protected String rename(String oldName) {
                return (String)rename.apply(oldName);
            }
        };
    }
}

