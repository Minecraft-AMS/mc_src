/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.block.MapColor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class DyeColor
extends Enum<DyeColor>
implements StringIdentifiable {
    public static final /* enum */ DyeColor WHITE = new DyeColor(0, "white", 0xF9FFFE, MapColor.WHITE, 0xF0F0F0, 0xFFFFFF);
    public static final /* enum */ DyeColor ORANGE = new DyeColor(1, "orange", 16351261, MapColor.ORANGE, 15435844, 16738335);
    public static final /* enum */ DyeColor MAGENTA = new DyeColor(2, "magenta", 13061821, MapColor.MAGENTA, 12801229, 0xFF00FF);
    public static final /* enum */ DyeColor LIGHT_BLUE = new DyeColor(3, "light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901);
    public static final /* enum */ DyeColor YELLOW = new DyeColor(4, "yellow", 16701501, MapColor.YELLOW, 14602026, 0xFFFF00);
    public static final /* enum */ DyeColor LIME = new DyeColor(5, "lime", 8439583, MapColor.LIME, 4312372, 0xBFFF00);
    public static final /* enum */ DyeColor PINK = new DyeColor(6, "pink", 15961002, MapColor.PINK, 14188952, 16738740);
    public static final /* enum */ DyeColor GRAY = new DyeColor(7, "gray", 4673362, MapColor.GRAY, 0x434343, 0x808080);
    public static final /* enum */ DyeColor LIGHT_GRAY = new DyeColor(8, "light_gray", 0x9D9D97, MapColor.LIGHT_GRAY, 0xABABAB, 0xD3D3D3);
    public static final /* enum */ DyeColor CYAN = new DyeColor(9, "cyan", 1481884, MapColor.CYAN, 2651799, 65535);
    public static final /* enum */ DyeColor PURPLE = new DyeColor(10, "purple", 8991416, MapColor.PURPLE, 8073150, 10494192);
    public static final /* enum */ DyeColor BLUE = new DyeColor(11, "blue", 3949738, MapColor.BLUE, 2437522, 255);
    public static final /* enum */ DyeColor BROWN = new DyeColor(12, "brown", 8606770, MapColor.BROWN, 5320730, 9127187);
    public static final /* enum */ DyeColor GREEN = new DyeColor(13, "green", 6192150, MapColor.GREEN, 3887386, 65280);
    public static final /* enum */ DyeColor RED = new DyeColor(14, "red", 11546150, MapColor.RED, 11743532, 0xFF0000);
    public static final /* enum */ DyeColor BLACK = new DyeColor(15, "black", 0x1D1D21, MapColor.BLACK, 0x1E1B1B, 0);
    private static final IntFunction<DyeColor> BY_ID;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    public static final StringIdentifiable.Codec<DyeColor> CODEC;
    private final int id;
    private final String name;
    private final MapColor mapColor;
    private final float[] colorComponents;
    private final int fireworkColor;
    private final int signColor;
    private static final /* synthetic */ DyeColor[] field_7953;

    public static DyeColor[] values() {
        return (DyeColor[])field_7953.clone();
    }

    public static DyeColor valueOf(String string) {
        return Enum.valueOf(DyeColor.class, string);
    }

    private DyeColor(int id, String name, int color, MapColor mapColor, int fireworkColor, int signColor) {
        this.id = id;
        this.name = name;
        this.mapColor = mapColor;
        this.signColor = signColor;
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF) >> 0;
        this.colorComponents = new float[]{(float)j / 255.0f, (float)k / 255.0f, (float)l / 255.0f};
        this.fireworkColor = fireworkColor;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public float[] getColorComponents() {
        return this.colorComponents;
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    public int getSignColor() {
        return this.signColor;
    }

    public static DyeColor byId(int id) {
        return BY_ID.apply(id);
    }

    @Nullable
    @Contract(value="_,!null->!null;_,null->_")
    public static DyeColor byName(String name, @Nullable DyeColor defaultColor) {
        DyeColor dyeColor = CODEC.byId(name);
        return dyeColor != null ? dyeColor : defaultColor;
    }

    @Nullable
    public static DyeColor byFireworkColor(int color) {
        return (DyeColor)BY_FIREWORK_COLOR.get(color);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ DyeColor[] method_36676() {
        return new DyeColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
    }

    static {
        field_7953 = DyeColor.method_36676();
        BY_ID = ValueLists.createIdToValueFunction(DyeColor::getId, DyeColor.values(), ValueLists.OutOfBoundsHandling.ZERO);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color.fireworkColor, color -> color)));
        CODEC = StringIdentifiable.createCodec(DyeColor::values);
    }
}

