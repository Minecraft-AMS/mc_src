/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;

public class Schema2704
extends IdentifierNormalizingSchema {
    public Schema2704(int i, Schema schema) {
        super(i, schema);
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, String name) {
        schema.register(entityTypes, name, () -> Schema100.targetItems(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        Schema2704.registerEntity(schema, map, "minecraft:goat");
        return map;
    }
}
