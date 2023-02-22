/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class SelectVillagerTradeC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int tradeId;

    public SelectVillagerTradeC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public SelectVillagerTradeC2SPacket(int i) {
        this.tradeId = i;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.tradeId = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.tradeId);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onVillagerTradeSelect(this);
    }

    public int method_12431() {
        return this.tradeId;
    }
}

