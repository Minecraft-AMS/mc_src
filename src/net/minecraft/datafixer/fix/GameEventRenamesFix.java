/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class GameEventRenamesFix
extends DataFix {
    private final String name;
    private final Map<String, String> renames;
    private final DSL.TypeReference field_38383;

    public GameEventRenamesFix(Schema schema, DSL.TypeReference typeReference, Map<String, String> renames) {
        this(schema, typeReference, typeReference.typeName() + "-renames at version: " + schema.getVersionKey(), renames);
    }

    public GameEventRenamesFix(Schema schema, DSL.TypeReference typeReference, String name, Map<String, String> renames) {
        super(schema, false);
        this.renames = renames;
        this.name = name;
        this.field_38383 = typeReference;
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)this.field_38383.typeName(), IdentifierNormalizingSchema.getIdentifierType());
        if (!Objects.equals(type, this.getInputSchema().getType(this.field_38383))) {
            throw new IllegalStateException("\"" + this.field_38383.typeName() + "\" type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(string -> this.renames.getOrDefault(string, (String)string)));
    }
}

