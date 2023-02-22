/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.Text;

public class ItemLoreToTextFix
extends DataFix {
    public ItemLoreToTextFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("display", dynamic2 -> dynamic2.update("Lore", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asStreamOpt().map(ItemLoreToTextFix::fixLoreNbt).map(arg_0 -> ((Dynamic)dynamic).createList(arg_0)).result(), (Object)dynamic))))));
    }

    private static <T> Stream<Dynamic<T>> fixLoreNbt(Stream<Dynamic<T>> nbt) {
        return nbt.map(dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asString().map(ItemLoreToTextFix::componentize).map(arg_0 -> ((Dynamic)dynamic).createString(arg_0)).result(), (Object)dynamic));
    }

    private static String componentize(String string) {
        return Text.Serializer.toJson(Text.literal(string));
    }
}

