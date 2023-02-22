/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.block.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public final class JigsawOrientation
extends Enum<JigsawOrientation>
implements StringIdentifiable {
    public static final /* enum */ JigsawOrientation DOWN_EAST = new JigsawOrientation("down_east", Direction.DOWN, Direction.EAST);
    public static final /* enum */ JigsawOrientation DOWN_NORTH = new JigsawOrientation("down_north", Direction.DOWN, Direction.NORTH);
    public static final /* enum */ JigsawOrientation DOWN_SOUTH = new JigsawOrientation("down_south", Direction.DOWN, Direction.SOUTH);
    public static final /* enum */ JigsawOrientation DOWN_WEST = new JigsawOrientation("down_west", Direction.DOWN, Direction.WEST);
    public static final /* enum */ JigsawOrientation UP_EAST = new JigsawOrientation("up_east", Direction.UP, Direction.EAST);
    public static final /* enum */ JigsawOrientation UP_NORTH = new JigsawOrientation("up_north", Direction.UP, Direction.NORTH);
    public static final /* enum */ JigsawOrientation UP_SOUTH = new JigsawOrientation("up_south", Direction.UP, Direction.SOUTH);
    public static final /* enum */ JigsawOrientation UP_WEST = new JigsawOrientation("up_west", Direction.UP, Direction.WEST);
    public static final /* enum */ JigsawOrientation WEST_UP = new JigsawOrientation("west_up", Direction.WEST, Direction.UP);
    public static final /* enum */ JigsawOrientation EAST_UP = new JigsawOrientation("east_up", Direction.EAST, Direction.UP);
    public static final /* enum */ JigsawOrientation NORTH_UP = new JigsawOrientation("north_up", Direction.NORTH, Direction.UP);
    public static final /* enum */ JigsawOrientation SOUTH_UP = new JigsawOrientation("south_up", Direction.SOUTH, Direction.UP);
    private static final Int2ObjectMap<JigsawOrientation> BY_INDEX;
    private final String name;
    private final Direction rotation;
    private final Direction facing;
    private static final /* synthetic */ JigsawOrientation[] field_23397;

    public static JigsawOrientation[] values() {
        return (JigsawOrientation[])field_23397.clone();
    }

    public static JigsawOrientation valueOf(String string) {
        return Enum.valueOf(JigsawOrientation.class, string);
    }

    private static int getIndex(Direction facing, Direction rotation) {
        return rotation.ordinal() << 3 | facing.ordinal();
    }

    private JigsawOrientation(String name, Direction facing, Direction rotation) {
        this.name = name;
        this.facing = facing;
        this.rotation = rotation;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static JigsawOrientation byDirections(Direction facing, Direction rotation) {
        int i = JigsawOrientation.getIndex(facing, rotation);
        return (JigsawOrientation)BY_INDEX.get(i);
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Direction getRotation() {
        return this.rotation;
    }

    private static /* synthetic */ JigsawOrientation[] method_36936() {
        return new JigsawOrientation[]{DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST, WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP};
    }

    static {
        field_23397 = JigsawOrientation.method_36936();
        BY_INDEX = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(JigsawOrientation.values().length), int2ObjectOpenHashMap -> {
            for (JigsawOrientation jigsawOrientation : JigsawOrientation.values()) {
                int2ObjectOpenHashMap.put(JigsawOrientation.getIndex(jigsawOrientation.facing, jigsawOrientation.rotation), (Object)jigsawOrientation);
            }
        });
    }
}

