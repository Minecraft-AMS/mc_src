/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
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
    private static final int field_33341 = 12;
    private final ChunkSectionPos sectionPos;
    private final short[] positions;
    private final BlockState[] blockStates;
    private final boolean noLightingUpdates;

    public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section, boolean noLightingUpdates) {
        this.sectionPos = sectionPos;
        this.noLightingUpdates = noLightingUpdates;
        int i = positions.size();
        this.positions = new short[i];
        this.blockStates = new BlockState[i];
        int j = 0;
        ShortIterator shortIterator = positions.iterator();
        while (shortIterator.hasNext()) {
            short s;
            this.positions[j] = s = ((Short)shortIterator.next()).shortValue();
            this.blockStates[j] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
            ++j;
        }
    }

    public ChunkDeltaUpdateS2CPacket(PacketByteBuf buf) {
        this.sectionPos = ChunkSectionPos.from(buf.readLong());
        this.noLightingUpdates = buf.readBoolean();
        int i = buf.readVarInt();
        this.positions = new short[i];
        this.blockStates = new BlockState[i];
        for (int j = 0; j < i; ++j) {
            long l = buf.readVarLong();
            this.positions[j] = (short)(l & 0xFFFL);
            this.blockStates[j] = Block.STATE_IDS.get((int)(l >>> 12));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeBoolean(this.noLightingUpdates);
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

    public boolean shouldSkipLightingUpdates() {
        return this.noLightingUpdates;
    }
}

