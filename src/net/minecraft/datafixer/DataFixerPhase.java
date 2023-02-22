/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer;

public final class DataFixerPhase
extends Enum<DataFixerPhase> {
    public static final /* enum */ DataFixerPhase UNINITIALIZED_UNOPTIMIZED = new DataFixerPhase();
    public static final /* enum */ DataFixerPhase UNINITIALIZED_OPTIMIZED = new DataFixerPhase();
    public static final /* enum */ DataFixerPhase INITIALIZED_UNOPTIMIZED = new DataFixerPhase();
    public static final /* enum */ DataFixerPhase INITIALIZED_OPTIMIZED = new DataFixerPhase();
    private static final /* synthetic */ DataFixerPhase[] field_38843;

    public static DataFixerPhase[] values() {
        return (DataFixerPhase[])field_38843.clone();
    }

    public static DataFixerPhase valueOf(String string) {
        return Enum.valueOf(DataFixerPhase.class, string);
    }

    private static /* synthetic */ DataFixerPhase[] method_43254() {
        return new DataFixerPhase[]{UNINITIALIZED_UNOPTIMIZED, UNINITIALIZED_OPTIMIZED, INITIALIZED_UNOPTIMIZED, INITIALIZED_OPTIMIZED};
    }

    static {
        field_38843 = DataFixerPhase.method_43254();
    }
}

