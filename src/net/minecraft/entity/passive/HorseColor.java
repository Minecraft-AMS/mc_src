/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class HorseColor
extends Enum<HorseColor>
implements StringIdentifiable {
    public static final /* enum */ HorseColor WHITE = new HorseColor(0, "white");
    public static final /* enum */ HorseColor CREAMY = new HorseColor(1, "creamy");
    public static final /* enum */ HorseColor CHESTNUT = new HorseColor(2, "chestnut");
    public static final /* enum */ HorseColor BROWN = new HorseColor(3, "brown");
    public static final /* enum */ HorseColor BLACK = new HorseColor(4, "black");
    public static final /* enum */ HorseColor GRAY = new HorseColor(5, "gray");
    public static final /* enum */ HorseColor DARK_BROWN = new HorseColor(6, "dark_brown");
    public static final Codec<HorseColor> CODEC;
    private static final IntFunction<HorseColor> BY_ID;
    private final int id;
    private final String name;
    private static final /* synthetic */ HorseColor[] field_23825;

    public static HorseColor[] values() {
        return (HorseColor[])field_23825.clone();
    }

    public static HorseColor valueOf(String string) {
        return Enum.valueOf(HorseColor.class, string);
    }

    private HorseColor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public static HorseColor byId(int id) {
        return BY_ID.apply(id);
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ HorseColor[] method_36646() {
        return new HorseColor[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN};
    }

    static {
        field_23825 = HorseColor.method_36646();
        CODEC = StringIdentifiable.createCodec(HorseColor::values);
        BY_ID = ValueLists.createIdToValueFunction(HorseColor::getId, HorseColor.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

