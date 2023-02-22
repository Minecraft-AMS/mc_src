/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ItemCustomNameToComponentFix
extends DataFix {
    public ItemCustomNameToComponentFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixCustomName(Dynamic<?> tag) {
        Optional optional = tag.get("display").get();
        if (optional.isPresent()) {
            Dynamic dynamic = (Dynamic)optional.get();
            Optional optional2 = dynamic.get("Name").asString();
            if (optional2.isPresent()) {
                dynamic = dynamic.set("Name", dynamic.createString(Text.Serializer.toJson(new LiteralText((String)optional2.get()))));
            } else {
                Optional optional3 = dynamic.get("LocName").asString();
                if (optional3.isPresent()) {
                    dynamic = dynamic.set("Name", dynamic.createString(Text.Serializer.toJson(new TranslatableText((String)optional3.get(), new Object[0]))));
                    dynamic = dynamic.remove("LocName");
                }
            }
            return tag.set("display", dynamic);
        }
        return tag;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixCustomName)));
    }
}

