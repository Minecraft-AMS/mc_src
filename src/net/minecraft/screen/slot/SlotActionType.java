/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.slot;

public final class SlotActionType
extends Enum<SlotActionType> {
    public static final /* enum */ SlotActionType PICKUP = new SlotActionType();
    public static final /* enum */ SlotActionType QUICK_MOVE = new SlotActionType();
    public static final /* enum */ SlotActionType SWAP = new SlotActionType();
    public static final /* enum */ SlotActionType CLONE = new SlotActionType();
    public static final /* enum */ SlotActionType THROW = new SlotActionType();
    public static final /* enum */ SlotActionType QUICK_CRAFT = new SlotActionType();
    public static final /* enum */ SlotActionType PICKUP_ALL = new SlotActionType();
    private static final /* synthetic */ SlotActionType[] field_7792;

    public static SlotActionType[] values() {
        return (SlotActionType[])field_7792.clone();
    }

    public static SlotActionType valueOf(String string) {
        return Enum.valueOf(SlotActionType.class, string);
    }

    private static /* synthetic */ SlotActionType[] method_36673() {
        return new SlotActionType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
    }

    static {
        field_7792 = SlotActionType.method_36673();
    }
}

