/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public final class DataFixTypes
extends Enum<DataFixTypes> {
    public static final /* enum */ DataFixTypes LEVEL = new DataFixTypes(TypeReferences.LEVEL);
    public static final /* enum */ DataFixTypes PLAYER = new DataFixTypes(TypeReferences.PLAYER);
    public static final /* enum */ DataFixTypes CHUNK = new DataFixTypes(TypeReferences.CHUNK);
    public static final /* enum */ DataFixTypes HOTBAR = new DataFixTypes(TypeReferences.HOTBAR);
    public static final /* enum */ DataFixTypes OPTIONS = new DataFixTypes(TypeReferences.OPTIONS);
    public static final /* enum */ DataFixTypes STRUCTURE = new DataFixTypes(TypeReferences.STRUCTURE);
    public static final /* enum */ DataFixTypes STATS = new DataFixTypes(TypeReferences.STATS);
    public static final /* enum */ DataFixTypes SAVED_DATA = new DataFixTypes(TypeReferences.SAVED_DATA);
    public static final /* enum */ DataFixTypes ADVANCEMENTS = new DataFixTypes(TypeReferences.ADVANCEMENTS);
    public static final /* enum */ DataFixTypes POI_CHUNK = new DataFixTypes(TypeReferences.POI_CHUNK);
    public static final /* enum */ DataFixTypes WORLD_GEN_SETTINGS = new DataFixTypes(TypeReferences.WORLD_GEN_SETTINGS);
    public static final /* enum */ DataFixTypes ENTITY_CHUNK = new DataFixTypes(TypeReferences.ENTITY_CHUNK);
    public static final Set<DSL.TypeReference> REQUIRED_TYPES;
    private final DSL.TypeReference typeReference;
    private static final /* synthetic */ DataFixTypes[] field_19223;

    public static DataFixTypes[] values() {
        return (DataFixTypes[])field_19223.clone();
    }

    public static DataFixTypes valueOf(String string) {
        return Enum.valueOf(DataFixTypes.class, string);
    }

    private DataFixTypes(DSL.TypeReference typeReference) {
        this.typeReference = typeReference;
    }

    private static int getSaveVersionId() {
        return SharedConstants.getGameVersion().getSaveVersion().getId();
    }

    public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion) {
        return dataFixer.update(this.typeReference, dynamic, oldVersion, newVersion);
    }

    public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion) {
        return this.update(dataFixer, dynamic, oldVersion, DataFixTypes.getSaveVersionId());
    }

    public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion) {
        return (NbtCompound)this.update(dataFixer, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt), oldVersion, newVersion).getValue();
    }

    public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion) {
        return this.update(dataFixer, nbt, oldVersion, DataFixTypes.getSaveVersionId());
    }

    private static /* synthetic */ DataFixTypes[] method_36589() {
        return new DataFixTypes[]{LEVEL, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
    }

    static {
        field_19223 = DataFixTypes.method_36589();
        REQUIRED_TYPES = Set.of(DataFixTypes.LEVEL.typeReference);
    }
}

