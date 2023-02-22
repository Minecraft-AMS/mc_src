/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag
extends AbstractListTag<IntTag> {
    private int[] value;

    IntArrayTag() {
    }

    public IntArrayTag(int[] is) {
        this.value = is;
    }

    public IntArrayTag(List<Integer> list) {
        this(IntArrayTag.toArray(list));
    }

    private static int[] toArray(List<Integer> list) {
        int[] is = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Integer integer = list.get(i);
            is[i] = integer == null ? 0 : integer;
        }
        return is;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for (int i : this.value) {
            output.writeInt(i);
        }
    }

    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        int i = input.readInt();
        positionTracker.add(32 * i);
        this.value = new int[i];
        for (int j = 0; j < i; ++j) {
            this.value[j] = input.readInt();
        }
    }

    @Override
    public byte getType() {
        return 11;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[I;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]);
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public IntArrayTag copy() {
        int[] is = new int[this.value.length];
        System.arraycopy(this.value, 0, is, 0, this.value.length);
        return new IntArrayTag(is);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof IntArrayTag && Arrays.equals(this.value, ((IntArrayTag)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    public int[] getIntArray() {
        return this.value;
    }

    @Override
    public Text toText(String indent, int depth) {
        Text text = new LiteralText("I").formatted(RED);
        Text text2 = new LiteralText("[").append(text).append(";");
        for (int i = 0; i < this.value.length; ++i) {
            text2.append(" ").append(new LiteralText(String.valueOf(this.value[i])).formatted(GOLD));
            if (i == this.value.length - 1) continue;
            text2.append(",");
        }
        text2.append("]");
        return text2;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public IntTag get(int i) {
        return new IntTag(this.value[i]);
    }

    @Override
    public IntTag set(int i, IntTag intTag) {
        int j = this.value[i];
        this.value[i] = intTag.getInt();
        return new IntTag(j);
    }

    public void method_10531(int i, IntTag intTag) {
        this.value = ArrayUtils.add((int[])this.value, (int)i, (int)intTag.getInt());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getInt();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add((int[])this.value, (int)index, (int)((AbstractNumberTag)tag).getInt());
            return true;
        }
        return false;
    }

    public IntTag method_10536(int i) {
        int j = this.value[i];
        this.value = ArrayUtils.remove((int[])this.value, (int)i);
        return new IntTag(j);
    }

    @Override
    public void clear() {
        this.value = new int[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag tag) {
        this.method_10531(i, (IntTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag tag) {
        return this.set(i, (IntTag)tag);
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
        this.method_10531(value, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object set(int index, Object object) {
        return this.set(index, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}
