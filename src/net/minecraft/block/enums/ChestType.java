/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class ChestType
extends Enum<ChestType>
implements StringIdentifiable {
    public static final /* enum */ ChestType SINGLE = new ChestType("single", 0);
    public static final /* enum */ ChestType LEFT = new ChestType("left", 2);
    public static final /* enum */ ChestType RIGHT = new ChestType("right", 1);
    public static final ChestType[] VALUES;
    private final String name;
    private final int opposite;
    private static final /* synthetic */ ChestType[] field_12573;

    public static ChestType[] values() {
        return (ChestType[])field_12573.clone();
    }

    public static ChestType valueOf(String string) {
        return Enum.valueOf(ChestType.class, string);
    }

    private ChestType(String name, int opposite) {
        this.name = name;
        this.opposite = opposite;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public ChestType getOpposite() {
        return VALUES[this.opposite];
    }

    private static /* synthetic */ ChestType[] method_36724() {
        return new ChestType[]{SINGLE, LEFT, RIGHT};
    }

    static {
        field_12573 = ChestType.method_36724();
        VALUES = ChestType.values();
    }
}

