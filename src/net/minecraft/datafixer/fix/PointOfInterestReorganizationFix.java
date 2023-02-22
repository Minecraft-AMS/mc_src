/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class PointOfInterestReorganizationFix
extends DataFix {
    public PointOfInterestReorganizationFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.POI_CHUNK.typeName(), (Type)DSL.remainderType());
        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI reorganization", type, dynamicOps -> pair -> pair.mapSecond(PointOfInterestReorganizationFix::reorganize));
    }

    private static <T> Dynamic<T> reorganize(Dynamic<T> tag) {
        HashMap map = Maps.newHashMap();
        for (int i = 0; i < 16; ++i) {
            String string = String.valueOf(i);
            Optional optional = tag.get(string).get();
            if (!optional.isPresent()) continue;
            Dynamic dynamic = (Dynamic)optional.get();
            Dynamic dynamic2 = tag.createMap((Map)ImmutableMap.of((Object)tag.createString("Records"), (Object)dynamic));
            map.put(tag.createInt(i), dynamic2);
            tag = tag.remove(string);
        }
        return tag.set("Sections", tag.createMap((Map)map));
    }
}

