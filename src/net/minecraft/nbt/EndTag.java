/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EndTag
implements Tag {
    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(64L);
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public byte getType() {
        return 0;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public EndTag copy() {
        return new EndTag();
    }

    @Override
    public Text toText(String indent, int depth) {
        return new LiteralText("");
    }

    public boolean equals(Object o) {
        return o instanceof EndTag;
    }

    public int hashCode() {
        return this.getType();
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

