/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.EntityTransformFix;

public class EntityHorseSplitFix
extends EntityTransformFix {
    public EntityHorseSplitFix(Schema outputSchema, boolean changesType) {
        super("EntityHorseSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", choice)) {
            int i = dynamic.get("Type").asInt(0);
            String string = switch (i) {
                default -> "Horse";
                case 1 -> "Donkey";
                case 2 -> "Mule";
                case 3 -> "ZombieHorse";
                case 4 -> "SkeletonHorse";
            };
            dynamic.remove("Type");
            Type type = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
            return Pair.of((Object)string, (Object)((Typed)((Pair)typed.write().flatMap(arg_0 -> ((Type)type).readTyped(arg_0)).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst()));
        }
        return Pair.of((Object)choice, typed);
    }
}

