/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.TranslatableOption;

public final class Arm
extends Enum<Arm>
implements TranslatableOption {
    public static final /* enum */ Arm LEFT = new Arm(0, "options.mainHand.left");
    public static final /* enum */ Arm RIGHT = new Arm(1, "options.mainHand.right");
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ Arm[] field_6180;

    public static Arm[] values() {
        return (Arm[])field_6180.clone();
    }

    public static Arm valueOf(String string) {
        return Enum.valueOf(Arm.class, string);
    }

    private Arm(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    private static /* synthetic */ Arm[] method_36606() {
        return new Arm[]{LEFT, RIGHT};
    }

    static {
        field_6180 = Arm.method_36606();
    }
}

