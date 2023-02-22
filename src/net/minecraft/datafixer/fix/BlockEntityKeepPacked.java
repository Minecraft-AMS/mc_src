/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityKeepPacked
extends ChoiceFix {
    public BlockEntityKeepPacked(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
    }

    private static Dynamic<?> keepPacked(Dynamic<?> tag) {
        return tag.set("keepPacked", tag.createBoolean(true));
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::keepPacked);
    }
}

