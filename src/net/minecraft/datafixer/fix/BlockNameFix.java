/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class BlockNameFix
extends DataFix {
    private final String name;

    public BlockNameFix(Schema oldSchema, String name) {
        super(oldSchema, false);
        this.name = name;
    }

    public TypeRewriteRule makeRule() {
        Type type2;
        Type type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
        if (!Objects.equals(type, type2 = DSL.named((String)TypeReferences.BLOCK_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()))) {
            throw new IllegalStateException("block type is not what was expected.");
        }
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::rename));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional optional = dynamic.get("Name").asString().result();
            if (optional.isPresent()) {
                return dynamic.set("Name", dynamic.createString(this.rename((String)optional.get())));
            }
            return dynamic;
        }));
        return TypeRewriteRule.seq((TypeRewriteRule)typeRewriteRule, (TypeRewriteRule)typeRewriteRule2);
    }

    protected abstract String rename(String var1);

    public static DataFix create(Schema oldSchema, String name, final Function<String, String> rename) {
        return new BlockNameFix(oldSchema, name){

            @Override
            protected String rename(String oldName) {
                return (String)rename.apply(oldName);
            }
        };
    }
}

