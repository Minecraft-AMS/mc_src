/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ObjectiveDisplayNameFix
extends DataFix {
    public ObjectiveDisplayNameFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.OBJECTIVE);
        return this.fixTypeEverywhereTyped("ObjectiveDisplayNameFix", type, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("DisplayName", dynamic2 -> (Dynamic)DataFixUtils.orElse((Optional)dynamic2.asString().map(string -> Text.Serializer.toJson(new LiteralText((String)string))).map(arg_0 -> ((Dynamic)dynamic).createString(arg_0)).result(), (Object)dynamic2))));
    }
}

