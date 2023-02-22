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

public class ByteTag
extends AbstractNumberTag {
    private byte value;

    ByteTag() {
    }

    public ByteTag(byte b) {
        this.value = b;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(72L);
        this.value = input.readByte();
    }

    @Override
    public byte getType() {
        return 1;
    }

    @Override
    public String toString() {
        return this.value + "b";
    }

    @Override
    public ByteTag copy() {
        return new ByteTag(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ByteTag && this.value == ((ByteTag)o).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public Text toText(String indent, int depth) {
        Text text = new LiteralText("b").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return this.value;
    }

    @Override
    public short getShort() {
        return this.value;
    }

    @Override
    public byte getByte() {
        return this.value;
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

