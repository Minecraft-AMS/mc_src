/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.function.IntFunction;
import net.minecraft.util.function.ValueLists;

public final class HorseMarking
extends Enum<HorseMarking> {
    public static final /* enum */ HorseMarking NONE = new HorseMarking(0);
    public static final /* enum */ HorseMarking WHITE = new HorseMarking(1);
    public static final /* enum */ HorseMarking WHITE_FIELD = new HorseMarking(2);
    public static final /* enum */ HorseMarking WHITE_DOTS = new HorseMarking(3);
    public static final /* enum */ HorseMarking BLACK_DOTS = new HorseMarking(4);
    private static final IntFunction<HorseMarking> BY_ID;
    private final int id;
    private static final /* synthetic */ HorseMarking[] field_23815;

    public static HorseMarking[] values() {
        return (HorseMarking[])field_23815.clone();
    }

    public static HorseMarking valueOf(String string) {
        return Enum.valueOf(HorseMarking.class, string);
    }

    private HorseMarking(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static HorseMarking byIndex(int index) {
        return BY_ID.apply(index);
    }

    private static /* synthetic */ HorseMarking[] method_36645() {
        return new HorseMarking[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
    }

    static {
        field_23815 = HorseMarking.method_36645();
        BY_ID = ValueLists.createIdToValueFunction(HorseMarking::getId, HorseMarking.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

