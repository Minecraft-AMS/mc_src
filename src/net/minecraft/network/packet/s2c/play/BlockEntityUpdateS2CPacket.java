/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;
    private int blockEntityType;
    private NbtCompound nbt;

    public BlockEntityUpdateS2CPacket() {
    }

    public BlockEntityUpdateS2CPacket(BlockPos pos, int blockEntityType, NbtCompound nbt) {
        this.pos = pos;
        this.blockEntityType = blockEntityType;
        this.nbt = nbt;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.blockEntityType = buf.readUnsignedByte();
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte((byte)this.blockEntityType);
        buf.writeNbt(this.nbt);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBlockEntityUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getBlockEntityType() {
        return this.blockEntityType;
    }

    @Environment(value=EnvType.CLIENT)
    public NbtCompound getNbt() {
        return this.nbt;
    }
}

