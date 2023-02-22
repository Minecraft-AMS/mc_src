/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemRemoveBlockEntityTagFix
extends DataFix {
    private final Set<String> itemIds;

    public ItemRemoveBlockEntityTagFix(Schema schema, boolean changesType, Set<String> itemIds) {
        super(schema, changesType);
        this.itemIds = itemIds;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        OpticFinder opticFinder2 = type.findField("tag");
        OpticFinder opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", type, typed -> {
            Typed typed2;
            Optional optional3;
            Optional optional2;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && this.itemIds.contains(((Pair)optional.get()).getSecond()) && (optional2 = typed.getOptionalTyped(opticFinder2)).isPresent() && (optional3 = (typed2 = (Typed)optional2.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                Optional optional4 = typed2.write().result();
                Dynamic dynamic = optional4.isPresent() ? (Dynamic)optional4.get() : (Dynamic)typed2.get(DSL.remainderFinder());
                Dynamic dynamic2 = dynamic.remove("BlockEntityTag");
                Optional optional5 = opticFinder2.type().readTyped(dynamic2).result();
                if (optional5.isEmpty()) {
                    return typed;
                }
                return typed.set(opticFinder2, (Typed)((Pair)optional5.get()).getFirst());
            }
            return typed;
        });
    }
}

