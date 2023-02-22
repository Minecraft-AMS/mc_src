/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.random.Random;

public final class BlockRotation
extends Enum<BlockRotation>
implements StringIdentifiable {
    public static final /* enum */ BlockRotation NONE = new BlockRotation("none", DirectionTransformation.IDENTITY);
    public static final /* enum */ BlockRotation CLOCKWISE_90 = new BlockRotation("clockwise_90", DirectionTransformation.ROT_90_Y_NEG);
    public static final /* enum */ BlockRotation CLOCKWISE_180 = new BlockRotation("180", DirectionTransformation.ROT_180_FACE_XZ);
    public static final /* enum */ BlockRotation COUNTERCLOCKWISE_90 = new BlockRotation("counterclockwise_90", DirectionTransformation.ROT_90_Y_POS);
    public static final Codec<BlockRotation> CODEC;
    private final String id;
    private final DirectionTransformation directionTransformation;
    private static final /* synthetic */ BlockRotation[] field_11466;

    public static BlockRotation[] values() {
        return (BlockRotation[])field_11466.clone();
    }

    public static BlockRotation valueOf(String string) {
        return Enum.valueOf(BlockRotation.class, string);
    }

    private BlockRotation(String id, DirectionTransformation directionTransformation) {
        this.id = id;
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
        return Util.copyShuffled(BlockRotation.values(), random);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ BlockRotation[] method_36709() {
        return new BlockRotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
    }

    static {
        field_11466 = BlockRotation.method_36709();
        CODEC = StringIdentifiable.createCodec(BlockRotation::values);
    }
}

