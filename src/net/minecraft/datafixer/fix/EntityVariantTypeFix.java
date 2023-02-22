/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityVariantTypeFix
extends ChoiceFix {
    private final String variantKey;
    private final IntFunction<String> variantIntToId;

    public EntityVariantTypeFix(Schema schema, String name, DSL.TypeReference typeReference, String entityId, String variantKey, IntFunction<String> variantIntToId) {
        super(schema, false, name, typeReference, entityId);
        this.variantKey = variantKey;
        this.variantIntToId = variantIntToId;
    }

    private static <T> Dynamic<T> method_43072(Dynamic<T> dynamic, String string, String string2, Function<Dynamic<T>, Dynamic<T>> function) {
        return dynamic.map(object3 -> {
            DynamicOps dynamicOps = dynamic.getOps();
            Function<Object, Object> function2 = object -> ((Dynamic)function.apply(new Dynamic(dynamicOps, object))).getValue();
            return dynamicOps.get(object3, string).map(object2 -> dynamicOps.set(object3, string2, function2.apply(object2))).result().orElse(object3);
        });
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), dynamic2 -> EntityVariantTypeFix.method_43072(dynamic2, this.variantKey, "variant", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asNumber().map(number -> dynamic.createString(this.variantIntToId.apply(number.intValue()))).result(), (Object)dynamic)));
    }
}

