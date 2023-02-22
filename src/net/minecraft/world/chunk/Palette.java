/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public interface Palette<T> {
    public int getIndex(T var1);

    public boolean accepts(T var1);

    @Nullable
    public T getByIndex(int var1);

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf var1);

    public void toPacket(PacketByteBuf var1);

    public int getPacketSize();

    public void fromTag(ListTag var1);
}

