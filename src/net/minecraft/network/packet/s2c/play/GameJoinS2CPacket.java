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

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long seed;
    private boolean hardcore;
    private GameMode gameMode;
    private DimensionType dimension;
    private int maxPlayers;
    private LevelGeneratorType generatorType;
    private int chunkLoadDistance;
    private boolean reducedDebugInfo;
    private boolean showsDeathScreen;

    public GameJoinS2CPacket() {
    }

    public GameJoinS2CPacket(int playerEntityId, GameMode gameMode, long seed, boolean hardcore, DimensionType dimensionType, int maxPlayers, LevelGeneratorType levelGeneratorType, int chunkLoadDistance, boolean reducedDebugInfo, boolean showsDeathScreen) {
        this.playerEntityId = playerEntityId;
        this.dimension = dimensionType;
        this.seed = seed;
        this.gameMode = gameMode;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.generatorType = levelGeneratorType;
        this.chunkLoadDistance = chunkLoadDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.showsDeathScreen = showsDeathScreen;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.playerEntityId = buf.readInt();
        int i = buf.readUnsignedByte();
        this.hardcore = (i & 8) == 8;
        this.gameMode = GameMode.byId(i &= 0xFFFFFFF7);
        this.dimension = DimensionType.byRawId(buf.readInt());
        this.seed = buf.readLong();
        this.maxPlayers = buf.readUnsignedByte();
        this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
        this.chunkLoadDistance = buf.readVarInt();
        this.reducedDebugInfo = buf.readBoolean();
        this.showsDeathScreen = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.playerEntityId);
        int i = this.gameMode.getId();
        if (this.hardcore) {
            i |= 8;
        }
        buf.writeByte(i);
        buf.writeInt(this.dimension.getRawId());
        buf.writeLong(this.seed);
        buf.writeByte(this.maxPlayers);
        buf.writeString(this.generatorType.getName());
        buf.writeVarInt(this.chunkLoadDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showsDeathScreen);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.playerEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSeed() {
        return this.seed;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkLoadDistance() {
        return this.chunkLoadDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean showsDeathScreen() {
        return this.showsDeathScreen;
    }
}

