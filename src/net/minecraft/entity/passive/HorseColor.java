/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public final class HorseColor
extends Enum<HorseColor> {
    public static final /* enum */ HorseColor WHITE = new HorseColor(0);
    public static final /* enum */ HorseColor CREAMY = new HorseColor(1);
    public static final /* enum */ HorseColor CHESTNUT = new HorseColor(2);
    public static final /* enum */ HorseColor BROWN = new HorseColor(3);
    public static final /* enum */ HorseColor BLACK = new HorseColor(4);
    public static final /* enum */ HorseColor GRAY = new HorseColor(5);
    public static final /* enum */ HorseColor DARKBROWN = new HorseColor(6);
    private static final HorseColor[] VALUES;
    private final int index;
    private static final /* synthetic */ HorseColor[] field_23825;

    public static HorseColor[] values() {
        return (HorseColor[])field_23825.clone();
    }

    public static HorseColor valueOf(String string) {
        return Enum.valueOf(HorseColor.class, string);
    }

    private HorseColor(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseColor byIndex(int index) {
        return VALUES[index % VALUES.length];
    }

    private static /* synthetic */ HorseColor[] method_36646() {
        return new HorseColor[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARKBROWN};
    }

    static {
        field_23825 = HorseColor.method_36646();
        VALUES = (HorseColor[])Arrays.stream(HorseColor.values()).sorted(Comparator.comparingInt(HorseColor::getIndex)).toArray(HorseColor[]::new);
    }
}

