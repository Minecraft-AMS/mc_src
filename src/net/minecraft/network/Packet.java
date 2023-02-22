/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.PacketByteBuf;

public interface Packet<T extends PacketListener> {
    public void read(PacketByteBuf var1) throws IOException;

    public void write(PacketByteBuf var1) throws IOException;

    public void apply(T var1);

    default public boolean isWritingErrorSkippable() {
        return false;
    }
}

