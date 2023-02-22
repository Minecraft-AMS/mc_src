/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data.validate;

import com.mojang.logging.LogUtils;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructureTemplate;
import org.slf4j.Logger;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public NbtCompound write(String name, NbtCompound nbt) {
        if (name.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.update(name, nbt);
        }
        return nbt;
    }

    public static NbtCompound update(String name, NbtCompound nbt) {
        return StructureValidatorProvider.internalUpdate(name, StructureValidatorProvider.addDataVersion(nbt));
    }

    private static NbtCompound addDataVersion(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", 99)) {
            nbt.putInt("DataVersion", 500);
        }
        return nbt;
    }

    private static NbtCompound internalUpdate(String name, NbtCompound nbt) {
        StructureTemplate structureTemplate = new StructureTemplate();
        int i = nbt.getInt("DataVersion");
        int j = 3200;
        if (i < 3200) {
            LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{i, 3200, name});
        }
        NbtCompound nbtCompound = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, nbt, i);
        structureTemplate.readNbt(Registries.BLOCK.getReadOnlyWrapper(), nbtCompound);
        return structureTemplate.writeNbt(new NbtCompound());
    }
}

