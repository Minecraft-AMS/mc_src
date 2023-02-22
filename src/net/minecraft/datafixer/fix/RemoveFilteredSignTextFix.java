/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class RemoveFilteredSignTextFix
extends ChoiceFix {
    public RemoveFilteredSignTextFix(Schema schema) {
        super(schema, false, "Remove filtered text from signs", TypeReferences.BLOCK_ENTITY, "minecraft:sign");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), blockEntity -> blockEntity.remove("FilteredText1").remove("FilteredText2").remove("FilteredText3").remove("FilteredText4"));
    }
}

