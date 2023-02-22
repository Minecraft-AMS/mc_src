/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.math.ChunkSectionPos;

public class BlendingDataFix
extends DataFix {
    private final String name;
    private static final Set<String> SKIP_BLENDING_STATUSES = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

    public BlendingDataFix(Schema schema) {
        super(schema, false);
        this.name = "Blending Data Fix v" + schema.getVersionKey();
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(TypeReferences.CHUNK);
        return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), (T chunk) -> BlendingDataFix.update(chunk, chunk.get("__context"))));
    }

    private static Dynamic<?> update(Dynamic<?> chunk, OptionalDynamic<?> context) {
        chunk = chunk.remove("blending_data");
        boolean bl = "minecraft:overworld".equals(context.get("dimension").asString().result().orElse(""));
        Optional optional = chunk.get("Status").result();
        if (bl && optional.isPresent()) {
            Dynamic dynamic;
            String string2;
            String string = IdentifierNormalizingSchema.normalize(((Dynamic)optional.get()).asString("empty"));
            Optional optional2 = chunk.get("below_zero_retrogen").result();
            if (!SKIP_BLENDING_STATUSES.contains(string)) {
                chunk = BlendingDataFix.setSections(chunk, 384, -64);
            } else if (optional2.isPresent() && !SKIP_BLENDING_STATUSES.contains(string2 = IdentifierNormalizingSchema.normalize((dynamic = (Dynamic)optional2.get()).get("target_status").asString("empty")))) {
                chunk = BlendingDataFix.setSections(chunk, 256, 0);
            }
        }
        return chunk;
    }

    private static Dynamic<?> setSections(Dynamic<?> dynamic, int height, int minY) {
        return dynamic.set("blending_data", dynamic.createMap(Map.of(dynamic.createString("min_section"), dynamic.createInt(ChunkSectionPos.getSectionCoord(minY)), dynamic.createString("max_section"), dynamic.createInt(ChunkSectionPos.getSectionCoord(minY + height)))));
    }
}

