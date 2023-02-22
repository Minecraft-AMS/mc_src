/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.event;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class BlockPositionSource
implements PositionSource {
    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(blockPositionSource -> blockPositionSource.pos)).apply((Applicative)instance, BlockPositionSource::new));
    final BlockPos pos;

    public BlockPositionSource(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Optional<Vec3d> getPos(World world) {
        return Optional.of(Vec3d.ofCenter(this.pos));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type
    implements PositionSourceType<BlockPositionSource> {
        @Override
        public BlockPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new BlockPositionSource(packetByteBuf.readBlockPos());
        }

        @Override
        public void writeToBuf(PacketByteBuf packetByteBuf, BlockPositionSource blockPositionSource) {
            packetByteBuf.writeBlockPos(blockPositionSource.pos);
        }

        @Override
        public Codec<BlockPositionSource> getCodec() {
            return CODEC;
        }

        @Override
        public /* synthetic */ PositionSource readFromBuf(PacketByteBuf buf) {
            return this.readFromBuf(buf);
        }
    }
}

