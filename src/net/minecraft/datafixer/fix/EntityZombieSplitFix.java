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

public class EntityZombieSplitFix
extends EntitySimpleTransformFix {
    public EntityZombieSplitFix(Schema outputSchema, boolean changesType) {
        super("EntityZombieSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
        if (Objects.equals("Zombie", choice)) {
            String string = "Zombie";
            int i = tag.get("ZombieType").asInt(0);
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
                    tag = tag.set("Profession", tag.createInt(i - 1));
                    break;
                }
                case 6: {
                    string = "Husk";
                }
            }
            tag = tag.remove("ZombieType");
            return Pair.of((Object)string, (Object)tag);
        }
        return Pair.of((Object)choice, tag);
    }
}

