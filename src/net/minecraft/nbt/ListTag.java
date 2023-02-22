/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ListTag
extends AbstractListTag<Tag> {
    private List<Tag> value = Lists.newArrayList();
    private byte type = 0;

    @Override
    public void write(DataOutput output) throws IOException {
        this.type = this.value.isEmpty() ? (byte)0 : this.value.get(0).getType();
        output.writeByte(this.type);
        output.writeInt(this.value.size());
        for (Tag tag : this.value) {
            tag.write(output);
        }
    }

    @Override
    public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(296L);
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.type = input.readByte();
        int i = input.readInt();
        if (this.type == 0 && i > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        positionTracker.add(32L * (long)i);
        this.value = Lists.newArrayListWithCapacity((int)i);
        for (int j = 0; j < i; ++j) {
            Tag tag = Tag.createTag(this.type);
            tag.read(input, depth + 1, positionTracker);
            this.value.add(tag);
        }
    }

    @Override
    public byte getType() {
        return 9;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < this.value.size(); ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value.get(i));
        }
        return stringBuilder.append(']').toString();
    }

    private void forgetTypeIfEmpty() {
        if (this.value.isEmpty()) {
            this.type = 0;
        }
    }

    @Override
    public Tag remove(int i) {
        Tag tag = this.value.remove(i);
        this.forgetTypeIfEmpty();
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public CompoundTag getCompound(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 10) {
            return (CompoundTag)tag;
        }
        return new CompoundTag();
    }

    public ListTag getList(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 9) {
            return (ListTag)tag;
        }
        return new ListTag();
    }

    public short getShort(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 2) {
            return ((ShortTag)tag).getShort();
        }
        return 0;
    }

    public int getInt(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 3) {
            return ((IntTag)tag).getInt();
        }
        return 0;
    }

    public int[] getIntArray(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 11) {
            return ((IntArrayTag)tag).getIntArray();
        }
        return new int[0];
    }

    public double getDouble(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 6) {
            return ((DoubleTag)tag).getDouble();
        }
        return 0.0;
    }

    public float getFloat(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 5) {
            return ((FloatTag)tag).getFloat();
        }
        return 0.0f;
    }

    public String getString(int index) {
        if (index < 0 || index >= this.value.size()) {
            return "";
        }
        Tag tag = this.value.get(index);
        if (tag.getType() == 8) {
            return tag.asString();
        }
        return tag.toString();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Tag get(int i) {
        return this.value.get(i);
    }

    @Override
    public Tag set(int i, Tag tag) {
        Tag tag2 = this.get(i);
        if (!this.setTag(i, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
        return tag2;
    }

    @Override
    public void add(int i, Tag tag) {
        if (!this.addTag(i, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.set(index, tag);
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.add(index, tag);
            return true;
        }
        return false;
    }

    private boolean canAdd(Tag tag) {
        if (tag.getType() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = tag.getType();
            return true;
        }
        return this.type == tag.getType();
    }

    @Override
    public ListTag copy() {
        ListTag listTag = new ListTag();
        listTag.type = this.type;
        for (Tag tag : this.value) {
            Tag tag2 = tag.copy();
            listTag.value.add(tag2);
        }
        return listTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public Text toText(String indent, int depth) {
        if (this.isEmpty()) {
            return new LiteralText("[]");
        }
        LiteralText text = new LiteralText("[");
        if (!indent.isEmpty()) {
            text.append("\n");
        }
        for (int i = 0; i < this.value.size(); ++i) {
            LiteralText text2 = new LiteralText(Strings.repeat((String)indent, (int)(depth + 1)));
            text2.append(this.value.get(i).toText(indent, depth + 1));
            if (i != this.value.size() - 1) {
                text2.append(String.valueOf(',')).append(indent.isEmpty() ? " " : "\n");
            }
            text.append(text2);
        }
        if (!indent.isEmpty()) {
            text.append("\n").append(Strings.repeat((String)indent, (int)depth));
        }
        text.append("]");
        return text;
    }

    public int getElementType() {
        return this.type;
    }

    @Override
    public void clear() {
        this.value.clear();
        this.type = 0;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int value, Object object) {
        this.add(value, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int index, Object object) {
        return this.set(index, (Tag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

