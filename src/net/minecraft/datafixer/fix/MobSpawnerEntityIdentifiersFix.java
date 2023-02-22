/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class MobSpawnerEntityIdentifiersFix
extends DataFix {
    public MobSpawnerEntityIdentifiersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixSpawner(Dynamic<?> tag) {
        Optional optional2;
        if (!"MobSpawner".equals(tag.get("id").asString(""))) {
            return tag;
        }
        Optional optional = tag.get("EntityId").asString();
        if (optional.isPresent()) {
            Dynamic dynamic2 = (Dynamic)DataFixUtils.orElse((Optional)tag.get("SpawnData").get(), (Object)tag.emptyMap());
            dynamic2 = dynamic2.set("id", dynamic2.createString(((String)optional.get()).isEmpty() ? "Pig" : (String)optional.get()));
            tag = tag.set("SpawnData", dynamic2);
            tag = tag.remove("EntityId");
        }
        if ((optional2 = tag.get("SpawnPotentials").asStreamOpt()).isPresent()) {
            tag = tag.set("SpawnPotentials", tag.createList(((Stream)optional2.get()).map(dynamic -> {
                Optional optional = dynamic.get("Type").asString();
                if (optional.isPresent()) {
                    Dynamic dynamic2 = ((Dynamic)DataFixUtils.orElse((Optional)dynamic.get("Properties").get(), (Object)dynamic.emptyMap())).set("id", dynamic.createString((String)optional.get()));
                    return dynamic.set("Entity", dynamic2).remove("Type").remove("Properties");
                }
                return dynamic;
            })));
        }
        return tag;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, typed -> {
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            Pair pair = type.readTyped(this.fixSpawner(dynamic = dynamic.set("id", dynamic.createString("MobSpawner"))));
            if (!((Optional)pair.getSecond()).isPresent()) {
                return typed;
            }
            return (Typed)((Optional)pair.getSecond()).get();
        });
    }
}

