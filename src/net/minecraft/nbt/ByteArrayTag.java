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
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag
extends AbstractListTag<ByteTag> {
    public static final TagReader<ByteArrayTag> READER = new TagReader<ByteArrayTag>(){

        @Override
        public ByteArrayTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(192L);
            int j = dataInput.readInt();
            positionTracker.add(8L * (long)j);
            byte[] bs = new byte[j];
            dataInput.readFully(bs);
            return new ByteArrayTag(bs);
        }

        @Override
        public String getCrashReportName() {
            return "BYTE[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Byte_Array";
        }

        @Override
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private byte[] value;

    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    public ByteArrayTag(List<Byte> value) {
        this(ByteArrayTag.toArray(value));
    }

    private static byte[] toArray(List<Byte> list) {
        byte[] bs = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Byte byte_ = list.get(i);
            bs[i] = byte_ == null ? (byte)0 : byte_;
        }
        return bs;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        output.write(this.value);
    }

    @Override
    public byte getType() {
        return 7;
    }

    public TagReader<ByteArrayTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[B;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]).append('B');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public Tag copy() {
        byte[] bs = new byte[this.value.length];
        System.arraycopy(this.value, 0, bs, 0, this.value.length);
        return new ByteArrayTag(bs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String indent, int depth) {
        Text text = new LiteralText("B").formatted(RED);
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

    public byte[] getByteArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public ByteTag get(int i) {
        return ByteTag.of(this.value[i]);
    }

    public ByteTag method_10606(int i, ByteTag byteTag) {
        byte b = this.value[i];
        this.value[i] = byteTag.getByte();
        return ByteTag.of(b);
    }

    @Override
    public void add(int i, ByteTag byteTag) {
        this.value = ArrayUtils.add((byte[])this.value, (int)i, (byte)byteTag.getByte());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getByte();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add((byte[])this.value, (int)index, (byte)((AbstractNumberTag)tag).getByte());
            return true;
        }
        return false;
    }

    @Override
    public ByteTag remove(int i) {
        byte b = this.value[i];
        this.value = ArrayUtils.remove((byte[])this.value, (int)i);
        return ByteTag.of(b);
    }

    @Override
    public void clear() {
        this.value = new byte[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag tag) {
        this.add(i, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag tag) {
        return this.method_10606(i, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int value, Object object) {
        this.add(value, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object set(int index, Object object) {
        return this.method_10606(index, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

