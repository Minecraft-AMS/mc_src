/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class RenameChunkStatusFix
extends DataFix {
    private final String name;
    private final UnaryOperator<String> mapper;

    public RenameChunkStatusFix(Schema schema, String name, UnaryOperator<String> mapper) {
        super(schema, false);
        this.name = name;
        this.mapper = mapper;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(TypeReferences.CHUNK), typed -> typed.update(DSL.remainderFinder(), chunk -> chunk.update("Status", this::updateStatus).update("below_zero_retrogen", dynamic -> dynamic.update("target_status", this::updateStatus))));
    }

    private <T> Dynamic<T> updateStatus(Dynamic<T> status) {
        return (Dynamic)DataFixUtils.orElse(status.asString().result().map(this.mapper).map(arg_0 -> status.createString(arg_0)), status);
    }
}

