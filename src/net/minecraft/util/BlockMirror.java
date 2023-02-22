/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public final class BlockMirror
extends Enum<BlockMirror> {
    public static final /* enum */ BlockMirror NONE = new BlockMirror(new TranslatableText("mirror.none"), DirectionTransformation.IDENTITY);
    public static final /* enum */ BlockMirror LEFT_RIGHT = new BlockMirror(new TranslatableText("mirror.left_right"), DirectionTransformation.INVERT_Z);
    public static final /* enum */ BlockMirror FRONT_BACK = new BlockMirror(new TranslatableText("mirror.front_back"), DirectionTransformation.INVERT_X);
    private final Text name;
    private final DirectionTransformation directionTransformation;
    private static final /* synthetic */ BlockMirror[] field_11299;

    public static BlockMirror[] values() {
        return (BlockMirror[])field_11299.clone();
    }

    public static BlockMirror valueOf(String string) {
        return Enum.valueOf(BlockMirror.class, string);
    }

    private BlockMirror(Text name, DirectionTransformation directionTransformation) {
        this.name = name;
        this.directionTransformation = directionTransformation;
    }

    public int mirror(int rotation, int fullTurn) {
        int i = fullTurn / 2;
        int j = rotation > i ? rotation - fullTurn : rotation;
        switch (this) {
            case FRONT_BACK: {
                return (fullTurn - j) % fullTurn;
            }
            case LEFT_RIGHT: {
                return (i - j + fullTurn) % fullTurn;
            }
        }
        return rotation;
    }

    public BlockRotation getRotation(Direction direction) {
        Direction.Axis axis = direction.getAxis();
        return this == LEFT_RIGHT && axis == Direction.Axis.Z || this == FRONT_BACK && axis == Direction.Axis.X ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
    }

    public Direction apply(Direction direction) {
        if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
            return direction.getOpposite();
        }
        if (this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z) {
            return direction.getOpposite();
        }
        return direction;
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }

    public Text getName() {
        return this.name;
    }

    private static /* synthetic */ BlockMirror[] method_36706() {
        return new BlockMirror[]{NONE, LEFT_RIGHT, FRONT_BACK};
    }

    static {
        field_11299 = BlockMirror.method_36706();
    }
}

