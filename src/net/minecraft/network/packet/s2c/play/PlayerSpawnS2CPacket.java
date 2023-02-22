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
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class PlayerSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private DataTracker dataTracker;
    private List<DataTracker.Entry<?>> trackedValues;

    public PlayerSpawnS2CPacket() {
    }

    public PlayerSpawnS2CPacket(PlayerEntity player) {
        this.id = player.getEntityId();
        this.uuid = player.getGameProfile().getId();
        this.x = player.x;
        this.y = player.y;
        this.z = player.z;
        this.yaw = (byte)(player.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(player.pitch * 256.0f / 360.0f);
        this.dataTracker = player.getDataTracker();
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.trackedValues = DataTracker.deserializePacket(buf);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        this.dataTracker.toPacketByteBuf(buf);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerSpawn(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public List<DataTracker.Entry<?>> getTrackedValues() {
        return this.trackedValues;
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getPlayerUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }
}

