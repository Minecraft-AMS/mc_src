/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    protected boolean success = true;

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().playLevelEvent(this.success ? 1000 : 1001, pointer.getBlockPos(), 0);
    }
}

