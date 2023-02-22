/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

public class ArrayPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final Function<CompoundTag, T> valueDeserializer;
    private final int indexBits;
    private int size;

    public ArrayPalette(IdList<T> idList, int integer, PaletteResizeListener<T> resizeListener, Function<CompoundTag, T> valueDeserializer) {
        this.idList = idList;
        this.array = new Object[1 << integer];
        this.indexBits = integer;
        this.resizeListener = resizeListener;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public int getIndex(T object) {
        int i;
        for (i = 0; i < this.size; ++i) {
            if (this.array[i] != object) continue;
            return i;
        }
        if ((i = this.size++) < this.array.length) {
            this.array[i] = object;
            return i;
        }
        return this.resizeListener.onResize(this.indexBits + 1, object);
    }

    @Override
    public boolean accepts(T object) {
        return ArrayUtils.contains((Object[])this.array, object);
    }

    @Override
    @Nullable
    public T getByIndex(int index) {
        if (index >= 0 && index < this.size) {
            return this.array[index];
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf buf) {
        this.size = buf.readVarInt();
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = this.idList.get(buf.readVarInt());
        }
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        buf.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            buf.writeVarInt(this.idList.getId(this.array[i]));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getSize());
        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.array[j]));
        }
        return i;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void fromTag(ListTag tag) {
        for (int i = 0; i < tag.size(); ++i) {
            this.array[i] = this.valueDeserializer.apply(tag.getCompound(i));
        }
        this.size = tag.size();
    }
}

