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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClientCommandC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private Mode mode;
    private int mountJumpHeight;

    public ClientCommandC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ClientCommandC2SPacket(Entity entity, Mode mode) {
        this(entity, mode, 0);
    }

    @Environment(value=EnvType.CLIENT)
    public ClientCommandC2SPacket(Entity entity, Mode mode, int i) {
        this.entityId = entity.getEntityId();
        this.mode = mode;
        this.mountJumpHeight = i;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.mode = buf.readEnumConstant(Mode.class);
        this.mountJumpHeight = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeEnumConstant(this.mode);
        buf.writeVarInt(this.mountJumpHeight);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onClientCommand(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public int getMountJumpHeight() {
        return this.mountJumpHeight;
    }

    public static enum Mode {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING;

    }
}
