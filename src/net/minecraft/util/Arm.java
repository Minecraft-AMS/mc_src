/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class Arm
extends Enum<Arm> {
    public static final /* enum */ Arm LEFT = new Arm(new TranslatableText("options.mainHand.left"));
    public static final /* enum */ Arm RIGHT = new Arm(new TranslatableText("options.mainHand.right"));
    private final Text optionName;
    private static final /* synthetic */ Arm[] field_6180;

    public static Arm[] values() {
        return (Arm[])field_6180.clone();
    }

    public static Arm valueOf(String string) {
        return Enum.valueOf(Arm.class, string);
    }

    private Arm(Text optionName) {
        this.optionName = optionName;
    }

    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    public String toString() {
        return this.optionName.getString();
    }

    public Text getOptionName() {
        return this.optionName;
    }

    private static /* synthetic */ Arm[] method_36606() {
        return new Arm[]{LEFT, RIGHT};
    }

    static {
        field_6180 = Arm.method_36606();
    }
}

