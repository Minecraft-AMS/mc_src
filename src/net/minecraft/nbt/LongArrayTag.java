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
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag
extends AbstractListTag<LongTag> {
    private long[] value;

    LongArrayTag() {
    }

    public LongArrayTag(long[] ls) {
        this.value = ls;
    }

    public LongArrayTag(LongSet longSet) {
        this.value = longSet.toLongArray();
    }

    public LongArrayTag(List<Long> list) {
        this(LongArrayTag.toArray(list));
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
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        int i = input.readInt();
        positionTracker.add(64 * i);
        this.value = new long[i];
        for (int j = 0; j < i; ++j) {
            this.value[j] = input.readLong();
        }
    }

    @Override
    public byte getType() {
        return 12;
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
    public LongArrayTag copy() {
        long[] ls = new long[this.value.length];
        System.arraycopy(this.value, 0, ls, 0, this.value.length);
        return new LongArrayTag(ls);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String indent, int depth) {
        Text text = new LiteralText("L").formatted(RED);
        Text text2 = new LiteralText("[").append(text).append(";");
        for (int i = 0; i < this.value.length; ++i) {
            Text text3 = new LiteralText(String.valueOf(this.value[i])).formatted(GOLD);
            text2.append(" ").append(text3).append(text);
            if (i == this.value.length - 1) continue;
            text2.append(",");
        }
        text2.append("]");
        return text2;
    }

    public long[] getLongArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public LongTag get(int i) {
        return new LongTag(this.value[i]);
    }

    @Override
    public LongTag set(int i, LongTag longTag) {
        long l = this.value[i];
        this.value[i] = longTag.getLong();
        return new LongTag(l);
    }

    public void method_10531(int i, LongTag longTag) {
        this.value = ArrayUtils.add((long[])this.value, (int)i, (long)longTag.getLong());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getLong();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add((long[])this.value, (int)index, (long)((AbstractNumberTag)tag).getLong());
            return true;
        }
        return false;
    }

    public LongTag method_10536(int i) {
        long l = this.value[i];
        this.value = ArrayUtils.remove((long[])this.value, (int)i);
        return new LongTag(l);
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag tag) {
        this.method_10531(i, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag tag) {
        return this.set(i, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int value, Object object) {
        this.method_10531(value, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object set(int index, Object object) {
        return this.set(index, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}
