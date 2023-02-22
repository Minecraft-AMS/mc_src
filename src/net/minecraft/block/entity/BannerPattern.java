/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class BannerPattern
extends Enum<BannerPattern> {
    public static final /* enum */ BannerPattern BASE = new BannerPattern("base", "b", false);
    public static final /* enum */ BannerPattern SQUARE_BOTTOM_LEFT = new BannerPattern("square_bottom_left", "bl");
    public static final /* enum */ BannerPattern SQUARE_BOTTOM_RIGHT = new BannerPattern("square_bottom_right", "br");
    public static final /* enum */ BannerPattern SQUARE_TOP_LEFT = new BannerPattern("square_top_left", "tl");
    public static final /* enum */ BannerPattern SQUARE_TOP_RIGHT = new BannerPattern("square_top_right", "tr");
    public static final /* enum */ BannerPattern STRIPE_BOTTOM = new BannerPattern("stripe_bottom", "bs");
    public static final /* enum */ BannerPattern STRIPE_TOP = new BannerPattern("stripe_top", "ts");
    public static final /* enum */ BannerPattern STRIPE_LEFT = new BannerPattern("stripe_left", "ls");
    public static final /* enum */ BannerPattern STRIPE_RIGHT = new BannerPattern("stripe_right", "rs");
    public static final /* enum */ BannerPattern STRIPE_CENTER = new BannerPattern("stripe_center", "cs");
    public static final /* enum */ BannerPattern STRIPE_MIDDLE = new BannerPattern("stripe_middle", "ms");
    public static final /* enum */ BannerPattern STRIPE_DOWNRIGHT = new BannerPattern("stripe_downright", "drs");
    public static final /* enum */ BannerPattern STRIPE_DOWNLEFT = new BannerPattern("stripe_downleft", "dls");
    public static final /* enum */ BannerPattern STRIPE_SMALL = new BannerPattern("small_stripes", "ss");
    public static final /* enum */ BannerPattern CROSS = new BannerPattern("cross", "cr");
    public static final /* enum */ BannerPattern STRAIGHT_CROSS = new BannerPattern("straight_cross", "sc");
    public static final /* enum */ BannerPattern TRIANGLE_BOTTOM = new BannerPattern("triangle_bottom", "bt");
    public static final /* enum */ BannerPattern TRIANGLE_TOP = new BannerPattern("triangle_top", "tt");
    public static final /* enum */ BannerPattern TRIANGLES_BOTTOM = new BannerPattern("triangles_bottom", "bts");
    public static final /* enum */ BannerPattern TRIANGLES_TOP = new BannerPattern("triangles_top", "tts");
    public static final /* enum */ BannerPattern DIAGONAL_LEFT = new BannerPattern("diagonal_left", "ld");
    public static final /* enum */ BannerPattern DIAGONAL_RIGHT = new BannerPattern("diagonal_up_right", "rd");
    public static final /* enum */ BannerPattern DIAGONAL_LEFT_MIRROR = new BannerPattern("diagonal_up_left", "lud");
    public static final /* enum */ BannerPattern DIAGONAL_RIGHT_MIRROR = new BannerPattern("diagonal_right", "rud");
    public static final /* enum */ BannerPattern CIRCLE_MIDDLE = new BannerPattern("circle", "mc");
    public static final /* enum */ BannerPattern RHOMBUS_MIDDLE = new BannerPattern("rhombus", "mr");
    public static final /* enum */ BannerPattern HALF_VERTICAL = new BannerPattern("half_vertical", "vh");
    public static final /* enum */ BannerPattern HALF_HORIZONTAL = new BannerPattern("half_horizontal", "hh");
    public static final /* enum */ BannerPattern HALF_VERTICAL_MIRROR = new BannerPattern("half_vertical_right", "vhr");
    public static final /* enum */ BannerPattern HALF_HORIZONTAL_MIRROR = new BannerPattern("half_horizontal_bottom", "hhb");
    public static final /* enum */ BannerPattern BORDER = new BannerPattern("border", "bo");
    public static final /* enum */ BannerPattern CURLY_BORDER = new BannerPattern("curly_border", "cbo");
    public static final /* enum */ BannerPattern GRADIENT = new BannerPattern("gradient", "gra");
    public static final /* enum */ BannerPattern GRADIENT_UP = new BannerPattern("gradient_up", "gru");
    public static final /* enum */ BannerPattern BRICKS = new BannerPattern("bricks", "bri");
    public static final /* enum */ BannerPattern GLOBE = new BannerPattern("globe", "glb", true);
    public static final /* enum */ BannerPattern CREEPER = new BannerPattern("creeper", "cre", true);
    public static final /* enum */ BannerPattern SKULL = new BannerPattern("skull", "sku", true);
    public static final /* enum */ BannerPattern FLOWER = new BannerPattern("flower", "flo", true);
    public static final /* enum */ BannerPattern MOJANG = new BannerPattern("mojang", "moj", true);
    public static final /* enum */ BannerPattern PIGLIN = new BannerPattern("piglin", "pig", true);
    private static final BannerPattern[] VALUES;
    public static final int COUNT;
    public static final int HAS_PATTERN_ITEM_COUNT;
    public static final int LOOM_APPLICABLE_COUNT;
    private final boolean hasPatternItem;
    private final String name;
    final String id;
    private static final /* synthetic */ BannerPattern[] field_11833;

    public static BannerPattern[] values() {
        return (BannerPattern[])field_11833.clone();
    }

    public static BannerPattern valueOf(String string) {
        return Enum.valueOf(BannerPattern.class, string);
    }

    private BannerPattern(String name, String id) {
        this(name, id, false);
    }

    private BannerPattern(String name, String id, boolean hasPatternItem) {
        this.name = name;
        this.id = id;
        this.hasPatternItem = hasPatternItem;
    }

    public Identifier getSpriteId(boolean banner) {
        String string = banner ? "banner" : "shield";
        return new Identifier("entity/" + string + "/" + this.getName());
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    @Nullable
    public static BannerPattern byId(String id) {
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            if (!bannerPattern.id.equals(id)) continue;
            return bannerPattern;
        }
        return null;
    }

    @Nullable
    public static BannerPattern byName(String name) {
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            if (!bannerPattern.name.equals(name)) continue;
            return bannerPattern;
        }
        return null;
    }

    private static /* synthetic */ BannerPattern[] method_36713() {
        return new BannerPattern[]{BASE, SQUARE_BOTTOM_LEFT, SQUARE_BOTTOM_RIGHT, SQUARE_TOP_LEFT, SQUARE_TOP_RIGHT, STRIPE_BOTTOM, STRIPE_TOP, STRIPE_LEFT, STRIPE_RIGHT, STRIPE_CENTER, STRIPE_MIDDLE, STRIPE_DOWNRIGHT, STRIPE_DOWNLEFT, STRIPE_SMALL, CROSS, STRAIGHT_CROSS, TRIANGLE_BOTTOM, TRIANGLE_TOP, TRIANGLES_BOTTOM, TRIANGLES_TOP, DIAGONAL_LEFT, DIAGONAL_RIGHT, DIAGONAL_LEFT_MIRROR, DIAGONAL_RIGHT_MIRROR, CIRCLE_MIDDLE, RHOMBUS_MIDDLE, HALF_VERTICAL, HALF_HORIZONTAL, HALF_VERTICAL_MIRROR, HALF_HORIZONTAL_MIRROR, BORDER, CURLY_BORDER, GRADIENT, GRADIENT_UP, BRICKS, GLOBE, CREEPER, SKULL, FLOWER, MOJANG, PIGLIN};
    }

    static {
        field_11833 = BannerPattern.method_36713();
        VALUES = BannerPattern.values();
        COUNT = VALUES.length;
        HAS_PATTERN_ITEM_COUNT = (int)Arrays.stream(VALUES).filter(bannerPattern -> bannerPattern.hasPatternItem).count();
        LOOM_APPLICABLE_COUNT = COUNT - HAS_PATTERN_ITEM_COUNT - 1;
    }

    public static class Patterns {
        private final List<Pair<BannerPattern, DyeColor>> entries = Lists.newArrayList();

        public Patterns add(BannerPattern pattern, DyeColor color) {
            return this.add((Pair<BannerPattern, DyeColor>)Pair.of((Object)((Object)pattern), (Object)color));
        }

        public Patterns add(Pair<BannerPattern, DyeColor> pattern) {
            this.entries.add(pattern);
            return this;
        }

        public NbtList toNbt() {
            NbtList nbtList = new NbtList();
            for (Pair<BannerPattern, DyeColor> pair : this.entries) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("Pattern", ((BannerPattern)((Object)pair.getFirst())).id);
                nbtCompound.putInt("Color", ((DyeColor)pair.getSecond()).getId());
                nbtList.add(nbtCompound);
            }
            return nbtList;
        }
    }
}

