/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class NbtDouble
extends AbstractNbtNumber {
    public static final NbtDouble ZERO = new NbtDouble(0.0);
    public static final NbtType<NbtDouble> TYPE = new NbtType<NbtDouble>(){

        @Override
        public NbtDouble read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(128L);
            return NbtDouble.of(dataInput.readDouble());
        }

        @Override
        public String getCrashReportName() {
            return "DOUBLE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Double";
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
    private final double value;

    private NbtDouble(double value) {
        this.value = value;
    }

    public static NbtDouble of(double value) {
        if (value == 0.0) {
            return ZERO;
        }
        return new NbtDouble(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    @Override
    public byte getType() {
        return 6;
    }

    public NbtType<NbtDouble> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.value + "d";
    }

    @Override
    public NbtDouble copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtDouble && this.value == ((NbtDouble)o).value;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.value);
        return (int)(l ^ l >>> 32);
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("d").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long longValue() {
        return (long)Math.floor(this.value);
    }

    @Override
    public int intValue() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short shortValue() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return (float)this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}

