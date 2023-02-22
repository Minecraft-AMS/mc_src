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
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.apache.commons.lang3.ArrayUtils;

public class NbtIntArray
extends AbstractNbtList<NbtInt> {
    private static final int SIZE = 24;
    public static final NbtType<NbtIntArray> TYPE = new NbtType.OfVariableSize<NbtIntArray>(){

        @Override
        public NbtIntArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(24L);
            int j = dataInput.readInt();
            nbtTagSizeTracker.add(4L * (long)j);
            int[] is = new int[j];
            for (int k = 0; k < j; ++k) {
                is[k] = dataInput.readInt();
            }
            return new NbtIntArray(is);
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
            int i = input.readInt();
            int[] is = new int[i];
            for (int j = 0; j < i; ++j) {
                is[j] = input.readInt();
            }
            return visitor.visitIntArray(is);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            input.skipBytes(input.readInt() * 4);
        }

        @Override
        public String getCrashReportName() {
            return "INT[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Int_Array";
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private int[] value;

    public NbtIntArray(int[] value) {
        this.value = value;
    }

    public NbtIntArray(List<Integer> value) {
        this(NbtIntArray.toArray(value));
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
    public int getSizeInBytes() {
        return 24 + 4 * this.value.length;
    }

    @Override
    public byte getType() {
        return 11;
    }

    public NbtType<NbtIntArray> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public NbtIntArray copy() {
        int[] is = new int[this.value.length];
        System.arraycopy(this.value, 0, is, 0, this.value.length);
        return new NbtIntArray(is);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtIntArray && Arrays.equals(this.value, ((NbtIntArray)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    public int[] getIntArray() {
        return this.value;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitIntArray(this);
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public NbtInt get(int i) {
        return NbtInt.of(this.value[i]);
    }

    @Override
    public NbtInt set(int i, NbtInt nbtInt) {
        int j = this.value[i];
        this.value[i] = nbtInt.intValue();
        return NbtInt.of(j);
    }

    @Override
    public void add(int i, NbtInt nbtInt) {
        this.value = ArrayUtils.add((int[])this.value, (int)i, (int)nbtInt.intValue());
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.value[index] = ((AbstractNbtNumber)element).intValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.value = ArrayUtils.add((int[])this.value, (int)index, (int)((AbstractNbtNumber)element).intValue());
            return true;
        }
        return false;
    }

    @Override
    public NbtInt remove(int i) {
        int j = this.value[i];
        this.value = ArrayUtils.remove((int[])this.value, (int)i);
        return NbtInt.of(j);
    }

    @Override
    public byte getHeldType() {
        return 3;
    }

    @Override
    public void clear() {
        this.value = new int[0];
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitIntArray(this.value);
    }

    @Override
    public /* synthetic */ NbtElement remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, NbtElement nbtElement) {
        this.add(i, (NbtInt)nbtElement);
    }

    @Override
    public /* synthetic */ NbtElement set(int i, NbtElement nbtElement) {
        return this.set(i, (NbtInt)nbtElement);
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
        this.add(i, (NbtInt)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (NbtInt)object);
    }

    @Override
    public /* synthetic */ Object get(int index) {
        return this.get(index);
    }
}

