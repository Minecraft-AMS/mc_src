/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.util.math;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.math.Direction;

public final class EightWayDirection
extends Enum<EightWayDirection> {
    public static final /* enum */ EightWayDirection NORTH = new EightWayDirection(Direction.NORTH);
    public static final /* enum */ EightWayDirection NORTH_EAST = new EightWayDirection(Direction.NORTH, Direction.EAST);
    public static final /* enum */ EightWayDirection EAST = new EightWayDirection(Direction.EAST);
    public static final /* enum */ EightWayDirection SOUTH_EAST = new EightWayDirection(Direction.SOUTH, Direction.EAST);
    public static final /* enum */ EightWayDirection SOUTH = new EightWayDirection(Direction.SOUTH);
    public static final /* enum */ EightWayDirection SOUTH_WEST = new EightWayDirection(Direction.SOUTH, Direction.WEST);
    public static final /* enum */ EightWayDirection WEST = new EightWayDirection(Direction.WEST);
    public static final /* enum */ EightWayDirection NORTH_WEST = new EightWayDirection(Direction.NORTH, Direction.WEST);
    private final Set<Direction> directions;
    private static final /* synthetic */ EightWayDirection[] field_11071;

    public static EightWayDirection[] values() {
        return (EightWayDirection[])field_11071.clone();
    }

    public static EightWayDirection valueOf(String string) {
        return Enum.valueOf(EightWayDirection.class, string);
    }

    private EightWayDirection(Direction ... directions) {
        this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
    }

    public Set<Direction> getDirections() {
        return this.directions;
    }

    private static /* synthetic */ EightWayDirection[] method_36935() {
        return new EightWayDirection[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
    }

    static {
        field_11071 = EightWayDirection.method_36935();
    }
}

