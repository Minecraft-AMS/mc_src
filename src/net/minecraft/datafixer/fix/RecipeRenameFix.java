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
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class RecipeRenameFix
extends DataFix {
    private final String name;
    private final Function<String, String> renamer;

    public RecipeRenameFix(Schema schema, boolean bl, String name, Function<String, String> renamer) {
        super(schema, bl);
        this.name = name;
        this.renamer = renamer;
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.RECIPE.typeName(), (Type)DSL.namespacedString());
        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this.renamer));
    }
}

