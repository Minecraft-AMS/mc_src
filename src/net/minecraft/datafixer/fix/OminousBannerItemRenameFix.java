/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class OminousBannerItemRenameFix
extends DataFix {
    public OminousBannerItemRenameFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixBannerName(Dynamic<?> tag) {
        Optional optional = tag.get("display").get();
        if (optional.isPresent()) {
            Dynamic dynamic = (Dynamic)optional.get();
            Optional optional2 = dynamic.get("Name").asString();
            if (optional2.isPresent()) {
                String string = (String)optional2.get();
                string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
                dynamic = dynamic.set("Name", dynamic.createString(string));
            }
            return tag.set("display", dynamic);
        }
        return tag;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), (Type)DSL.namespacedString()));
        OpticFinder opticFinder2 = type.findField("tag");
        return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, typed -> {
            Optional optional2;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:white_banner") && (optional2 = typed.getOptionalTyped(opticFinder2)).isPresent()) {
                Typed typed2 = (Typed)optional2.get();
                Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
                return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), this.fixBannerName(dynamic)));
            }
            return typed;
        });
    }
}

