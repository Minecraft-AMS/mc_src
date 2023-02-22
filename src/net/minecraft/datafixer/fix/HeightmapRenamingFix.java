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

public class HeightmapRenamingFix
extends DataFix {
    public HeightmapRenamingFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        return this.fixTypeEverywhereTyped("HeightmapRenamingFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::renameHeightmapTags)));
    }

    private Dynamic<?> renameHeightmapTags(Dynamic<?> tag) {
        Optional optional5;
        Optional optional4;
        Optional optional3;
        Optional optional = tag.get("Heightmaps").get();
        if (!optional.isPresent()) {
            return tag;
        }
        Dynamic dynamic = (Dynamic)optional.get();
        Optional optional2 = dynamic.get("LIQUID").get();
        if (optional2.isPresent()) {
            dynamic = dynamic.remove("LIQUID");
            dynamic = dynamic.set("WORLD_SURFACE_WG", (Dynamic)optional2.get());
        }
        if ((optional3 = dynamic.get("SOLID").get()).isPresent()) {
            dynamic = dynamic.remove("SOLID");
            dynamic = dynamic.set("OCEAN_FLOOR_WG", (Dynamic)optional3.get());
            dynamic = dynamic.set("OCEAN_FLOOR", (Dynamic)optional3.get());
        }
        if ((optional4 = dynamic.get("LIGHT").get()).isPresent()) {
            dynamic = dynamic.remove("LIGHT");
            dynamic = dynamic.set("LIGHT_BLOCKING", (Dynamic)optional4.get());
        }
        if ((optional5 = dynamic.get("RAIN").get()).isPresent()) {
            dynamic = dynamic.remove("RAIN");
            dynamic = dynamic.set("MOTION_BLOCKING", (Dynamic)optional5.get());
            dynamic = dynamic.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)optional5.get());
        }
        return tag.set("Heightmaps", dynamic);
    }
}

