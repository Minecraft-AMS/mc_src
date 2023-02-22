/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.PeekingIterator
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$AbstractStringBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.Nullable;

public class NbtOps
implements DynamicOps<NbtElement> {
    public static final NbtOps INSTANCE = new NbtOps();

    protected NbtOps() {
    }

    public NbtElement empty() {
        return NbtNull.INSTANCE;
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, NbtElement nbtElement) {
        switch (nbtElement.getType()) {
            case 0: {
                return (U)dynamicOps.empty();
            }
            case 1: {
                return (U)dynamicOps.createByte(((AbstractNbtNumber)nbtElement).byteValue());
            }
            case 2: {
                return (U)dynamicOps.createShort(((AbstractNbtNumber)nbtElement).shortValue());
            }
            case 3: {
                return (U)dynamicOps.createInt(((AbstractNbtNumber)nbtElement).intValue());
            }
            case 4: {
                return (U)dynamicOps.createLong(((AbstractNbtNumber)nbtElement).longValue());
            }
            case 5: {
                return (U)dynamicOps.createFloat(((AbstractNbtNumber)nbtElement).floatValue());
            }
            case 6: {
                return (U)dynamicOps.createDouble(((AbstractNbtNumber)nbtElement).doubleValue());
            }
            case 7: {
                return (U)dynamicOps.createByteList(ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()));
            }
            case 8: {
                return (U)dynamicOps.createString(nbtElement.asString());
            }
            case 9: {
                return (U)this.convertList(dynamicOps, nbtElement);
            }
            case 10: {
                return (U)this.convertMap(dynamicOps, nbtElement);
            }
            case 11: {
                return (U)dynamicOps.createIntList(Arrays.stream(((NbtIntArray)nbtElement).getIntArray()));
            }
            case 12: {
                return (U)dynamicOps.createLongList(Arrays.stream(((NbtLongArray)nbtElement).getLongArray()));
            }
        }
        throw new IllegalStateException("Unknown tag type: " + nbtElement);
    }

    public DataResult<Number> getNumberValue(NbtElement nbtElement) {
        if (nbtElement instanceof AbstractNbtNumber) {
            return DataResult.success((Object)((AbstractNbtNumber)nbtElement).numberValue());
        }
        return DataResult.error((String)"Not a number");
    }

    public NbtElement createNumeric(Number number) {
        return NbtDouble.of(number.doubleValue());
    }

    public NbtElement createByte(byte b) {
        return NbtByte.of(b);
    }

    public NbtElement createShort(short s) {
        return NbtShort.of(s);
    }

    public NbtElement createInt(int i) {
        return NbtInt.of(i);
    }

    public NbtElement createLong(long l) {
        return NbtLong.of(l);
    }

    public NbtElement createFloat(float f) {
        return NbtFloat.of(f);
    }

    public NbtElement createDouble(double d) {
        return NbtDouble.of(d);
    }

    public NbtElement createBoolean(boolean bl) {
        return NbtByte.of(bl);
    }

    public DataResult<String> getStringValue(NbtElement nbtElement) {
        if (nbtElement instanceof NbtString) {
            return DataResult.success((Object)nbtElement.asString());
        }
        return DataResult.error((String)"Not a string");
    }

    public NbtElement createString(String string) {
        return NbtString.of(string);
    }

    private static AbstractNbtList<?> method_29144(byte b, byte c) {
        if (NbtOps.method_29145(b, c, (byte)4)) {
            return new NbtLongArray(new long[0]);
        }
        if (NbtOps.method_29145(b, c, (byte)1)) {
            return new NbtByteArray(new byte[0]);
        }
        if (NbtOps.method_29145(b, c, (byte)3)) {
            return new NbtIntArray(new int[0]);
        }
        return new NbtList();
    }

    private static boolean method_29145(byte b, byte c, byte d) {
        return b == d && (c == d || c == 0);
    }

    private static <T extends NbtElement> void method_29151(AbstractNbtList<T> abstractNbtList, NbtElement nbtElement2, NbtElement nbtElement22) {
        if (nbtElement2 instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList2 = (AbstractNbtList)nbtElement2;
            abstractNbtList2.forEach(nbtElement -> abstractNbtList.add(nbtElement));
        }
        abstractNbtList.add(nbtElement22);
    }

    private static <T extends NbtElement> void method_29150(AbstractNbtList<T> abstractNbtList, NbtElement nbtElement2, List<NbtElement> list) {
        if (nbtElement2 instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList2 = (AbstractNbtList)nbtElement2;
            abstractNbtList2.forEach(nbtElement -> abstractNbtList.add(nbtElement));
        }
        list.forEach(nbtElement -> abstractNbtList.add(nbtElement));
    }

    public DataResult<NbtElement> mergeToList(NbtElement nbtElement, NbtElement nbtElement2) {
        if (!(nbtElement instanceof AbstractNbtList) && !(nbtElement instanceof NbtNull)) {
            return DataResult.error((String)("mergeToList called with not a list: " + nbtElement), (Object)nbtElement);
        }
        AbstractNbtList<?> abstractNbtList = NbtOps.method_29144(nbtElement instanceof AbstractNbtList ? ((AbstractNbtList)nbtElement).getHeldType() : (byte)0, nbtElement2.getType());
        NbtOps.method_29151(abstractNbtList, nbtElement, nbtElement2);
        return DataResult.success(abstractNbtList);
    }

    public DataResult<NbtElement> mergeToList(NbtElement nbtElement, List<NbtElement> list) {
        if (!(nbtElement instanceof AbstractNbtList) && !(nbtElement instanceof NbtNull)) {
            return DataResult.error((String)("mergeToList called with not a list: " + nbtElement), (Object)nbtElement);
        }
        AbstractNbtList<?> abstractNbtList = NbtOps.method_29144(nbtElement instanceof AbstractNbtList ? ((AbstractNbtList)nbtElement).getHeldType() : (byte)0, list.stream().findFirst().map(NbtElement::getType).orElse((byte)0));
        NbtOps.method_29150(abstractNbtList, nbtElement, list);
        return DataResult.success(abstractNbtList);
    }

    public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
        if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtNull)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + nbtElement), (Object)nbtElement);
        }
        if (!(nbtElement2 instanceof NbtString)) {
            return DataResult.error((String)("key is not a string: " + nbtElement2), (Object)nbtElement);
        }
        NbtCompound nbtCompound = new NbtCompound();
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
            nbtCompound2.getKeys().forEach(string -> nbtCompound.put((String)string, nbtCompound2.get((String)string)));
        }
        nbtCompound.put(nbtElement2.asString(), nbtElement3);
        return DataResult.success((Object)nbtCompound);
    }

    public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, MapLike<NbtElement> mapLike) {
        if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtNull)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + nbtElement), (Object)nbtElement);
        }
        NbtCompound nbtCompound = new NbtCompound();
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
            nbtCompound2.getKeys().forEach(string -> nbtCompound.put((String)string, nbtCompound2.get((String)string)));
        }
        ArrayList list = Lists.newArrayList();
        mapLike.entries().forEach(pair -> {
            NbtElement nbtElement = (NbtElement)pair.getFirst();
            if (!(nbtElement instanceof NbtString)) {
                list.add(nbtElement);
                return;
            }
            nbtCompound.put(nbtElement.asString(), (NbtElement)pair.getSecond());
        });
        if (!list.isEmpty()) {
            return DataResult.error((String)("some keys are not strings: " + list), (Object)nbtCompound);
        }
        return DataResult.success((Object)nbtCompound);
    }

    public DataResult<Stream<Pair<NbtElement, NbtElement>>> getMapValues(NbtElement nbtElement) {
        if (!(nbtElement instanceof NbtCompound)) {
            return DataResult.error((String)("Not a map: " + nbtElement));
        }
        NbtCompound nbtCompound = (NbtCompound)nbtElement;
        return DataResult.success(nbtCompound.getKeys().stream().map(string -> Pair.of((Object)this.createString((String)string), (Object)nbtCompound.get((String)string))));
    }

    public DataResult<Consumer<BiConsumer<NbtElement, NbtElement>>> getMapEntries(NbtElement nbtElement) {
        if (!(nbtElement instanceof NbtCompound)) {
            return DataResult.error((String)("Not a map: " + nbtElement));
        }
        NbtCompound nbtCompound = (NbtCompound)nbtElement;
        return DataResult.success(biConsumer -> nbtCompound.getKeys().forEach(string -> biConsumer.accept(this.createString((String)string), nbtCompound.get((String)string))));
    }

    public DataResult<MapLike<NbtElement>> getMap(NbtElement nbtElement) {
        if (!(nbtElement instanceof NbtCompound)) {
            return DataResult.error((String)("Not a map: " + nbtElement));
        }
        final NbtCompound nbtCompound = (NbtCompound)nbtElement;
        return DataResult.success((Object)new MapLike<NbtElement>(){

            @Nullable
            public NbtElement get(NbtElement nbtElement) {
                return nbtCompound.get(nbtElement.asString());
            }

            @Nullable
            public NbtElement get(String string) {
                return nbtCompound.get(string);
            }

            public Stream<Pair<NbtElement, NbtElement>> entries() {
                return nbtCompound.getKeys().stream().map(string -> Pair.of((Object)NbtOps.this.createString((String)string), (Object)nbtCompound.get((String)string)));
            }

            public String toString() {
                return "MapLike[" + nbtCompound + "]";
            }

            @Nullable
            public /* synthetic */ Object get(String string) {
                return this.get(string);
            }

            @Nullable
            public /* synthetic */ Object get(Object object) {
                return this.get((NbtElement)object);
            }
        });
    }

    public NbtElement createMap(Stream<Pair<NbtElement, NbtElement>> stream) {
        NbtCompound nbtCompound = new NbtCompound();
        stream.forEach(pair -> nbtCompound.put(((NbtElement)pair.getFirst()).asString(), (NbtElement)pair.getSecond()));
        return nbtCompound;
    }

    public DataResult<Stream<NbtElement>> getStream(NbtElement nbtElement2) {
        if (nbtElement2 instanceof AbstractNbtList) {
            return DataResult.success(((AbstractNbtList)nbtElement2).stream().map(nbtElement -> nbtElement));
        }
        return DataResult.error((String)"Not a list");
    }

    public DataResult<Consumer<Consumer<NbtElement>>> getList(NbtElement nbtElement) {
        if (nbtElement instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            return DataResult.success(abstractNbtList::forEach);
        }
        return DataResult.error((String)("Not a list: " + nbtElement));
    }

    public DataResult<ByteBuffer> getByteBuffer(NbtElement nbtElement) {
        if (nbtElement instanceof NbtByteArray) {
            return DataResult.success((Object)ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()));
        }
        return super.getByteBuffer((Object)nbtElement);
    }

    public NbtElement createByteList(ByteBuffer byteBuffer) {
        return new NbtByteArray(DataFixUtils.toArray((ByteBuffer)byteBuffer));
    }

    public DataResult<IntStream> getIntStream(NbtElement nbtElement) {
        if (nbtElement instanceof NbtIntArray) {
            return DataResult.success((Object)Arrays.stream(((NbtIntArray)nbtElement).getIntArray()));
        }
        return super.getIntStream((Object)nbtElement);
    }

    public NbtElement createIntList(IntStream intStream) {
        return new NbtIntArray(intStream.toArray());
    }

    public DataResult<LongStream> getLongStream(NbtElement nbtElement) {
        if (nbtElement instanceof NbtLongArray) {
            return DataResult.success((Object)Arrays.stream(((NbtLongArray)nbtElement).getLongArray()));
        }
        return super.getLongStream((Object)nbtElement);
    }

    public NbtElement createLongList(LongStream longStream) {
        return new NbtLongArray(longStream.toArray());
    }

    public NbtElement createList(Stream<NbtElement> stream) {
        PeekingIterator peekingIterator = Iterators.peekingIterator(stream.iterator());
        if (!peekingIterator.hasNext()) {
            return new NbtList();
        }
        NbtElement nbtElement2 = (NbtElement)peekingIterator.peek();
        if (nbtElement2 instanceof NbtByte) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, nbtElement -> ((NbtByte)nbtElement).byteValue()));
            return new NbtByteArray(list);
        }
        if (nbtElement2 instanceof NbtInt) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, nbtElement -> ((NbtInt)nbtElement).intValue()));
            return new NbtIntArray(list);
        }
        if (nbtElement2 instanceof NbtLong) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, nbtElement -> ((NbtLong)nbtElement).longValue()));
            return new NbtLongArray(list);
        }
        NbtList nbtList = new NbtList();
        while (peekingIterator.hasNext()) {
            NbtElement nbtElement22 = (NbtElement)peekingIterator.next();
            if (nbtElement22 instanceof NbtNull) continue;
            nbtList.add(nbtElement22);
        }
        return nbtList;
    }

    public NbtElement remove(NbtElement nbtElement, String string) {
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound.getKeys().stream().filter(k -> !Objects.equals(k, string)).forEach(k -> nbtCompound2.put((String)k, nbtCompound.get((String)k)));
            return nbtCompound2;
        }
        return nbtElement;
    }

    public String toString() {
        return "NBT";
    }

    public RecordBuilder<NbtElement> mapBuilder() {
        return new MapBuilder();
    }

    public /* synthetic */ Object remove(Object element, String key) {
        return this.remove((NbtElement)element, key);
    }

    public /* synthetic */ Object createLongList(LongStream longStream) {
        return this.createLongList(longStream);
    }

    public /* synthetic */ DataResult getLongStream(Object element) {
        return this.getLongStream((NbtElement)element);
    }

    public /* synthetic */ Object createIntList(IntStream intStream) {
        return this.createIntList(intStream);
    }

    public /* synthetic */ DataResult getIntStream(Object element) {
        return this.getIntStream((NbtElement)element);
    }

    public /* synthetic */ Object createByteList(ByteBuffer byteBuffer) {
        return this.createByteList(byteBuffer);
    }

    public /* synthetic */ DataResult getByteBuffer(Object element) {
        return this.getByteBuffer((NbtElement)element);
    }

    public /* synthetic */ Object createList(Stream stream) {
        return this.createList(stream);
    }

    public /* synthetic */ DataResult getList(Object element) {
        return this.getList((NbtElement)element);
    }

    public /* synthetic */ DataResult getStream(Object element) {
        return this.getStream((NbtElement)element);
    }

    public /* synthetic */ DataResult getMap(Object element) {
        return this.getMap((NbtElement)element);
    }

    public /* synthetic */ Object createMap(Stream stream) {
        return this.createMap(stream);
    }

    public /* synthetic */ DataResult getMapEntries(Object element) {
        return this.getMapEntries((NbtElement)element);
    }

    public /* synthetic */ DataResult getMapValues(Object element) {
        return this.getMapValues((NbtElement)element);
    }

    public /* synthetic */ DataResult mergeToMap(Object element, MapLike mapLike) {
        return this.mergeToMap((NbtElement)element, (MapLike<NbtElement>)mapLike);
    }

    public /* synthetic */ DataResult mergeToMap(Object map, Object key, Object value) {
        return this.mergeToMap((NbtElement)map, (NbtElement)key, (NbtElement)value);
    }

    public /* synthetic */ DataResult mergeToList(Object object, List list) {
        return this.mergeToList((NbtElement)object, (List<NbtElement>)list);
    }

    public /* synthetic */ DataResult mergeToList(Object list, Object value) {
        return this.mergeToList((NbtElement)list, (NbtElement)value);
    }

    public /* synthetic */ Object createString(String string) {
        return this.createString(string);
    }

    public /* synthetic */ DataResult getStringValue(Object element) {
        return this.getStringValue((NbtElement)element);
    }

    public /* synthetic */ Object createBoolean(boolean value) {
        return this.createBoolean(value);
    }

    public /* synthetic */ Object createDouble(double value) {
        return this.createDouble(value);
    }

    public /* synthetic */ Object createFloat(float value) {
        return this.createFloat(value);
    }

    public /* synthetic */ Object createLong(long value) {
        return this.createLong(value);
    }

    public /* synthetic */ Object createInt(int value) {
        return this.createInt(value);
    }

    public /* synthetic */ Object createShort(short value) {
        return this.createShort(value);
    }

    public /* synthetic */ Object createByte(byte value) {
        return this.createByte(value);
    }

    public /* synthetic */ Object createNumeric(Number value) {
        return this.createNumeric(value);
    }

    public /* synthetic */ DataResult getNumberValue(Object element) {
        return this.getNumberValue((NbtElement)element);
    }

    public /* synthetic */ Object convertTo(DynamicOps dynamicOps, Object element) {
        return this.convertTo(dynamicOps, (NbtElement)element);
    }

    public /* synthetic */ Object empty() {
        return this.empty();
    }

    class MapBuilder
    extends RecordBuilder.AbstractStringBuilder<NbtElement, NbtCompound> {
        protected MapBuilder() {
            super((DynamicOps)NbtOps.this);
        }

        protected NbtCompound initBuilder() {
            return new NbtCompound();
        }

        protected NbtCompound append(String string, NbtElement nbtElement, NbtCompound nbtCompound) {
            nbtCompound.put(string, nbtElement);
            return nbtCompound;
        }

        protected DataResult<NbtElement> build(NbtCompound nbtCompound, NbtElement nbtElement) {
            if (nbtElement == null || nbtElement == NbtNull.INSTANCE) {
                return DataResult.success((Object)nbtCompound);
            }
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound2 = new NbtCompound(Maps.newHashMap(((NbtCompound)nbtElement).toMap()));
                for (Map.Entry<String, NbtElement> entry : nbtCompound.toMap().entrySet()) {
                    nbtCompound2.put(entry.getKey(), entry.getValue());
                }
                return DataResult.success((Object)nbtCompound2);
            }
            return DataResult.error((String)("mergeToMap called with not a map: " + nbtElement), (Object)nbtElement);
        }

        protected /* synthetic */ Object append(String string, Object object, Object object2) {
            return this.append(string, (NbtElement)object, (NbtCompound)object2);
        }

        protected /* synthetic */ DataResult build(Object object, Object object2) {
            return this.build((NbtCompound)object, (NbtElement)object2);
        }

        protected /* synthetic */ Object initBuilder() {
            return this.initBuilder();
        }
    }
}

