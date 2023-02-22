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

public class CatTypeFix
extends ChoiceFix {
    public CatTypeFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
    }

    public Dynamic<?> fixCatTypeData(Dynamic<?> tag) {
        if (tag.get("CatType").asInt(0) == 9) {
            return tag.set("CatType", tag.createInt(10));
        }
        return tag;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixCatTypeData);
    }
}

