/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NbtString
implements NbtElement {
    public static final NbtType<NbtString> TYPE = new NbtType<NbtString>(){

        @Override
        public NbtString read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(288L);
            String string = dataInput.readUTF();
            nbtTagSizeTracker.add(16 * string.length());
            return NbtString.of(string);
        }

        @Override
        public String getCrashReportName() {
            return "STRING";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_String";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private static final NbtString EMPTY = new NbtString("");
    private final String value;

    private NbtString(String value) {
        Objects.requireNonNull(value, "Null string not allowed");
        this.value = value;
    }

    public static NbtString of(String value) {
        if (value.isEmpty()) {
            return EMPTY;
        }
        return new NbtString(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public byte getType() {
        return 8;
    }

    public NbtType<NbtString> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return NbtString.escape(this.value);
    }

    @Override
    public NbtString copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtString && Objects.equals(this.value, ((NbtString)o).value);
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
        String string = NbtString.escape(this.value);
        String string2 = string.substring(0, 1);
        MutableText text = new LiteralText(string.substring(1, string.length() - 1)).formatted(GREEN);
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
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}

