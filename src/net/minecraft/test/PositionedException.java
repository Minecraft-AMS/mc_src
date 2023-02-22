/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.test;

import net.minecraft.test.GameTestException;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PositionedException
extends GameTestException {
    private final BlockPos pos;
    private final BlockPos relativePos;
    private final long field_21449;

    @Override
    public String getMessage() {
        String string = "" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
        return super.getMessage() + " at " + string + " (t=" + this.field_21449 + ")";
    }

    @Nullable
    public String getDebugMessage() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }
}
