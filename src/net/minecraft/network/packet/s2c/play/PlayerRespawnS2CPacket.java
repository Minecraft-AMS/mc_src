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
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class PlayerRespawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private DimensionType dimension;
    private GameMode gameMode;
    private LevelGeneratorType generatorType;

    public PlayerRespawnS2CPacket() {
    }

    public PlayerRespawnS2CPacket(DimensionType dimension, LevelGeneratorType generatorType, GameMode gameMode) {
        this.dimension = dimension;
        this.gameMode = gameMode;
        this.generatorType = generatorType;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerRespawn(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.dimension = DimensionType.byRawId(buf.readInt());
        this.gameMode = GameMode.byId(buf.readUnsignedByte());
        this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.dimension.getRawId());
        buf.writeByte(this.gameMode.getId());
        buf.writeString(this.generatorType.getName());
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
}

