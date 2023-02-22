/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntitySimpleTransformFix;

public class EntityCatSplitFix
extends EntitySimpleTransformFix {
    public EntityCatSplitFix(Schema outputSchema, boolean changesType) {
        super("EntityCatSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
        if (Objects.equals("minecraft:ocelot", choice)) {
            int i = tag.get("CatType").asInt(0);
            if (i == 0) {
                String string = tag.get("Owner").asString("");
                String string2 = tag.get("OwnerUUID").asString("");
                if (string.length() > 0 || string2.length() > 0) {
                    tag.set("Trusting", tag.createBoolean(true));
                }
            } else if (i > 0 && i < 4) {
                tag = tag.set("CatType", tag.createInt(i));
                tag = tag.set("OwnerUUID", tag.createString(tag.get("OwnerUUID").asString("")));
                return Pair.of((Object)"minecraft:cat", (Object)tag);
            }
        }
        return Pair.of((Object)choice, tag);
    }
}

