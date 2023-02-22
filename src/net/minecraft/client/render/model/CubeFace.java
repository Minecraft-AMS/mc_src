/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public final class CubeFace
extends Enum<CubeFace> {
    public static final /* enum */ CubeFace DOWN = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH));
    public static final /* enum */ CubeFace UP = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH));
    public static final /* enum */ CubeFace NORTH = new CubeFace(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH));
    public static final /* enum */ CubeFace SOUTH = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH));
    public static final /* enum */ CubeFace WEST = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH));
    public static final /* enum */ CubeFace EAST = new CubeFace(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH));
    private static final CubeFace[] DIRECTION_LOOKUP;
    private final Corner[] corners;
    private static final /* synthetic */ CubeFace[] field_3964;

    public static CubeFace[] values() {
        return (CubeFace[])field_3964.clone();
    }

    public static CubeFace valueOf(String string) {
        return Enum.valueOf(CubeFace.class, string);
    }

    public static CubeFace getFace(Direction direction) {
        return DIRECTION_LOOKUP[direction.getId()];
    }

    private CubeFace(Corner ... corners) {
        this.corners = corners;
    }

    public Corner getCorner(int corner) {
        return this.corners[corner];
    }

    private static /* synthetic */ CubeFace[] method_36913() {
        return new CubeFace[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        field_3964 = CubeFace.method_36913();
        DIRECTION_LOOKUP = Util.make(new CubeFace[6], cubeFaces -> {
            cubeFaces[DirectionIds.DOWN] = DOWN;
            cubeFaces[DirectionIds.UP] = UP;
            cubeFaces[DirectionIds.NORTH] = NORTH;
            cubeFaces[DirectionIds.SOUTH] = SOUTH;
            cubeFaces[DirectionIds.WEST] = WEST;
            cubeFaces[DirectionIds.EAST] = EAST;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class Corner {
        public final int xSide;
        public final int ySide;
        public final int zSide;

        Corner(int xSide, int ySide, int zSide) {
            this.xSide = xSide;
            this.ySide = ySide;
            this.zSide = zSide;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DirectionIds {
        public static final int SOUTH = Direction.SOUTH.getId();
        public static final int UP = Direction.UP.getId();
        public static final int EAST = Direction.EAST.getId();
        public static final int NORTH = Direction.NORTH.getId();
        public static final int DOWN = Direction.DOWN.getId();
        public static final int WEST = Direction.WEST.getId();
    }
}

