/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class UseAction
extends Enum<UseAction> {
    public static final /* enum */ UseAction NONE = new UseAction();
    public static final /* enum */ UseAction EAT = new UseAction();
    public static final /* enum */ UseAction DRINK = new UseAction();
    public static final /* enum */ UseAction BLOCK = new UseAction();
    public static final /* enum */ UseAction BOW = new UseAction();
    public static final /* enum */ UseAction SPEAR = new UseAction();
    public static final /* enum */ UseAction CROSSBOW = new UseAction();
    public static final /* enum */ UseAction SPYGLASS = new UseAction();
    public static final /* enum */ UseAction TOOT_HORN = new UseAction();
    private static final /* synthetic */ UseAction[] field_8948;

    public static UseAction[] values() {
        return (UseAction[])field_8948.clone();
    }

    public static UseAction valueOf(String string) {
        return Enum.valueOf(UseAction.class, string);
    }

    private static /* synthetic */ UseAction[] method_36686() {
        return new UseAction[]{NONE, EAT, DRINK, BLOCK, BOW, SPEAR, CROSSBOW, SPYGLASS, TOOT_HORN};
    }

    static {
        field_8948 = UseAction.method_36686();
    }
}

