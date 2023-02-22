/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDeltaUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private ChunkSectionPos sectionPos;
    private short[] positions;
    private BlockState[] blockStates;
    private boolean field_26749;

    public ChunkDeltaUpdateS2CPacket() {
    }

    public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section, boolean noLightingUpdates) {
        this.sectionPos = sectionPos;
        this.field_26749 = noLightingUpdates;
        this.allocateBuffers(positions.size());
        int i = 0;
        ShortIterator shortIterator = positions.iterator();
        while (shortIterator.hasNext()) {
            short s;
            this.positions[i] = s = ((Short)shortIterator.next()).shortValue();
            this.blockStates[i] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
            ++i;
        }
    }

    private void allocateBuffers(int positionCount) {
        this.positions = new short[positionCount];
        this.blockStates = new BlockState[positionCount];
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.sectionPos = ChunkSectionPos.from(buf.readLong());
        this.field_26749 = buf.readBoolean();
        int i = buf.readVarInt();
        this.allocateBuffers(i);
        for (int j = 0; j < this.positions.length; ++j) {
            long l = buf.readVarLong();
            this.positions[j] = (short)(l & 0xFFFL);
            this.blockStates[j] = Block.STATE_IDS.get((int)(l >>> 12));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeBoolean(this.field_26749);
        buf.writeVarInt(this.positions.length);
        for (int i = 0; i < this.positions.length; ++i) {
            buf.writeVarLong(Block.getRawIdFromState(this.blockStates[i]) << 12 | this.positions[i]);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChunkDeltaUpdate(this);
    }

    public void visitUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < this.positions.length; ++i) {
            short s = this.positions[i];
            mutable.set(this.sectionPos.unpackBlockX(s), this.sectionPos.unpackBlockY(s), this.sectionPos.unpackBlockZ(s));
            biConsumer.accept(mutable, this.blockStates[i]);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_31179() {
        return this.field_26749;
    }
}

