/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class Unit
extends Enum<Unit> {
    public static final /* enum */ Unit INSTANCE = new Unit();
    private static final /* synthetic */ Unit[] field_17275;

    public static Unit[] values() {
        return (Unit[])field_17275.clone();
    }

    public static Unit valueOf(String string) {
        return Enum.valueOf(Unit.class, string);
    }

    private static /* synthetic */ Unit[] method_36588() {
        return new Unit[]{INSTANCE};
    }

    static {
        field_17275 = Unit.method_36588();
    }
}

