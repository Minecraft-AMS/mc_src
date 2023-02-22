/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item.map;

import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class MapIcon {
    private final Type type;
    private final byte x;
    private final byte z;
    private final byte rotation;
    @Nullable
    private final Text text;

    public MapIcon(Type type, byte x, byte z, byte rotation, @Nullable Text text) {
        this.type = type;
        this.x = x;
        this.z = z;
        this.rotation = rotation;
        this.text = text;
    }

    public byte getTypeId() {
        return this.type.getId();
    }

    public Type getType() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getZ() {
        return this.z;
    }

    public byte getRotation() {
        return this.rotation;
    }

    public boolean isAlwaysRendered() {
        return this.type.isAlwaysRendered();
    }

    @Nullable
    public Text getText() {
        return this.text;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapIcon)) {
            return false;
        }
        MapIcon mapIcon = (MapIcon)o;
        return this.type == mapIcon.type && this.rotation == mapIcon.rotation && this.x == mapIcon.x && this.z == mapIcon.z && Objects.equals(this.text, mapIcon.text);
    }

    public int hashCode() {
        int i = this.type.getId();
        i = 31 * i + this.x;
        i = 31 * i + this.z;
        i = 31 * i + this.rotation;
        i = 31 * i + Objects.hashCode(this.text);
        return i;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type PLAYER = new Type(false, true);
        public static final /* enum */ Type FRAME = new Type(true, true);
        public static final /* enum */ Type RED_MARKER = new Type(false, true);
        public static final /* enum */ Type BLUE_MARKER = new Type(false, true);
        public static final /* enum */ Type TARGET_X = new Type(true, false);
        public static final /* enum */ Type TARGET_POINT = new Type(true, false);
        public static final /* enum */ Type PLAYER_OFF_MAP = new Type(false, true);
        public static final /* enum */ Type PLAYER_OFF_LIMITS = new Type(false, true);
        public static final /* enum */ Type MANSION = new Type(true, 5393476, false);
        public static final /* enum */ Type MONUMENT = new Type(true, 3830373, false);
        public static final /* enum */ Type BANNER_WHITE = new Type(true, true);
        public static final /* enum */ Type BANNER_ORANGE = new Type(true, true);
        public static final /* enum */ Type BANNER_MAGENTA = new Type(true, true);
        public static final /* enum */ Type BANNER_LIGHT_BLUE = new Type(true, true);
        public static final /* enum */ Type BANNER_YELLOW = new Type(true, true);
        public static final /* enum */ Type BANNER_LIME = new Type(true, true);
        public static final /* enum */ Type BANNER_PINK = new Type(true, true);
        public static final /* enum */ Type BANNER_GRAY = new Type(true, true);
        public static final /* enum */ Type BANNER_LIGHT_GRAY = new Type(true, true);
        public static final /* enum */ Type BANNER_CYAN = new Type(true, true);
        public static final /* enum */ Type BANNER_PURPLE = new Type(true, true);
        public static final /* enum */ Type BANNER_BLUE = new Type(true, true);
        public static final /* enum */ Type BANNER_BROWN = new Type(true, true);
        public static final /* enum */ Type BANNER_GREEN = new Type(true, true);
        public static final /* enum */ Type BANNER_RED = new Type(true, true);
        public static final /* enum */ Type BANNER_BLACK = new Type(true, true);
        public static final /* enum */ Type RED_X = new Type(true, false);
        private final byte id;
        private final boolean alwaysRender;
        private final int tintColor;
        private final boolean useIconCountLimit;
        private static final /* synthetic */ Type[] field_109;

        public static Type[] values() {
            return (Type[])field_109.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(boolean alwaysRender, boolean useIconCountLimit) {
            this(alwaysRender, -1, useIconCountLimit);
        }

        private Type(boolean alwaysRender, int tintColor, boolean useIconCountLimit) {
            this.useIconCountLimit = useIconCountLimit;
            this.id = (byte)this.ordinal();
            this.alwaysRender = alwaysRender;
            this.tintColor = tintColor;
        }

        public byte getId() {
            return this.id;
        }

        public boolean isAlwaysRendered() {
            return this.alwaysRender;
        }

        public boolean hasTintColor() {
            return this.tintColor >= 0;
        }

        public int getTintColor() {
            return this.tintColor;
        }

        public static Type byId(byte id) {
            return Type.values()[MathHelper.clamp((int)id, 0, Type.values().length - 1)];
        }

        public boolean shouldUseIconCountLimit() {
            return this.useIconCountLimit;
        }

        private static /* synthetic */ Type[] method_36790() {
            return new Type[]{PLAYER, FRAME, RED_MARKER, BLUE_MARKER, TARGET_X, TARGET_POINT, PLAYER_OFF_MAP, PLAYER_OFF_LIMITS, MANSION, MONUMENT, BANNER_WHITE, BANNER_ORANGE, BANNER_MAGENTA, BANNER_LIGHT_BLUE, BANNER_YELLOW, BANNER_LIME, BANNER_PINK, BANNER_GRAY, BANNER_LIGHT_GRAY, BANNER_CYAN, BANNER_PURPLE, BANNER_BLUE, BANNER_BROWN, BANNER_GREEN, BANNER_RED, BANNER_BLACK, RED_X};
        }

        static {
            field_109 = Type.method_36790();
        }
    }
}

