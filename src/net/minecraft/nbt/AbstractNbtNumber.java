/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.NbtElement;

public abstract class AbstractNbtNumber
implements NbtElement {
    protected AbstractNbtNumber() {
    }

    public abstract long longValue();

    public abstract int intValue();

    public abstract short shortValue();

    public abstract byte byteValue();

    public abstract double doubleValue();

    public abstract float floatValue();

    public abstract Number numberValue();
}

