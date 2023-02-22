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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    protected int id;
    protected short deltaX;
    protected short deltaY;
    protected short deltaZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotate;

    public static long encodePacketCoordinate(double coord) {
        return MathHelper.lfloor(coord * 4096.0);
    }

    public static Vec3d decodePacketCoordinates(long x, long y, long z) {
        return new Vec3d(x, y, z).multiply(2.44140625E-4);
    }

    public EntityS2CPacket() {
    }

    public EntityS2CPacket(int entityId) {
        this.id = entityId;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        listener.onEntityUpdate(this);
    }

    public String toString() {
        return "Entity_" + super.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.id);
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaXShort() {
        return this.deltaX;
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaYShort() {
        return this.deltaY;
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaZShort() {
        return this.deltaZ;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasRotation() {
        return this.rotate;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Rotate
    extends EntityS2CPacket {
        public Rotate() {
            this.rotate = true;
        }

        public Rotate(int i, byte b, byte c, boolean bl) {
            super(i);
            this.yaw = b;
            this.pitch = c;
            this.rotate = true;
            this.onGround = bl;
        }

        @Override
        public void read(PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        @Override
        public void write(PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class MoveRelative
    extends EntityS2CPacket {
        public MoveRelative() {
        }

        public MoveRelative(int i, short s, short t, short u, boolean bl) {
            super(i);
            this.deltaX = s;
            this.deltaY = t;
            this.deltaZ = u;
            this.onGround = bl;
        }

        @Override
        public void read(PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.deltaX = buf.readShort();
            this.deltaY = buf.readShort();
            this.deltaZ = buf.readShort();
            this.onGround = buf.readBoolean();
        }

        @Override
        public void write(PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeShort(this.deltaX);
            buf.writeShort(this.deltaY);
            buf.writeShort(this.deltaZ);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class RotateAndMoveRelative
    extends EntityS2CPacket {
        public RotateAndMoveRelative() {
            this.rotate = true;
        }

        public RotateAndMoveRelative(int i, short s, short t, short u, byte b, byte c, boolean bl) {
            super(i);
            this.deltaX = s;
            this.deltaY = t;
            this.deltaZ = u;
            this.yaw = b;
            this.pitch = c;
            this.onGround = bl;
            this.rotate = true;
        }

        @Override
        public void read(PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.deltaX = buf.readShort();
            this.deltaY = buf.readShort();
            this.deltaZ = buf.readShort();
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        @Override
        public void write(PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeShort(this.deltaX);
            buf.writeShort(this.deltaY);
            buf.writeShort(this.deltaZ);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }
}
