/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.Formatting;

public final class Rarity
extends Enum<Rarity> {
    public static final /* enum */ Rarity COMMON = new Rarity(Formatting.WHITE);
    public static final /* enum */ Rarity UNCOMMON = new Rarity(Formatting.YELLOW);
    public static final /* enum */ Rarity RARE = new Rarity(Formatting.AQUA);
    public static final /* enum */ Rarity EPIC = new Rarity(Formatting.LIGHT_PURPLE);
    public final Formatting formatting;
    private static final /* synthetic */ Rarity[] field_8905;

    public static Rarity[] values() {
        return (Rarity[])field_8905.clone();
    }

    public static Rarity valueOf(String string) {
        return Enum.valueOf(Rarity.class, string);
    }

    private Rarity(Formatting formatting) {
        this.formatting = formatting;
    }

    private static /* synthetic */ Rarity[] method_36683() {
        return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
    }

    static {
        field_8905 = Rarity.method_36683();
    }
}

