/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NbtOps
implements DynamicOps<Tag> {
    public static final NbtOps INSTANCE = new NbtOps();

    protected NbtOps() {
    }

    public Tag empty() {
        return new EndTag();
    }

    public Type<?> getType(Tag tag) {
        switch (tag.getType()) {
            case 0: {
                return DSL.nilType();
            }
            case 1: {
                return DSL.byteType();
            }
            case 2: {
                return DSL.shortType();
            }
            case 3: {
                return DSL.intType();
            }
            case 4: {
                return DSL.longType();
            }
            case 5: {
                return DSL.floatType();
            }
            case 6: {
                return DSL.doubleType();
            }
            case 7: {
                return DSL.list((Type)DSL.byteType());
            }
            case 8: {
                return DSL.string();
            }
            case 9: {
                return DSL.list((Type)DSL.remainderType());
            }
            case 10: {
                return DSL.compoundList((Type)DSL.remainderType(), (Type)DSL.remainderType());
            }
            case 11: {
                return DSL.list((Type)DSL.intType());
            }
            case 12: {
                return DSL.list((Type)DSL.longType());
            }
        }
        return DSL.remainderType();
    }

    public Optional<Number> getNumberValue(Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            return Optional.of(((AbstractNumberTag)tag).getNumber());
        }
        return Optional.empty();
    }

    public Tag createNumeric(Number number) {
        return new DoubleTag(number.doubleValue());
    }

    public Tag createByte(byte b) {
        return new ByteTag(b);
    }

    public Tag createShort(short s) {
        return new ShortTag(s);
    }

    public Tag createInt(int i) {
        return new IntTag(i);
    }

    public Tag createLong(long l) {
        return new LongTag(l);
    }

    public Tag createFloat(float f) {
        return new FloatTag(f);
    }

    public Tag createDouble(double d) {
        return new DoubleTag(d);
    }

    public Optional<String> getStringValue(Tag tag) {
        if (tag instanceof StringTag) {
            return Optional.of(tag.asString());
        }
        return Optional.empty();
    }

    public Tag createString(String string) {
        return new StringTag(string);
    }

    public Tag mergeInto(Tag tag, Tag tag2) {
        if (tag2 instanceof EndTag) {
            return tag;
        }
        if (tag instanceof CompoundTag) {
            if (tag2 instanceof CompoundTag) {
                CompoundTag compoundTag = new CompoundTag();
                CompoundTag compoundTag2 = (CompoundTag)tag;
                for (String string : compoundTag2.getKeys()) {
                    compoundTag.put(string, compoundTag2.get(string));
                }
                CompoundTag compoundTag3 = (CompoundTag)tag2;
                for (String string2 : compoundTag3.getKeys()) {
                    compoundTag.put(string2, compoundTag3.get(string2));
                }
                return compoundTag;
            }
            return tag;
        }
        if (tag instanceof EndTag) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
        }
        if (!(tag instanceof AbstractListTag)) {
            return tag;
        }
        ListTag abstractListTag = new ListTag();
        AbstractListTag abstractListTag2 = (AbstractListTag)tag;
        abstractListTag.addAll(abstractListTag2);
        abstractListTag.add(tag2);
        return abstractListTag;
    }

    public Tag mergeInto(Tag tag, Tag tag2, Tag tag3) {
        CompoundTag compoundTag;
        if (tag instanceof EndTag) {
            compoundTag = new CompoundTag();
        } else if (tag instanceof CompoundTag) {
            CompoundTag compoundTag2 = (CompoundTag)tag;
            compoundTag = new CompoundTag();
            compoundTag2.getKeys().forEach(string -> compoundTag.put((String)string, compoundTag2.get((String)string)));
        } else {
            return tag;
        }
        compoundTag.put(tag2.asString(), tag3);
        return compoundTag;
    }

    public Tag merge(Tag tag, Tag tag2) {
        if (tag instanceof EndTag) {
            return tag2;
        }
        if (tag2 instanceof EndTag) {
            return tag;
        }
        if (tag instanceof CompoundTag && tag2 instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            CompoundTag compoundTag2 = (CompoundTag)tag2;
            CompoundTag compoundTag3 = new CompoundTag();
            compoundTag.getKeys().forEach(string -> compoundTag3.put((String)string, compoundTag.get((String)string)));
            compoundTag2.getKeys().forEach(string -> compoundTag3.put((String)string, compoundTag2.get((String)string)));
        }
        if (tag instanceof AbstractListTag && tag2 instanceof AbstractListTag) {
            ListTag listTag = new ListTag();
            listTag.addAll((AbstractListTag)tag);
            listTag.addAll((AbstractListTag)tag2);
            return listTag;
        }
        throw new IllegalArgumentException("Could not merge " + tag + " and " + tag2);
    }

    public Optional<Map<Tag, Tag>> getMapValues(Tag tag) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            return Optional.of(compoundTag.getKeys().stream().map(string -> Pair.of((Object)this.createString((String)string), (Object)compoundTag.get((String)string))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        }
        return Optional.empty();
    }

    public Tag createMap(Map<Tag, Tag> map) {
        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<Tag, Tag> entry : map.entrySet()) {
            compoundTag.put(entry.getKey().asString(), entry.getValue());
        }
        return compoundTag;
    }

    public Optional<Stream<Tag>> getStream(Tag tag2) {
        if (tag2 instanceof AbstractListTag) {
            return Optional.of(((AbstractListTag)tag2).stream().map(tag -> tag));
        }
        return Optional.empty();
    }

    public Optional<ByteBuffer> getByteBuffer(Tag tag) {
        if (tag instanceof ByteArrayTag) {
            return Optional.of(ByteBuffer.wrap(((ByteArrayTag)tag).getByteArray()));
        }
        return super.getByteBuffer((Object)tag);
    }

    public Tag createByteList(ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray((ByteBuffer)byteBuffer));
    }

    public Optional<IntStream> getIntStream(Tag tag) {
        if (tag instanceof IntArrayTag) {
            return Optional.of(Arrays.stream(((IntArrayTag)tag).getIntArray()));
        }
        return super.getIntStream((Object)tag);
    }

    public Tag createIntList(IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }

    public Optional<LongStream> getLongStream(Tag tag) {
        if (tag instanceof LongArrayTag) {
            return Optional.of(Arrays.stream(((LongArrayTag)tag).getLongArray()));
        }
        return super.getLongStream((Object)tag);
    }

    public Tag createLongList(LongStream longStream) {
        return new LongArrayTag(longStream.toArray());
    }

    public Tag createList(Stream<Tag> stream) {
        PeekingIterator peekingIterator = Iterators.peekingIterator(stream.iterator());
        if (!peekingIterator.hasNext()) {
            return new ListTag();
        }
        Tag tag2 = (Tag)peekingIterator.peek();
        if (tag2 instanceof ByteTag) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, tag -> ((ByteTag)tag).getByte()));
            return new ByteArrayTag(list);
        }
        if (tag2 instanceof IntTag) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, tag -> ((IntTag)tag).getInt()));
            return new IntArrayTag(list);
        }
        if (tag2 instanceof LongTag) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, tag -> ((LongTag)tag).getLong()));
            return new LongArrayTag(list);
        }
        ListTag listTag = new ListTag();
        while (peekingIterator.hasNext()) {
            Tag tag22 = (Tag)peekingIterator.next();
            if (tag22 instanceof EndTag) continue;
            listTag.add(tag22);
        }
        return listTag;
    }

    public Tag remove(Tag tag, String string3) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag.getKeys().stream().filter(string2 -> !Objects.equals(string2, string3)).forEach(string -> compoundTag2.put((String)string, compoundTag.get((String)string)));
            return compoundTag2;
        }
        return tag;
    }

    public String toString() {
        return "NBT";
    }

    public /* synthetic */ Object remove(Object object, String string) {
        return this.remove((Tag)object, string);
    }

    public /* synthetic */ Object createLongList(LongStream longStream) {
        return this.createLongList(longStream);
    }

    public /* synthetic */ Optional getLongStream(Object object) {
        return this.getLongStream((Tag)object);
    }

    public /* synthetic */ Object createIntList(IntStream intStream) {
        return this.createIntList(intStream);
    }

    public /* synthetic */ Optional getIntStream(Object object) {
        return this.getIntStream((Tag)object);
    }

    public /* synthetic */ Object createByteList(ByteBuffer byteBuffer) {
        return this.createByteList(byteBuffer);
    }

    public /* synthetic */ Optional getByteBuffer(Object object) {
        return this.getByteBuffer((Tag)object);
    }

    public /* synthetic */ Object createList(Stream stream) {
        return this.createList(stream);
    }

    public /* synthetic */ Optional getStream(Object object) {
        return this.getStream((Tag)object);
    }

    public /* synthetic */ Object createMap(Map map) {
        return this.createMap(map);
    }

    public /* synthetic */ Optional getMapValues(Object object) {
        return this.getMapValues((Tag)object);
    }

    public /* synthetic */ Object merge(Object object, Object object2) {
        return this.merge((Tag)object, (Tag)object2);
    }

    public /* synthetic */ Object mergeInto(Object object, Object object2, Object object3) {
        return this.mergeInto((Tag)object, (Tag)object2, (Tag)object3);
    }

    public /* synthetic */ Object mergeInto(Object object, Object object2) {
        return this.mergeInto((Tag)object, (Tag)object2);
    }

    public /* synthetic */ Object createString(String string) {
        return this.createString(string);
    }

    public /* synthetic */ Optional getStringValue(Object object) {
        return this.getStringValue((Tag)object);
    }

    public /* synthetic */ Object createDouble(double d) {
        return this.createDouble(d);
    }

    public /* synthetic */ Object createFloat(float f) {
        return this.createFloat(f);
    }

    public /* synthetic */ Object createLong(long l) {
        return this.createLong(l);
    }

    public /* synthetic */ Object createInt(int i) {
        return this.createInt(i);
    }

    public /* synthetic */ Object createShort(short s) {
        return this.createShort(s);
    }

    public /* synthetic */ Object createByte(byte b) {
        return this.createByte(b);
    }

    public /* synthetic */ Object createNumeric(Number number) {
        return this.createNumeric(number);
    }

    public /* synthetic */ Optional getNumberValue(Object object) {
        return this.getNumberValue((Tag)object);
    }

    public /* synthetic */ Type getType(Object object) {
        return this.getType((Tag)object);
    }

    public /* synthetic */ Object empty() {
        return this.empty();
    }
}

