/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

public final class StructureWeightType
extends Enum<StructureWeightType> {
    public static final /* enum */ StructureWeightType NONE = new StructureWeightType();
    public static final /* enum */ StructureWeightType BURY = new StructureWeightType();
    public static final /* enum */ StructureWeightType BEARD = new StructureWeightType();
    private static final /* synthetic */ StructureWeightType[] field_28925;

    public static StructureWeightType[] values() {
        return (StructureWeightType[])field_28925.clone();
    }

    public static StructureWeightType valueOf(String string) {
        return Enum.valueOf(StructureWeightType.class, string);
    }

    private static /* synthetic */ StructureWeightType[] method_36756() {
        return new StructureWeightType[]{NONE, BURY, BEARD};
    }

    static {
        field_28925 = StructureWeightType.method_36756();
    }
}

