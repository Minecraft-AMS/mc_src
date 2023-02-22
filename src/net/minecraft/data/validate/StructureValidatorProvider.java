/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider
implements SnbtProvider.class_4460 {
    @Override
    public CompoundTag method_21674(String string, CompoundTag compoundTag) {
        if (string.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.method_16878(StructureValidatorProvider.method_16880(compoundTag));
        }
        return compoundTag;
    }

    private static CompoundTag method_16880(CompoundTag compoundTag) {
        if (!compoundTag.contains("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        return compoundTag;
    }

    private static CompoundTag method_16878(CompoundTag compoundTag) {
        Structure structure = new Structure();
        structure.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
        return structure.toTag(new CompoundTag());
    }
}

