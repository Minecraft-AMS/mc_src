/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public final class HorseMarking
extends Enum<HorseMarking> {
    public static final /* enum */ HorseMarking NONE = new HorseMarking(0);
    public static final /* enum */ HorseMarking WHITE = new HorseMarking(1);
    public static final /* enum */ HorseMarking WHITE_FIELD = new HorseMarking(2);
    public static final /* enum */ HorseMarking WHITE_DOTS = new HorseMarking(3);
    public static final /* enum */ HorseMarking BLACK_DOTS = new HorseMarking(4);
    private static final HorseMarking[] VALUES;
    private final int index;
    private static final /* synthetic */ HorseMarking[] field_23815;

    public static HorseMarking[] values() {
        return (HorseMarking[])field_23815.clone();
    }

    public static HorseMarking valueOf(String string) {
        return Enum.valueOf(HorseMarking.class, string);
    }

    private HorseMarking(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseMarking byIndex(int index) {
        return VALUES[index % VALUES.length];
    }

    private static /* synthetic */ HorseMarking[] method_36645() {
        return new HorseMarking[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
    }

    static {
        field_23815 = HorseMarking.method_36645();
        VALUES = (HorseMarking[])Arrays.stream(HorseMarking.values()).sorted(Comparator.comparingInt(HorseMarking::getIndex)).toArray(HorseMarking[]::new);
    }
}

