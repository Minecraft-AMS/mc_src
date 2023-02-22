/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.render.entity;

import net.minecraft.text.Text;

public final class PlayerModelPart
extends Enum<PlayerModelPart> {
    public static final /* enum */ PlayerModelPart CAPE = new PlayerModelPart(0, "cape");
    public static final /* enum */ PlayerModelPart JACKET = new PlayerModelPart(1, "jacket");
    public static final /* enum */ PlayerModelPart LEFT_SLEEVE = new PlayerModelPart(2, "left_sleeve");
    public static final /* enum */ PlayerModelPart RIGHT_SLEEVE = new PlayerModelPart(3, "right_sleeve");
    public static final /* enum */ PlayerModelPart LEFT_PANTS_LEG = new PlayerModelPart(4, "left_pants_leg");
    public static final /* enum */ PlayerModelPart RIGHT_PANTS_LEG = new PlayerModelPart(5, "right_pants_leg");
    public static final /* enum */ PlayerModelPart HAT = new PlayerModelPart(6, "hat");
    private final int id;
    private final int bitFlag;
    private final String name;
    private final Text optionName;
    private static final /* synthetic */ PlayerModelPart[] field_7562;

    public static PlayerModelPart[] values() {
        return (PlayerModelPart[])field_7562.clone();
    }

    public static PlayerModelPart valueOf(String string) {
        return Enum.valueOf(PlayerModelPart.class, string);
    }

    private PlayerModelPart(int id, String name) {
        this.id = id;
        this.bitFlag = 1 << id;
        this.name = name;
        this.optionName = Text.translatable("options.modelPart." + name);
    }

    public int getBitFlag() {
        return this.bitFlag;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getOptionName() {
        return this.optionName;
    }

    private static /* synthetic */ PlayerModelPart[] method_36662() {
        return new PlayerModelPart[]{CAPE, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS_LEG, RIGHT_PANTS_LEG, HAT};
    }

    static {
        field_7562 = PlayerModelPart.method_36662();
    }
}

