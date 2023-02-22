/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class StringTag
implements Tag {
    private String value;

    public StringTag() {
        this("");
    }

    public StringTag(String string) {
        Objects.requireNonNull(string, "Null string not allowed");
        this.value = string;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(288L);
        this.value = input.readUTF();
        positionTracker.add(16 * this.value.length());
    }

    @Override
    public byte getType() {
        return 8;
    }

    @Override
    public String toString() {
        return StringTag.escape(this.value);
    }

    @Override
    public StringTag copy() {
        return new StringTag(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof StringTag && Objects.equals(this.value, ((StringTag)o).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public Text toText(String indent, int depth) {
        String string = StringTag.escape(this.value);
        String string2 = string.substring(0, 1);
        Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(GREEN);
        return new LiteralText(string2).append(text).append(string2);
    }

    public static String escape(String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        int c = 0;
        for (int i = 0; i < value.length(); ++i) {
            int d = value.charAt(i);
            if (d == 92) {
                stringBuilder.append('\\');
            } else if (d == 34 || d == 39) {
                if (c == 0) {
                    int n = c = d == 34 ? 39 : 34;
                }
                if (c == d) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append((char)d);
        }
        if (c == 0) {
            c = 34;
        }
        stringBuilder.setCharAt(0, (char)c);
        stringBuilder.append((char)c);
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

