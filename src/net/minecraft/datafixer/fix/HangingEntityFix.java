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
import net.minecraft.datafixer.TypeReferences;

public class HangingEntityFix
extends DataFix {
    private static final int[][] OFFSETS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

    public HangingEntityFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixDecorationPosition(Dynamic<?> tag, boolean isPainting, boolean isItemFrame) {
        if ((isPainting || isItemFrame) && !tag.get("Facing").asNumber().isPresent()) {
            int i;
            if (tag.get("Direction").asNumber().isPresent()) {
                i = tag.get("Direction").asByte((byte)0) % OFFSETS.length;
                int[] is = OFFSETS[i];
                tag = tag.set("TileX", tag.createInt(tag.get("TileX").asInt(0) + is[0]));
                tag = tag.set("TileY", tag.createInt(tag.get("TileY").asInt(0) + is[1]));
                tag = tag.set("TileZ", tag.createInt(tag.get("TileZ").asInt(0) + is[2]));
                tag = tag.remove("Direction");
                if (isItemFrame && tag.get("ItemRotation").asNumber().isPresent()) {
                    tag = tag.set("ItemRotation", tag.createByte((byte)(tag.get("ItemRotation").asByte((byte)0) * 2)));
                }
            } else {
                i = tag.get("Dir").asByte((byte)0) % OFFSETS.length;
                tag = tag.remove("Dir");
            }
            tag = tag.set("Facing", tag.createByte((byte)i));
        }
        return tag;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
        OpticFinder opticFinder = DSL.namedChoice((String)"Painting", (Type)type);
        Type type2 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
        OpticFinder opticFinder2 = DSL.namedChoice((String)"ItemFrame", (Type)type2);
        Type type3 = this.getInputSchema().getType(TypeReferences.ENTITY);
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped("EntityPaintingFix", type3, typed2 -> typed2.updateTyped(opticFinder, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition((Dynamic<?>)dynamic, true, false))));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type3, typed2 -> typed2.updateTyped(opticFinder2, type2, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition((Dynamic<?>)dynamic, false, true))));
        return TypeRewriteRule.seq((TypeRewriteRule)typeRewriteRule, (TypeRewriteRule)typeRewriteRule2);
    }
}

