/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum DyeColor implements StringIdentifiable
{
    WHITE(0, "white", 0xF9FFFE, MaterialColor.WHITE, 0xF0F0F0, 0xFFFFFF),
    ORANGE(1, "orange", 16351261, MaterialColor.ORANGE, 15435844, 16738335),
    MAGENTA(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229, 0xFF00FF),
    LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.LIGHT_BLUE, 6719955, 10141901),
    YELLOW(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026, 0xFFFF00),
    LIME(5, "lime", 8439583, MaterialColor.LIME, 4312372, 0xBFFF00),
    PINK(6, "pink", 15961002, MaterialColor.PINK, 14188952, 16738740),
    GRAY(7, "gray", 4673362, MaterialColor.GRAY, 0x434343, 0x808080),
    LIGHT_GRAY(8, "light_gray", 0x9D9D97, MaterialColor.LIGHT_GRAY, 0xABABAB, 0xD3D3D3),
    CYAN(9, "cyan", 1481884, MaterialColor.CYAN, 2651799, 65535),
    PURPLE(10, "purple", 8991416, MaterialColor.PURPLE, 8073150, 10494192),
    BLUE(11, "blue", 3949738, MaterialColor.BLUE, 2437522, 255),
    BROWN(12, "brown", 8606770, MaterialColor.BROWN, 5320730, 9127187),
    GREEN(13, "green", 6192150, MaterialColor.GREEN, 3887386, 65280),
    RED(14, "red", 11546150, MaterialColor.RED, 11743532, 0xFF0000),
    BLACK(15, "black", 0x1D1D21, MaterialColor.BLACK, 0x1E1B1B, 0);

    private static final DyeColor[] VALUES;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    private final int id;
    private final String name;
    private final MaterialColor materialColor;
    private final int color;
    private final int colorSwapped;
    private final float[] colorComponents;
    private final int fireworkColor;
    private final int signColor;

    private DyeColor(int woolId, String name, int color, MaterialColor materialColor, int j, int k) {
        this.id = woolId;
        this.name = name;
        this.color = color;
        this.materialColor = materialColor;
        this.signColor = k;
        int l = (color & 0xFF0000) >> 16;
        int m = (color & 0xFF00) >> 8;
        int n = (color & 0xFF) >> 0;
        this.colorSwapped = n << 16 | m << 8 | l << 0;
        this.colorComponents = new float[]{(float)l / 255.0f, (float)m / 255.0f, (float)n / 255.0f};
        this.fireworkColor = j;
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

    public MaterialColor getMaterialColor() {
        return this.materialColor;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSignColor() {
        return this.signColor;
    }

    public static DyeColor byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            id = 0;
        }
        return VALUES[id];
    }

    public static DyeColor byName(String string, DyeColor dyeColor) {
        for (DyeColor dyeColor2 : DyeColor.values()) {
            if (!dyeColor2.name.equals(string)) continue;
            return dyeColor2;
        }
        return dyeColor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor byFireworkColor(int i) {
        return (DyeColor)BY_FIREWORK_COLOR.get(i);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        VALUES = (DyeColor[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(dyeColor -> dyeColor.fireworkColor, dyeColor -> dyeColor)));
    }
}

