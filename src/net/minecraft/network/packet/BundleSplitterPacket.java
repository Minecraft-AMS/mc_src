/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class BundleSplitterPacket<T extends PacketListener>
implements Packet<T> {
    @Override
    public final void write(PacketByteBuf buf) {
    }

    @Override
    public final void apply(T listener) {
        throw new AssertionError((Object)"This packet should be handled by pipeline");
    }
}

