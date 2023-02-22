/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class WallMountLocation
extends Enum<WallMountLocation>
implements StringIdentifiable {
    public static final /* enum */ WallMountLocation FLOOR = new WallMountLocation("floor");
    public static final /* enum */ WallMountLocation WALL = new WallMountLocation("wall");
    public static final /* enum */ WallMountLocation CEILING = new WallMountLocation("ceiling");
    private final String name;
    private static final /* synthetic */ WallMountLocation[] field_12474;

    public static WallMountLocation[] values() {
        return (WallMountLocation[])field_12474.clone();
    }

    public static WallMountLocation valueOf(String string) {
        return Enum.valueOf(WallMountLocation.class, string);
    }

    private WallMountLocation(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ WallMountLocation[] method_36720() {
        return new WallMountLocation[]{FLOOR, WALL, CEILING};
    }

    static {
        field_12474 = WallMountLocation.method_36720();
    }
}

