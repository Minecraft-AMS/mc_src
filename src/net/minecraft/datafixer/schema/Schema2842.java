/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class Schema2842
extends IdentifierNormalizingSchema {
    public Schema2842(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, TypeReferences.CHUNK, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ENTITY_TREE.in(schema)), (String)"block_entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.or((TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema), (TypeTemplate)DSL.remainder())), (String)"block_ticks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))), (String)"sections", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"biomes", (TypeTemplate)DSL.optionalFields((String)"palette", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BIOME.in(schema))), (String)"block_states", (TypeTemplate)DSL.optionalFields((String)"palette", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_STATE.in(schema))))), (String)"structures", (TypeTemplate)DSL.optionalFields((String)"starts", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.STRUCTURE_FEATURE.in(schema)))));
    }
}

