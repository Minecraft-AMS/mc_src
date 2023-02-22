/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema
extends Schema {
    public IdentifierNormalizingSchema(int i, Schema schema) {
        super(i, schema);
    }

    public static String normalize(String id) {
        Identifier identifier = Identifier.tryParse(id);
        if (identifier != null) {
            return identifier.toString();
        }
        return id;
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
    }
}

