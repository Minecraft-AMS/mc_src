/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import net.minecraft.world.chunk.SingularPalette;
import org.jetbrains.annotations.Nullable;

public class PalettedContainer<T>
implements PaletteResizeListener<T> {
    private static final int field_34557 = 0;
    private final PaletteResizeListener<T> dummyListener = (newSize, added) -> 0;
    private final IndexedIterable<T> idList;
    private volatile Data<T> data;
    private final PaletteProvider paletteProvider;
    private final LockHelper lockHelper = new LockHelper("PalettedContainer");

    public void lock() {
        this.lockHelper.lock();
    }

    public void unlock() {
        this.lockHelper.unlock();
    }

    public static <T> Codec<PalettedContainer<T>> createCodec(IndexedIterable<T> idList, Codec<T> entryCodec, PaletteProvider provider, T object) {
        return RecordCodecBuilder.create(instance -> instance.group((App)entryCodec.mapResult(Codecs.orElsePartial(object)).listOf().fieldOf("palette").forGetter(Serialized::paletteEntries), (App)Codec.LONG_STREAM.optionalFieldOf("data").forGetter(Serialized::storage)).apply((Applicative)instance, Serialized::new)).comapFlatMap(serialized -> PalettedContainer.read(idList, provider, serialized), container -> container.write(idList, provider));
    }

    public PalettedContainer(IndexedIterable<T> idList, PaletteProvider paletteProvider, DataProvider<T> dataProvider, PaletteStorage storage, List<T> paletteEntries) {
        this.idList = idList;
        this.paletteProvider = paletteProvider;
        this.data = new Data<T>(dataProvider, storage, dataProvider.factory().create(dataProvider.bits(), idList, this, paletteEntries));
    }

    private PalettedContainer(IndexedIterable<T> idList, PaletteProvider paletteProvider, Data<T> data) {
        this.idList = idList;
        this.paletteProvider = paletteProvider;
        this.data = data;
    }

    public PalettedContainer(IndexedIterable<T> idList, T object, PaletteProvider paletteProvider) {
        this.paletteProvider = paletteProvider;
        this.idList = idList;
        this.data = this.getCompatibleData(null, 0);
        this.data.palette.index(object);
    }

    private Data<T> getCompatibleData(@Nullable Data<T> previousData, int bits) {
        DataProvider<T> dataProvider = this.paletteProvider.createDataProvider(this.idList, bits);
        if (previousData != null && dataProvider.equals(previousData.configuration())) {
            return previousData;
        }
        return dataProvider.createData(this.idList, this, this.paletteProvider.getContainerSize());
    }

    @Override
    public int onResize(int i, T object) {
        Data<T> data = this.data;
        Data data2 = this.getCompatibleData(data, i);
        data2.importFrom(data.palette, data.storage);
        this.data = data2;
        return data2.palette.index(object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T swap(int x, int y, int z, T value) {
        this.lock();
        try {
            T t = this.swap(this.paletteProvider.computeIndex(x, y, z), value);
            return t;
        }
        finally {
            this.unlock();
        }
    }

    public T swapUnsafe(int x, int y, int z, T value) {
        return this.swap(this.paletteProvider.computeIndex(x, y, z), value);
    }

    private T swap(int index, T value) {
        int i = this.data.palette.index(value);
        int j = this.data.storage.swap(index, i);
        return this.data.palette.get(j);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int x, int y, int z, T value) {
        this.lock();
        try {
            this.set(this.paletteProvider.computeIndex(x, y, z), value);
        }
        finally {
            this.unlock();
        }
    }

    private void set(int index, T value) {
        int i = this.data.palette.index(value);
        this.data.storage.set(index, i);
    }

    public T get(int x, int y, int z) {
        return this.get(this.paletteProvider.computeIndex(x, y, z));
    }

    protected T get(int index) {
        Data<T> data = this.data;
        return data.palette.get(data.storage.get(index));
    }

    public void method_39793(Consumer<T> consumer) {
        Palette palette = this.data.palette();
        IntArraySet intSet = new IntArraySet();
        this.data.storage.forEach(arg_0 -> ((IntSet)intSet).add(arg_0));
        intSet.forEach(id -> consumer.accept(palette.get(id)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readPacket(PacketByteBuf buf) {
        this.lock();
        try {
            byte i = buf.readByte();
            Data<T> data = this.getCompatibleData(this.data, i);
            data.palette.readPacket(buf);
            buf.readLongArray(data.storage.getData());
            this.data = data;
        }
        finally {
            this.unlock();
        }
    }

    public void writePacket(PacketByteBuf buf) {
        this.lock();
        try {
            this.data.writePacket(buf);
        }
        finally {
            this.unlock();
        }
    }

    private static <T> DataResult<PalettedContainer<T>> read(IndexedIterable<T> idList, PaletteProvider provider, Serialized<T> serialized) {
        PaletteStorage paletteStorage;
        List<T> list = serialized.paletteEntries();
        int i2 = provider.getContainerSize();
        int j = provider.getBits(idList, list.size());
        DataProvider<T> dataProvider = provider.createDataProvider(idList, j);
        if (j == 0) {
            paletteStorage = new EmptyPaletteStorage(i2);
        } else {
            Optional<LongStream> optional = serialized.storage();
            if (optional.isEmpty()) {
                return DataResult.error((String)"Missing values for non-zero storage");
            }
            long[] ls = optional.get().toArray();
            try {
                if (dataProvider.factory() == PaletteProvider.ID_LIST) {
                    BiMapPalette<Object> palette = new BiMapPalette<Object>(idList, j, (i, object) -> 0, list);
                    PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i2, ls);
                    int[] is = new int[i2];
                    packedIntegerArray.method_39892(is);
                    PalettedContainer.method_39894(is, i -> idList.getRawId(palette.get(i)));
                    paletteStorage = new PackedIntegerArray(dataProvider.bits(), i2, is);
                } else {
                    paletteStorage = new PackedIntegerArray(dataProvider.bits(), i2, ls);
                }
            }
            catch (PackedIntegerArray.InvalidLengthException invalidLengthException) {
                return DataResult.error((String)("Failed to read PalettedContainer: " + invalidLengthException.getMessage()));
            }
        }
        return DataResult.success(new PalettedContainer<T>(idList, provider, dataProvider, paletteStorage, list));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Serialized<T> write(IndexedIterable<T> idList, PaletteProvider provider) {
        this.lock();
        try {
            Optional<LongStream> optional;
            BiMapPalette<T> biMapPalette = new BiMapPalette<T>(idList, this.data.storage.getElementBits(), this.dummyListener);
            int i = provider.getContainerSize();
            int[] is = new int[i];
            this.data.storage.method_39892(is);
            PalettedContainer.method_39894(is, id -> biMapPalette.index(this.data.palette.get(id)));
            int j = provider.getBits(idList, biMapPalette.getSize());
            if (j != 0) {
                PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i, is);
                optional = Optional.of(Arrays.stream(packedIntegerArray.getData()));
            } else {
                optional = Optional.empty();
            }
            Serialized<T> serialized = new Serialized<T>(biMapPalette.getElements(), optional);
            return serialized;
        }
        finally {
            this.unlock();
        }
    }

    private static <T> void method_39894(int[] is, IntUnaryOperator intUnaryOperator) {
        int i = -1;
        int j = -1;
        for (int k = 0; k < is.length; ++k) {
            int l = is[k];
            if (l != i) {
                i = l;
                j = intUnaryOperator.applyAsInt(l);
            }
            is[k] = j;
        }
    }

    public int getPacketSize() {
        return this.data.getPacketSize();
    }

    public boolean hasAny(Predicate<T> predicate) {
        return this.data.palette.hasAny(predicate);
    }

    public PalettedContainer<T> copy() {
        return new PalettedContainer<T>(this.idList, this.paletteProvider, new Data<T>(this.data.configuration(), this.data.storage().copy(), this.data.palette().copy()));
    }

    public void count(Counter<T> counter) {
        if (this.data.palette.getSize() == 1) {
            counter.accept(this.data.palette.get(0), this.data.storage.getSize());
            return;
        }
        Int2IntOpenHashMap int2IntOpenHashMap = new Int2IntOpenHashMap();
        this.data.storage.forEach(key -> int2IntOpenHashMap.addTo(key, 1));
        int2IntOpenHashMap.int2IntEntrySet().forEach(entry -> counter.accept(this.data.palette.get(entry.getIntKey()), entry.getIntValue()));
    }

    public static abstract class PaletteProvider {
        public static final Palette.Factory SINGULAR = SingularPalette::create;
        public static final Palette.Factory ARRAY = ArrayPalette::create;
        public static final Palette.Factory BI_MAP = BiMapPalette::create;
        static final Palette.Factory ID_LIST = IdListPalette::create;
        public static final PaletteProvider BLOCK_STATE = new PaletteProvider(4){

            @Override
            public <A> DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
                return switch (bits) {
                    case 0 -> new DataProvider(SINGULAR, bits);
                    case 1, 2, 3, 4 -> new DataProvider(ARRAY, 4);
                    case 5, 6, 7, 8 -> new DataProvider(BI_MAP, bits);
                    default -> new DataProvider(ID_LIST, MathHelper.ceilLog2(idList.size()));
                };
            }
        };
        public static final PaletteProvider BIOME = new PaletteProvider(2){

            @Override
            public <A> DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
                return switch (bits) {
                    case 0 -> new DataProvider(SINGULAR, bits);
                    case 1, 2, 3 -> new DataProvider(ARRAY, bits);
                    default -> new DataProvider(ID_LIST, MathHelper.ceilLog2(idList.size()));
                };
            }
        };
        private final int edgeBits;

        PaletteProvider(int edgeBits) {
            this.edgeBits = edgeBits;
        }

        public int getContainerSize() {
            return 1 << this.edgeBits * 3;
        }

        public int computeIndex(int x, int y, int z) {
            return (y << this.edgeBits | z) << this.edgeBits | x;
        }

        public abstract <A> DataProvider<A> createDataProvider(IndexedIterable<A> var1, int var2);

        <A> int getBits(IndexedIterable<A> idList, int size) {
            int i = MathHelper.ceilLog2(size);
            DataProvider<A> dataProvider = this.createDataProvider(idList, i);
            return dataProvider.factory() == ID_LIST ? i : dataProvider.bits();
        }
    }

    static final class Data<T>
    extends Record {
        private final DataProvider<T> configuration;
        final PaletteStorage storage;
        final Palette<T> palette;

        Data(DataProvider<T> configuration, PaletteStorage storage, Palette<T> palette) {
            this.configuration = configuration;
            this.storage = storage;
            this.palette = palette;
        }

        public void importFrom(Palette<T> palette, PaletteStorage storage) {
            for (int i = 0; i < storage.getSize(); ++i) {
                T object = palette.get(storage.get(i));
                this.storage.set(i, this.palette.index(object));
            }
        }

        public int getPacketSize() {
            return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntLength(this.storage.getSize()) + this.storage.getData().length * 8;
        }

        public void writePacket(PacketByteBuf buf) {
            buf.writeByte(this.storage.getElementBits());
            this.palette.writePacket(buf);
            buf.writeLongArray(this.storage.getData());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "configuration;storage;palette", "configuration", "storage", "palette"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "configuration;storage;palette", "configuration", "storage", "palette"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "configuration;storage;palette", "configuration", "storage", "palette"}, this, object);
        }

        public DataProvider<T> configuration() {
            return this.configuration;
        }

        public PaletteStorage storage() {
            return this.storage;
        }

        public Palette<T> palette() {
            return this.palette;
        }
    }

    record DataProvider<T>(Palette.Factory factory, int bits) {
        public Data<T> createData(IndexedIterable<T> idList, PaletteResizeListener<T> listener, int size) {
            PaletteStorage paletteStorage = this.bits == 0 ? new EmptyPaletteStorage(size) : new PackedIntegerArray(this.bits, size);
            Palette<T> palette = this.factory.create(this.bits, idList, listener, List.of());
            return new Data<T>(this, paletteStorage, palette);
        }
    }

    record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage) {
    }

    @FunctionalInterface
    public static interface Counter<T> {
        public void accept(T var1, int var2);
    }
}

