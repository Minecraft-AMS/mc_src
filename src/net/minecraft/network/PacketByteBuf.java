/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ByteProcessor
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PacketByteBuf
extends ByteBuf {
    private static final int MAX_VAR_INT_LENGTH = 5;
    private static final int MAX_VAR_LONG_LENGTH = 10;
    private static final int MAX_READ_NBT_SIZE = 0x200000;
    private final ByteBuf parent;
    public static final short DEFAULT_MAX_STRING_LENGTH = Short.MAX_VALUE;
    public static final int MAX_TEXT_LENGTH = 262144;

    public PacketByteBuf(ByteBuf parent) {
        this.parent = parent;
    }

    public static int getVarIntLength(int value) {
        for (int i = 1; i < 5; ++i) {
            if ((value & -1 << i * 7) != 0) continue;
            return i;
        }
        return 5;
    }

    public static int getVarLongLength(long value) {
        for (int i = 1; i < 10; ++i) {
            if ((value & -1L << i * 7) != 0L) continue;
            return i;
        }
        return 10;
    }

    public <T> T decode(Codec<T> codec) {
        NbtCompound nbtCompound = this.readUnlimitedNbt();
        DataResult dataResult = codec.parse((DynamicOps)NbtOps.INSTANCE, (Object)nbtCompound);
        dataResult.error().ifPresent(partial -> {
            throw new EncoderException("Failed to decode: " + partial.message() + " " + nbtCompound);
        });
        return dataResult.result().get();
    }

    public <T> void encode(Codec<T> codec, T object) {
        DataResult dataResult = codec.encodeStart((DynamicOps)NbtOps.INSTANCE, object);
        dataResult.error().ifPresent(partial -> {
            throw new EncoderException("Failed to encode: " + partial.message() + " " + object);
        });
        this.writeNbt((NbtCompound)dataResult.result().get());
    }

    public static <T> IntFunction<T> getMaxValidator(IntFunction<T> applier, int max) {
        return value -> {
            if (value > max) {
                throw new DecoderException("Value " + value + " is larger than limit " + max);
            }
            return applier.apply(value);
        };
    }

    public <T, C extends Collection<T>> C readCollection(IntFunction<C> collectionFactory, Function<PacketByteBuf, T> entryParser) {
        int i = this.readVarInt();
        Collection collection = (Collection)collectionFactory.apply(i);
        for (int j = 0; j < i; ++j) {
            collection.add(entryParser.apply(this));
        }
        return (C)collection;
    }

    public <T> void writeCollection(Collection<T> collection, BiConsumer<PacketByteBuf, T> entrySerializer) {
        this.writeVarInt(collection.size());
        for (T object : collection) {
            entrySerializer.accept(this, (PacketByteBuf)((Object)object));
        }
    }

    public <T> List<T> readList(Function<PacketByteBuf, T> entryParser) {
        return this.readCollection(Lists::newArrayListWithCapacity, entryParser);
    }

    public IntList readIntList() {
        int i = this.readVarInt();
        IntArrayList intList = new IntArrayList();
        for (int j = 0; j < i; ++j) {
            intList.add(this.readVarInt());
        }
        return intList;
    }

    public void writeIntList(IntList list) {
        this.writeVarInt(list.size());
        list.forEach(this::writeVarInt);
    }

    public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
        int i = this.readVarInt();
        Map map = (Map)mapFactory.apply(i);
        for (int j = 0; j < i; ++j) {
            K object = keyParser.apply(this);
            V object2 = valueParser.apply(this);
            map.put(object, object2);
        }
        return (M)map;
    }

    public <K, V> Map<K, V> readMap(Function<PacketByteBuf, K> keyParser, Function<PacketByteBuf, V> valueParser) {
        return this.readMap(Maps::newHashMapWithExpectedSize, keyParser, valueParser);
    }

    public <K, V> void writeMap(Map<K, V> map, BiConsumer<PacketByteBuf, K> keySerializer, BiConsumer<PacketByteBuf, V> valueSerializer) {
        this.writeVarInt(map.size());
        map.forEach((key, value) -> {
            keySerializer.accept(this, key);
            valueSerializer.accept(this, value);
        });
    }

    public void forEachInCollection(Consumer<PacketByteBuf> consumer) {
        int i = this.readVarInt();
        for (int j = 0; j < i; ++j) {
            consumer.accept(this);
        }
    }

    public <T> void writeOptional(Optional<T> value, BiConsumer<PacketByteBuf, T> serializer) {
        if (value.isPresent()) {
            this.writeBoolean(true);
            serializer.accept(this, (PacketByteBuf)((Object)value.get()));
        } else {
            this.writeBoolean(false);
        }
    }

    public <T> Optional<T> readOptional(Function<PacketByteBuf, T> parser) {
        if (this.readBoolean()) {
            return Optional.of(parser.apply(this));
        }
        return Optional.empty();
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public PacketByteBuf writeByteArray(byte[] array) {
        this.writeVarInt(array.length);
        this.writeBytes(array);
        return this;
    }

    public byte[] readByteArray(int maxSize) {
        int i = this.readVarInt();
        if (i > maxSize) {
            throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
        }
        byte[] bs = new byte[i];
        this.readBytes(bs);
        return bs;
    }

    public PacketByteBuf writeIntArray(int[] array) {
        this.writeVarInt(array.length);
        for (int i : array) {
            this.writeVarInt(i);
        }
        return this;
    }

    public int[] readIntArray() {
        return this.readIntArray(this.readableBytes());
    }

    public int[] readIntArray(int maxSize) {
        int i = this.readVarInt();
        if (i > maxSize) {
            throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
        }
        int[] is = new int[i];
        for (int j = 0; j < is.length; ++j) {
            is[j] = this.readVarInt();
        }
        return is;
    }

    public PacketByteBuf writeLongArray(long[] array) {
        this.writeVarInt(array.length);
        for (long l : array) {
            this.writeLong(l);
        }
        return this;
    }

    public long[] readLongArray() {
        return this.readLongArray(null);
    }

    public long[] readLongArray(@Nullable long[] toArray) {
        return this.readLongArray(toArray, this.readableBytes() / 8);
    }

    public long[] readLongArray(@Nullable long[] toArray, int maxSize) {
        int i = this.readVarInt();
        if (toArray == null || toArray.length != i) {
            if (i > maxSize) {
                throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + maxSize);
            }
            toArray = new long[i];
        }
        for (int j = 0; j < toArray.length; ++j) {
            toArray[j] = this.readLong();
        }
        return toArray;
    }

    @VisibleForTesting
    public byte[] getWrittenBytes() {
        int i = this.writerIndex();
        byte[] bs = new byte[i];
        this.getBytes(0, bs);
        return bs;
    }

    public BlockPos readBlockPos() {
        return BlockPos.fromLong(this.readLong());
    }

    public PacketByteBuf writeBlockPos(BlockPos pos) {
        this.writeLong(pos.asLong());
        return this;
    }

    public ChunkPos readChunkPos() {
        return new ChunkPos(this.readLong());
    }

    public PacketByteBuf writeChunkPos(ChunkPos pos) {
        this.writeLong(pos.toLong());
        return this;
    }

    public ChunkSectionPos readChunkSectionPos() {
        return ChunkSectionPos.from(this.readLong());
    }

    public PacketByteBuf writeChunkSectionPos(ChunkSectionPos pos) {
        this.writeLong(pos.asLong());
        return this;
    }

    public Text readText() {
        return Text.Serializer.fromJson(this.readString(262144));
    }

    public PacketByteBuf writeText(Text text) {
        return this.writeString(Text.Serializer.toJson(text), 262144);
    }

    public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
        return (T)((Enum[])enumClass.getEnumConstants())[this.readVarInt()];
    }

    public PacketByteBuf writeEnumConstant(Enum<?> instance) {
        return this.writeVarInt(instance.ordinal());
    }

    public int readVarInt() {
        byte b;
        int i = 0;
        int j = 0;
        do {
            b = this.readByte();
            i |= (b & 0x7F) << j++ * 7;
            if (j <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((b & 0x80) == 128);
        return i;
    }

    public long readVarLong() {
        byte b;
        long l = 0L;
        int i = 0;
        do {
            b = this.readByte();
            l |= (long)(b & 0x7F) << i++ * 7;
            if (i <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((b & 0x80) == 128);
        return l;
    }

    public PacketByteBuf writeUuid(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUuid() {
        return new UUID(this.readLong(), this.readLong());
    }

    public PacketByteBuf writeVarInt(int value) {
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                this.writeByte(value);
                return this;
            }
            this.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    public PacketByteBuf writeVarLong(long value) {
        while (true) {
            if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
                this.writeByte((int)value);
                return this;
            }
            this.writeByte((int)(value & 0x7FL) | 0x80);
            value >>>= 7;
        }
    }

    public PacketByteBuf writeNbt(@Nullable NbtCompound compound) {
        if (compound == null) {
            this.writeByte(0);
        } else {
            try {
                NbtIo.write(compound, (DataOutput)new ByteBufOutputStream((ByteBuf)this));
            }
            catch (IOException iOException) {
                throw new EncoderException((Throwable)iOException);
            }
        }
        return this;
    }

    @Nullable
    public NbtCompound readNbt() {
        return this.readNbt(new NbtTagSizeTracker(0x200000L));
    }

    @Nullable
    public NbtCompound readUnlimitedNbt() {
        return this.readNbt(NbtTagSizeTracker.EMPTY);
    }

    @Nullable
    public NbtCompound readNbt(NbtTagSizeTracker sizeTracker) {
        int i = this.readerIndex();
        byte b = this.readByte();
        if (b == 0) {
            return null;
        }
        this.readerIndex(i);
        try {
            return NbtIo.read((DataInput)new ByteBufInputStream((ByteBuf)this), sizeTracker);
        }
        catch (IOException iOException) {
            throw new EncoderException((Throwable)iOException);
        }
    }

    public PacketByteBuf writeItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item item = stack.getItem();
            this.writeVarInt(Item.getRawId(item));
            this.writeByte(stack.getCount());
            NbtCompound nbtCompound = null;
            if (item.isDamageable() || item.isNbtSynced()) {
                nbtCompound = stack.getNbt();
            }
            this.writeNbt(nbtCompound);
        }
        return this;
    }

    public ItemStack readItemStack() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        }
        int i = this.readVarInt();
        byte j = this.readByte();
        ItemStack itemStack = new ItemStack(Item.byRawId(i), j);
        itemStack.setNbt(this.readNbt());
        return itemStack;
    }

    public String readString() {
        return this.readString(Short.MAX_VALUE);
    }

    public String readString(int maxLength) {
        int i = this.readVarInt();
        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        }
        if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        String string = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + i);
        if (string.length() > maxLength) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
        }
        return string;
    }

    public PacketByteBuf writeString(String string) {
        return this.writeString(string, Short.MAX_VALUE);
    }

    public PacketByteBuf writeString(String string, int maxLength) {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        if (bs.length > maxLength) {
            throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + maxLength + ")");
        }
        this.writeVarInt(bs.length);
        this.writeBytes(bs);
        return this;
    }

    public Identifier readIdentifier() {
        return new Identifier(this.readString(Short.MAX_VALUE));
    }

    public PacketByteBuf writeIdentifier(Identifier id) {
        this.writeString(id.toString());
        return this;
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public PacketByteBuf writeDate(Date date) {
        this.writeLong(date.getTime());
        return this;
    }

    public BlockHitResult readBlockHitResult() {
        BlockPos blockPos = this.readBlockPos();
        Direction direction = this.readEnumConstant(Direction.class);
        float f = this.readFloat();
        float g = this.readFloat();
        float h = this.readFloat();
        boolean bl = this.readBoolean();
        return new BlockHitResult(new Vec3d((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)g, (double)blockPos.getZ() + (double)h), direction, blockPos, bl);
    }

    public void writeBlockHitResult(BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        this.writeBlockPos(blockPos);
        this.writeEnumConstant(hitResult.getSide());
        Vec3d vec3d = hitResult.getPos();
        this.writeFloat((float)(vec3d.x - (double)blockPos.getX()));
        this.writeFloat((float)(vec3d.y - (double)blockPos.getY()));
        this.writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
        this.writeBoolean(hitResult.isInsideBlock());
    }

    public BitSet readBitSet() {
        return BitSet.valueOf(this.readLongArray());
    }

    public void writeBitSet(BitSet bitSet) {
        this.writeLongArray(bitSet.toLongArray());
    }

    public int capacity() {
        return this.parent.capacity();
    }

    public ByteBuf capacity(int capacity) {
        return this.parent.capacity(capacity);
    }

    public int maxCapacity() {
        return this.parent.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.parent.alloc();
    }

    public ByteOrder order() {
        return this.parent.order();
    }

    public ByteBuf order(ByteOrder byteOrder) {
        return this.parent.order(byteOrder);
    }

    public ByteBuf unwrap() {
        return this.parent.unwrap();
    }

    public boolean isDirect() {
        return this.parent.isDirect();
    }

    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.parent.asReadOnly();
    }

    public int readerIndex() {
        return this.parent.readerIndex();
    }

    public ByteBuf readerIndex(int index) {
        return this.parent.readerIndex(index);
    }

    public int writerIndex() {
        return this.parent.writerIndex();
    }

    public ByteBuf writerIndex(int index) {
        return this.parent.writerIndex(index);
    }

    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return this.parent.setIndex(readerIndex, writerIndex);
    }

    public int readableBytes() {
        return this.parent.readableBytes();
    }

    public int writableBytes() {
        return this.parent.writableBytes();
    }

    public int maxWritableBytes() {
        return this.parent.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.parent.isReadable();
    }

    public boolean isReadable(int size) {
        return this.parent.isReadable(size);
    }

    public boolean isWritable() {
        return this.parent.isWritable();
    }

    public boolean isWritable(int size) {
        return this.parent.isWritable(size);
    }

    public ByteBuf clear() {
        return this.parent.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.parent.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.parent.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.parent.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.parent.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.parent.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.parent.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int minBytes) {
        return this.parent.ensureWritable(minBytes);
    }

    public int ensureWritable(int minBytes, boolean force) {
        return this.parent.ensureWritable(minBytes, force);
    }

    public boolean getBoolean(int index) {
        return this.parent.getBoolean(index);
    }

    public byte getByte(int index) {
        return this.parent.getByte(index);
    }

    public short getUnsignedByte(int index) {
        return this.parent.getUnsignedByte(index);
    }

    public short getShort(int index) {
        return this.parent.getShort(index);
    }

    public short getShortLE(int index) {
        return this.parent.getShortLE(index);
    }

    public int getUnsignedShort(int index) {
        return this.parent.getUnsignedShort(index);
    }

    public int getUnsignedShortLE(int index) {
        return this.parent.getUnsignedShortLE(index);
    }

    public int getMedium(int index) {
        return this.parent.getMedium(index);
    }

    public int getMediumLE(int index) {
        return this.parent.getMediumLE(index);
    }

    public int getUnsignedMedium(int index) {
        return this.parent.getUnsignedMedium(index);
    }

    public int getUnsignedMediumLE(int index) {
        return this.parent.getUnsignedMediumLE(index);
    }

    public int getInt(int index) {
        return this.parent.getInt(index);
    }

    public int getIntLE(int index) {
        return this.parent.getIntLE(index);
    }

    public long getUnsignedInt(int index) {
        return this.parent.getUnsignedInt(index);
    }

    public long getUnsignedIntLE(int index) {
        return this.parent.getUnsignedIntLE(index);
    }

    public long getLong(int index) {
        return this.parent.getLong(index);
    }

    public long getLongLE(int index) {
        return this.parent.getLongLE(index);
    }

    public char getChar(int index) {
        return this.parent.getChar(index);
    }

    public float getFloat(int index) {
        return this.parent.getFloat(index);
    }

    public double getDouble(int index) {
        return this.parent.getDouble(index);
    }

    public ByteBuf getBytes(int index, ByteBuf buf) {
        return this.parent.getBytes(index, buf);
    }

    public ByteBuf getBytes(int index, ByteBuf buf, int length) {
        return this.parent.getBytes(index, buf, length);
    }

    public ByteBuf getBytes(int index, ByteBuf buf, int outputIndex, int length) {
        return this.parent.getBytes(index, buf, outputIndex, length);
    }

    public ByteBuf getBytes(int index, byte[] bytes) {
        return this.parent.getBytes(index, bytes);
    }

    public ByteBuf getBytes(int index, byte[] bytes, int outputIndex, int length) {
        return this.parent.getBytes(index, bytes, outputIndex, length);
    }

    public ByteBuf getBytes(int index, ByteBuffer buf) {
        return this.parent.getBytes(index, buf);
    }

    public ByteBuf getBytes(int index, OutputStream stream, int length) throws IOException {
        return this.parent.getBytes(index, stream, length);
    }

    public int getBytes(int index, GatheringByteChannel channel, int length) throws IOException {
        return this.parent.getBytes(index, channel, length);
    }

    public int getBytes(int index, FileChannel channel, long pos, int length) throws IOException {
        return this.parent.getBytes(index, channel, pos, length);
    }

    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.parent.getCharSequence(index, length, charset);
    }

    public ByteBuf setBoolean(int index, boolean value) {
        return this.parent.setBoolean(index, value);
    }

    public ByteBuf setByte(int index, int value) {
        return this.parent.setByte(index, value);
    }

    public ByteBuf setShort(int index, int value) {
        return this.parent.setShort(index, value);
    }

    public ByteBuf setShortLE(int index, int value) {
        return this.parent.setShortLE(index, value);
    }

    public ByteBuf setMedium(int index, int value) {
        return this.parent.setMedium(index, value);
    }

    public ByteBuf setMediumLE(int index, int value) {
        return this.parent.setMediumLE(index, value);
    }

    public ByteBuf setInt(int index, int value) {
        return this.parent.setInt(index, value);
    }

    public ByteBuf setIntLE(int index, int value) {
        return this.parent.setIntLE(index, value);
    }

    public ByteBuf setLong(int index, long value) {
        return this.parent.setLong(index, value);
    }

    public ByteBuf setLongLE(int index, long value) {
        return this.parent.setLongLE(index, value);
    }

    public ByteBuf setChar(int index, int value) {
        return this.parent.setChar(index, value);
    }

    public ByteBuf setFloat(int index, float value) {
        return this.parent.setFloat(index, value);
    }

    public ByteBuf setDouble(int index, double value) {
        return this.parent.setDouble(index, value);
    }

    public ByteBuf setBytes(int index, ByteBuf buf) {
        return this.parent.setBytes(index, buf);
    }

    public ByteBuf setBytes(int index, ByteBuf buf, int length) {
        return this.parent.setBytes(index, buf, length);
    }

    public ByteBuf setBytes(int index, ByteBuf buf, int sourceIndex, int length) {
        return this.parent.setBytes(index, buf, sourceIndex, length);
    }

    public ByteBuf setBytes(int index, byte[] bytes) {
        return this.parent.setBytes(index, bytes);
    }

    public ByteBuf setBytes(int index, byte[] bytes, int sourceIndex, int length) {
        return this.parent.setBytes(index, bytes, sourceIndex, length);
    }

    public ByteBuf setBytes(int index, ByteBuffer buf) {
        return this.parent.setBytes(index, buf);
    }

    public int setBytes(int index, InputStream stream, int length) throws IOException {
        return this.parent.setBytes(index, stream, length);
    }

    public int setBytes(int index, ScatteringByteChannel channel, int length) throws IOException {
        return this.parent.setBytes(index, channel, length);
    }

    public int setBytes(int index, FileChannel channel, long pos, int length) throws IOException {
        return this.parent.setBytes(index, channel, pos, length);
    }

    public ByteBuf setZero(int index, int length) {
        return this.parent.setZero(index, length);
    }

    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.parent.setCharSequence(index, sequence, charset);
    }

    public boolean readBoolean() {
        return this.parent.readBoolean();
    }

    public byte readByte() {
        return this.parent.readByte();
    }

    public short readUnsignedByte() {
        return this.parent.readUnsignedByte();
    }

    public short readShort() {
        return this.parent.readShort();
    }

    public short readShortLE() {
        return this.parent.readShortLE();
    }

    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.parent.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.parent.readMedium();
    }

    public int readMediumLE() {
        return this.parent.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.parent.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.parent.readInt();
    }

    public int readIntLE() {
        return this.parent.readIntLE();
    }

    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.parent.readUnsignedIntLE();
    }

    public long readLong() {
        return this.parent.readLong();
    }

    public long readLongLE() {
        return this.parent.readLongLE();
    }

    public char readChar() {
        return this.parent.readChar();
    }

    public float readFloat() {
        return this.parent.readFloat();
    }

    public double readDouble() {
        return this.parent.readDouble();
    }

    public ByteBuf readBytes(int length) {
        return this.parent.readBytes(length);
    }

    public ByteBuf readSlice(int length) {
        return this.parent.readSlice(length);
    }

    public ByteBuf readRetainedSlice(int length) {
        return this.parent.readRetainedSlice(length);
    }

    public ByteBuf readBytes(ByteBuf buf) {
        return this.parent.readBytes(buf);
    }

    public ByteBuf readBytes(ByteBuf buf, int length) {
        return this.parent.readBytes(buf, length);
    }

    public ByteBuf readBytes(ByteBuf buf, int outputIndex, int length) {
        return this.parent.readBytes(buf, outputIndex, length);
    }

    public ByteBuf readBytes(byte[] bytes) {
        return this.parent.readBytes(bytes);
    }

    public ByteBuf readBytes(byte[] bytes, int outputIndex, int length) {
        return this.parent.readBytes(bytes, outputIndex, length);
    }

    public ByteBuf readBytes(ByteBuffer buf) {
        return this.parent.readBytes(buf);
    }

    public ByteBuf readBytes(OutputStream stream, int length) throws IOException {
        return this.parent.readBytes(stream, length);
    }

    public int readBytes(GatheringByteChannel channel, int length) throws IOException {
        return this.parent.readBytes(channel, length);
    }

    public CharSequence readCharSequence(int length, Charset charset) {
        return this.parent.readCharSequence(length, charset);
    }

    public int readBytes(FileChannel channel, long pos, int length) throws IOException {
        return this.parent.readBytes(channel, pos, length);
    }

    public ByteBuf skipBytes(int length) {
        return this.parent.skipBytes(length);
    }

    public ByteBuf writeBoolean(boolean value) {
        return this.parent.writeBoolean(value);
    }

    public ByteBuf writeByte(int value) {
        return this.parent.writeByte(value);
    }

    public ByteBuf writeShort(int value) {
        return this.parent.writeShort(value);
    }

    public ByteBuf writeShortLE(int value) {
        return this.parent.writeShortLE(value);
    }

    public ByteBuf writeMedium(int value) {
        return this.parent.writeMedium(value);
    }

    public ByteBuf writeMediumLE(int value) {
        return this.parent.writeMediumLE(value);
    }

    public ByteBuf writeInt(int value) {
        return this.parent.writeInt(value);
    }

    public ByteBuf writeIntLE(int value) {
        return this.parent.writeIntLE(value);
    }

    public ByteBuf writeLong(long value) {
        return this.parent.writeLong(value);
    }

    public ByteBuf writeLongLE(long value) {
        return this.parent.writeLongLE(value);
    }

    public ByteBuf writeChar(int value) {
        return this.parent.writeChar(value);
    }

    public ByteBuf writeFloat(float value) {
        return this.parent.writeFloat(value);
    }

    public ByteBuf writeDouble(double value) {
        return this.parent.writeDouble(value);
    }

    public ByteBuf writeBytes(ByteBuf buf) {
        return this.parent.writeBytes(buf);
    }

    public ByteBuf writeBytes(ByteBuf buf, int length) {
        return this.parent.writeBytes(buf, length);
    }

    public ByteBuf writeBytes(ByteBuf buf, int sourceIndex, int length) {
        return this.parent.writeBytes(buf, sourceIndex, length);
    }

    public ByteBuf writeBytes(byte[] bytes) {
        return this.parent.writeBytes(bytes);
    }

    public ByteBuf writeBytes(byte[] bytes, int sourceIndex, int length) {
        return this.parent.writeBytes(bytes, sourceIndex, length);
    }

    public ByteBuf writeBytes(ByteBuffer buf) {
        return this.parent.writeBytes(buf);
    }

    public int writeBytes(InputStream stream, int length) throws IOException {
        return this.parent.writeBytes(stream, length);
    }

    public int writeBytes(ScatteringByteChannel channel, int length) throws IOException {
        return this.parent.writeBytes(channel, length);
    }

    public int writeBytes(FileChannel channel, long pos, int length) throws IOException {
        return this.parent.writeBytes(channel, pos, length);
    }

    public ByteBuf writeZero(int length) {
        return this.parent.writeZero(length);
    }

    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.parent.writeCharSequence(sequence, charset);
    }

    public int indexOf(int from, int to, byte value) {
        return this.parent.indexOf(from, to, value);
    }

    public int bytesBefore(byte value) {
        return this.parent.bytesBefore(value);
    }

    public int bytesBefore(int length, byte value) {
        return this.parent.bytesBefore(length, value);
    }

    public int bytesBefore(int index, int length, byte value) {
        return this.parent.bytesBefore(index, length, value);
    }

    public int forEachByte(ByteProcessor byteProcessor) {
        return this.parent.forEachByte(byteProcessor);
    }

    public int forEachByte(int index, int length, ByteProcessor byteProcessor) {
        return this.parent.forEachByte(index, length, byteProcessor);
    }

    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(byteProcessor);
    }

    public int forEachByteDesc(int index, int length, ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(index, length, byteProcessor);
    }

    public ByteBuf copy() {
        return this.parent.copy();
    }

    public ByteBuf copy(int index, int length) {
        return this.parent.copy(index, length);
    }

    public ByteBuf slice() {
        return this.parent.slice();
    }

    public ByteBuf retainedSlice() {
        return this.parent.retainedSlice();
    }

    public ByteBuf slice(int index, int length) {
        return this.parent.slice(index, length);
    }

    public ByteBuf retainedSlice(int index, int length) {
        return this.parent.retainedSlice(index, length);
    }

    public ByteBuf duplicate() {
        return this.parent.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.parent.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.parent.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.parent.nioBuffer();
    }

    public ByteBuffer nioBuffer(int index, int length) {
        return this.parent.nioBuffer(index, length);
    }

    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.parent.internalNioBuffer(index, length);
    }

    public ByteBuffer[] nioBuffers() {
        return this.parent.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.parent.nioBuffers(index, length);
    }

    public boolean hasArray() {
        return this.parent.hasArray();
    }

    public byte[] array() {
        return this.parent.array();
    }

    public int arrayOffset() {
        return this.parent.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.parent.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.parent.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.parent.toString(charset);
    }

    public String toString(int index, int length, Charset charset) {
        return this.parent.toString(index, length, charset);
    }

    public int hashCode() {
        return this.parent.hashCode();
    }

    public boolean equals(Object o) {
        return this.parent.equals(o);
    }

    public int compareTo(ByteBuf byteBuf) {
        return this.parent.compareTo(byteBuf);
    }

    public String toString() {
        return this.parent.toString();
    }

    public ByteBuf retain(int i) {
        return this.parent.retain(i);
    }

    public ByteBuf retain() {
        return this.parent.retain();
    }

    public ByteBuf touch() {
        return this.parent.touch();
    }

    public ByteBuf touch(Object object) {
        return this.parent.touch(object);
    }

    public int refCnt() {
        return this.parent.refCnt();
    }

    public boolean release() {
        return this.parent.release();
    }

    public boolean release(int decrement) {
        return this.parent.release(decrement);
    }
}

