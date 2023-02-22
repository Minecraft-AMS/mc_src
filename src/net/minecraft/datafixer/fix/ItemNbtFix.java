/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
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
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class ItemNbtFix
extends DataFix {
    private final String name;
    private final Predicate<String> itemIdPredicate;

    public ItemNbtFix(Schema schema, String name, Predicate<String> itemIdPredicate) {
        super(schema, false);
        this.name = name;
        this.itemIdPredicate = itemIdPredicate;
    }

    public final TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        OpticFinder opticFinder2 = type.findField("tag");
        return this.fixTypeEverywhereTyped(this.name, type, typed -> {
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && this.itemIdPredicate.test((String)((Pair)optional.get()).getSecond())) {
                return typed.updateTyped(opticFinder2, nbt -> nbt.update(DSL.remainderFinder(), this::fixNbt));
            }
            return typed;
        });
    }

    protected abstract <T> Dynamic<T> fixNbt(Dynamic<T> var1);
}

