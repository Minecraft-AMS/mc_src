/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class DoubleBlockHalf
extends Enum<DoubleBlockHalf>
implements StringIdentifiable {
    public static final /* enum */ DoubleBlockHalf UPPER = new DoubleBlockHalf();
    public static final /* enum */ DoubleBlockHalf LOWER = new DoubleBlockHalf();
    private static final /* synthetic */ DoubleBlockHalf[] field_12608;

    public static DoubleBlockHalf[] values() {
        return (DoubleBlockHalf[])field_12608.clone();
    }

    public static DoubleBlockHalf valueOf(String string) {
        return Enum.valueOf(DoubleBlockHalf.class, string);
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this == UPPER ? "upper" : "lower";
    }

    private static /* synthetic */ DoubleBlockHalf[] method_36727() {
        return new DoubleBlockHalf[]{UPPER, LOWER};
    }

    static {
        field_12608 = DoubleBlockHalf.method_36727();
    }
}

