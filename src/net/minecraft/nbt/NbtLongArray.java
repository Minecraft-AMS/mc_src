/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class NbtLongArray
extends AbstractNbtList<NbtLong> {
    public static final NbtType<NbtLongArray> TYPE = new NbtType<NbtLongArray>(){

        @Override
        public NbtLongArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(192L);
            int j = dataInput.readInt();
            nbtTagSizeTracker.add(64L * (long)j);
            long[] ls = new long[j];
            for (int k = 0; k < j; ++k) {
                ls[k] = dataInput.readLong();
            }
            return new NbtLongArray(ls);
        }

        @Override
        public String getCrashReportName() {
            return "LONG[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Long_Array";
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private long[] value;

    public NbtLongArray(long[] value) {
        this.value = value;
    }

    public NbtLongArray(LongSet value) {
        this.value = value.toLongArray();
    }

    public NbtLongArray(List<Long> value) {
        this(NbtLongArray.toArray(value));
    }

    private static long[] toArray(List<Long> list) {
        long[] ls = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Long long_ = list.get(i);
            ls[i] = long_ == null ? 0L : long_;
        }
        return ls;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for (long l : this.value) {
            output.writeLong(l);
        }
    }

    @Override
    public byte getType() {
        return 12;
    }

    public NbtType<NbtLongArray> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[L;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]).append('L');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public NbtLongArray copy() {
        long[] ls = new long[this.value.length];
        System.arraycopy(this.value, 0, ls, 0, this.value.length);
        return new NbtLongArray(ls);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtLongArray && Arrays.equals(this.value, ((NbtLongArray)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("L").formatted(RED);
        MutableText mutableText = new LiteralText("[").append(text).append(";");
        for (int i = 0; i < this.value.length; ++i) {
            MutableText mutableText2 = new LiteralText(String.valueOf(this.value[i])).formatted(GOLD);
            mutableText.append(" ").append(mutableText2).append(text);
            if (i == this.value.length - 1) continue;
            mutableText.append(",");
        }
        mutableText.append("]");
        return mutableText;
    }

    public long[] getLongArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public NbtLong get(int i) {
        return NbtLong.of(this.value[i]);
    }

    public NbtLong method_10606(int i, NbtLong nbtLong) {
        long l = this.value[i];
        this.value[i] = nbtLong.longValue();
        return NbtLong.of(l);
    }

    @Override
    public void add(int i, NbtLong nbtLong) {
        this.value = ArrayUtils.add((long[])this.value, (int)i, (long)nbtLong.longValue());
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.value[index] = ((AbstractNbtNumber)element).longValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.value = ArrayUtils.add((long[])this.value, (int)index, (long)((AbstractNbtNumber)element).longValue());
            return true;
        }
        return false;
    }

    @Override
    public NbtLong remove(int i) {
        long l = this.value[i];
        this.value = ArrayUtils.remove((long[])this.value, (int)i);
        return NbtLong.of(l);
    }

    @Override
    public byte getHeldType() {
        return 4;
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public /* synthetic */ NbtElement remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, NbtElement nbtElement) {
        this.add(i, (NbtLong)nbtElement);
    }

    @Override
    public /* synthetic */ NbtElement set(int i, NbtElement nbtElement) {
        return this.method_10606(i, (NbtLong)nbtElement);
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.add(i, (NbtLong)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.method_10606(i, (NbtLong)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

