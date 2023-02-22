/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class CauldronRenameFix
extends DataFix {
    public CauldronRenameFix(Schema outputSchema, boolean bl) {
        super(outputSchema, bl);
    }

    private static Dynamic<?> rename(Dynamic<?> dynamic) {
        Optional optional = dynamic.get("Name").asString().result();
        if (optional.equals(Optional.of("minecraft:cauldron"))) {
            Dynamic dynamic2 = dynamic.get("Properties").orElseEmptyMap();
            if (dynamic2.get("level").asString("0").equals("0")) {
                return dynamic.remove("Properties");
            }
            return dynamic.set("Name", dynamic.createString("minecraft:water_cauldron"));
        }
        return dynamic;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("cauldron_rename_fix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), CauldronRenameFix::rename));
    }
}

