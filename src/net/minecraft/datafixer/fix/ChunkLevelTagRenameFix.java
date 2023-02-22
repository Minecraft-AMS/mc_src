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
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class ChunkLevelTagRenameFix
extends DataFix {
    public ChunkLevelTagRenameFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Structures");
        Type type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type type3 = type2.findFieldType("structures");
        return this.fixTypeEverywhereTyped("Chunk Renames; purge Level-tag", type, type2, typed2 -> {
            Typed typed22 = typed2.getTyped(opticFinder);
            Typed<?> typed3 = ChunkLevelTagRenameFix.method_39269(typed22);
            typed3 = typed3.set(DSL.remainderFinder(), ChunkLevelTagRenameFix.method_39270(typed2, (Dynamic)typed22.get(DSL.remainderFinder())));
            typed3 = ChunkLevelTagRenameFix.rename(typed3, "TileEntities", "block_entities");
            typed3 = ChunkLevelTagRenameFix.rename(typed3, "TileTicks", "block_ticks");
            typed3 = ChunkLevelTagRenameFix.rename(typed3, "Entities", "entities");
            typed3 = ChunkLevelTagRenameFix.rename(typed3, "Sections", "sections");
            typed3 = typed3.updateTyped(opticFinder2, type3, typed -> ChunkLevelTagRenameFix.rename(typed, "Starts", "starts"));
            typed3 = ChunkLevelTagRenameFix.rename(typed3, "Structures", "structures");
            return typed3.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Level"));
        });
    }

    private static Typed<?> rename(Typed<?> typed, String oldKey, String newKey) {
        return ChunkLevelTagRenameFix.rename(typed, oldKey, newKey, typed.getType().findFieldType(oldKey)).update(DSL.remainderFinder(), dynamic -> dynamic.remove(oldKey));
    }

    private static <A> Typed<?> rename(Typed<?> typed, String oldKey, String newKey, Type<A> type) {
        Type type2 = DSL.optional((Type)DSL.field((String)oldKey, type));
        Type type3 = DSL.optional((Type)DSL.field((String)newKey, type));
        return typed.update(type2.finder(), type3, Function.identity());
    }

    private static <A> Typed<Pair<String, A>> method_39269(Typed<A> typed) {
        return new Typed(DSL.named((String)"chunk", (Type)typed.getType()), typed.getOps(), (Object)Pair.of((Object)"chunk", (Object)typed.getValue()));
    }

    private static <T> Dynamic<T> method_39270(Typed<?> typed, Dynamic<T> dynamic) {
        DynamicOps dynamicOps = dynamic.getOps();
        Dynamic dynamic2 = ((Dynamic)typed.get(DSL.remainderFinder())).convert(dynamicOps);
        DataResult dataResult = dynamicOps.getMap(dynamic.getValue()).flatMap(mapLike -> dynamicOps.mergeToMap(dynamic2.getValue(), mapLike));
        return dataResult.result().map(object -> new Dynamic(dynamicOps, object)).orElse(dynamic);
    }
}

