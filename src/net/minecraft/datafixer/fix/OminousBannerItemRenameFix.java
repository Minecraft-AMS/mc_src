/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.fix.ItemNbtFix;

public class OminousBannerItemRenameFix
extends ItemNbtFix {
    public OminousBannerItemRenameFix(Schema schema) {
        super(schema, "OminousBannerRenameFix", itemId -> itemId.equals("minecraft:white_banner"));
    }

    @Override
    protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
        Optional optional = dynamic.get("display").result();
        if (optional.isPresent()) {
            Dynamic dynamic2 = (Dynamic)optional.get();
            Optional optional2 = dynamic2.get("Name").asString().result();
            if (optional2.isPresent()) {
                String string = (String)optional2.get();
                string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
                dynamic2 = dynamic2.set("Name", dynamic2.createString(string));
            }
            return dynamic.set("display", dynamic2);
        }
        return dynamic;
    }
}

