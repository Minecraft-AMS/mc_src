/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityBannerColorFix
extends ChoiceFix {
    public BlockEntityBannerColorFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixBannerColor(Dynamic<?> tag2) {
        tag2 = tag2.update("Base", tag -> tag.createInt(15 - tag.asInt(0)));
        tag2 = tag2.update("Patterns", dynamic -> (Dynamic)DataFixUtils.orElse(dynamic.asStreamOpt().map(stream -> stream.map(dynamic -> dynamic.update("Color", tag -> tag.createInt(15 - tag.asInt(0))))).map(arg_0 -> ((Dynamic)dynamic).createList(arg_0)), (Object)dynamic));
        return tag2;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixBannerColor);
    }
}

