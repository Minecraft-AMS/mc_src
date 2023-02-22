/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public final class BlockRotation
extends Enum<BlockRotation> {
    public static final /* enum */ BlockRotation NONE = new BlockRotation(DirectionTransformation.IDENTITY);
    public static final /* enum */ BlockRotation CLOCKWISE_90 = new BlockRotation(DirectionTransformation.ROT_90_Y_NEG);
    public static final /* enum */ BlockRotation CLOCKWISE_180 = new BlockRotation(DirectionTransformation.ROT_180_FACE_XZ);
    public static final /* enum */ BlockRotation COUNTERCLOCKWISE_90 = new BlockRotation(DirectionTransformation.ROT_90_Y_POS);
    private final DirectionTransformation directionTransformation;
    private static final /* synthetic */ BlockRotation[] field_11466;

    public static BlockRotation[] values() {
        return (BlockRotation[])field_11466.clone();
    }

    public static BlockRotation valueOf(String string) {
        return Enum.valueOf(BlockRotation.class, string);
    }

    private BlockRotation(DirectionTransformation directionTransformation) {
        this.directionTransformation = directionTransformation;
    }

    public BlockRotation rotate(BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_90: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_180: {
                        return NONE;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_90;
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return NONE;
                    }
                    case CLOCKWISE_180: {
                        return CLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_180: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return NONE;
                    }
                }
            }
        }
        return this;
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }

    public Direction rotate(Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return direction.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return direction.rotateYCounterclockwise();
            }
            case CLOCKWISE_90: {
                return direction.rotateYClockwise();
            }
        }
        return direction;
    }

    public int rotate(int rotation, int fullTurn) {
        switch (this) {
            case CLOCKWISE_180: {
                return (rotation + fullTurn / 2) % fullTurn;
            }
            case COUNTERCLOCKWISE_90: {
                return (rotation + fullTurn * 3 / 4) % fullTurn;
            }
            case CLOCKWISE_90: {
                return (rotation + fullTurn / 4) % fullTurn;
            }
        }
        return rotation;
    }

    public static BlockRotation random(Random random) {
        return Util.getRandom(BlockRotation.values(), random);
    }

    public static List<BlockRotation> randomRotationOrder(Random random) {
        ArrayList list = Lists.newArrayList((Object[])BlockRotation.values());
        Collections.shuffle(list, random);
        return list;
    }

    private static /* synthetic */ BlockRotation[] method_36709() {
        return new BlockRotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
    }

    static {
        field_11466 = BlockRotation.method_36709();
    }
}

