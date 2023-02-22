/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.Nullable;

public class NbtQueryResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int transactionId;
    @Nullable
    private NbtCompound nbt;

    public NbtQueryResponseS2CPacket() {
    }

    public NbtQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound nbt) {
        this.transactionId = transactionId;
        this.nbt = nbt;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeNbt(this.nbt);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onTagQuery(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public NbtCompound getNbt() {
        return this.nbt;
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }
}

