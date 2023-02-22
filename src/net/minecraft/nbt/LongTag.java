/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class LongTag
extends AbstractNumberTag {
    private long value;

    LongTag() {
    }

    public LongTag(long l) {
        this.value = l;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(128L);
        this.value = input.readLong();
    }

    @Override
    public byte getType() {
        return 4;
    }

    @Override
    public String toString() {
        return this.value + "L";
    }

    @Override
    public LongTag copy() {
        return new LongTag(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof LongTag && this.value == ((LongTag)o).value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public Text toText(String indent, int depth) {
        Text text = new LiteralText("L").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return (int)(this.value & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFFL);
    }

    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFFL);
    }

    @Override
    public double getDouble() {
        return this.value;
    }

    @Override
    public float getFloat() {
        return this.value;
    }

    @Override
    public Number getNumber() {
        return this.value;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

