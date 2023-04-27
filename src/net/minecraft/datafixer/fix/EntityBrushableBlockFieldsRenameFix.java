/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityBrushableBlockFieldsRenameFix
extends ChoiceFix {
    public EntityBrushableBlockFieldsRenameFix(Schema outputSchema) {
        super(outputSchema, false, "EntityBrushableBlockFieldsRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:brushable_block");
    }

    public Dynamic<?> renameFields(Dynamic<?> dynamic) {
        return this.renameField(this.renameField(dynamic, "loot_table", "LootTable"), "loot_table_seed", "LootTableSeed");
    }

    private Dynamic<?> renameField(Dynamic<?> dynamic, String oldName, String newName) {
        Optional optional = dynamic.get(oldName).result();
        Optional<Dynamic> optional2 = optional.map(dynamic2 -> dynamic.remove(oldName).set(newName, dynamic2));
        return (Dynamic)DataFixUtils.orElse(optional2, dynamic);
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::renameFields);
    }
}

