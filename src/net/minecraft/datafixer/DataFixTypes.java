/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixer.TypeReferences;

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

    public DSL.TypeReference getTypeReference() {
        return this.typeReference;
    }

    private static /* synthetic */ DataFixTypes[] method_36589() {
        return new DataFixTypes[]{LEVEL, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
    }

    static {
        field_19223 = DataFixTypes.method_36589();
    }
}

