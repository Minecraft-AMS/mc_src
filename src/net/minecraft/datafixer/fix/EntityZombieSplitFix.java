/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntitySimpleTransformFix;

public class EntityZombieSplitFix
extends EntitySimpleTransformFix {
    public EntityZombieSplitFix(Schema schema, boolean bl) {
        super("EntityZombieSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> dynamic) {
        if (Objects.equals("Zombie", choice)) {
            String string = "Zombie";
            int i = dynamic.get("ZombieType").asInt(0);
            switch (i) {
                default: {
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    string = "ZombieVillager";
                    dynamic = dynamic.set("Profession", dynamic.createInt(i - 1));
                    break;
                }
                case 6: {
                    string = "Husk";
                }
            }
            dynamic = dynamic.remove("ZombieType");
            return Pair.of((Object)string, (Object)dynamic);
        }
        return Pair.of((Object)choice, dynamic);
    }
}

