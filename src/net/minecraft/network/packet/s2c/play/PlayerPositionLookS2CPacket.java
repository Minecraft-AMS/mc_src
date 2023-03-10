/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerPositionLookS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final Set<Flag> flags;
    private final int teleportId;
    private final boolean shouldDismount;

    public PlayerPositionLookS2CPacket(double x, double y, double z, float yaw, float pitch, Set<Flag> flags, int teleportId, boolean shouldDismount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
        this.teleportId = teleportId;
        this.shouldDismount = shouldDismount;
    }

    public PlayerPositionLookS2CPacket(PacketByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = Flag.getFlags(buf.readUnsignedByte());
        this.teleportId = buf.readVarInt();
        this.shouldDismount = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(Flag.getBitfield(this.flags));
        buf.writeVarInt(this.teleportId);
        buf.writeBoolean(this.shouldDismount);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerPositionLook(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public int getTeleportId() {
        return this.teleportId;
    }

    public boolean shouldDismount() {
        return this.shouldDismount;
    }

    public Set<Flag> getFlags() {
        return this.flags;
    }

    public static final class Flag
    extends Enum<Flag> {
        public static final /* enum */ Flag X = new Flag(0);
        public static final /* enum */ Flag Y = new Flag(1);
        public static final /* enum */ Flag Z = new Flag(2);
        public static final /* enum */ Flag Y_ROT = new Flag(3);
        public static final /* enum */ Flag X_ROT = new Flag(4);
        private final int shift;
        private static final /* synthetic */ Flag[] field_12402;

        public static Flag[] values() {
            return (Flag[])field_12402.clone();
        }

        public static Flag valueOf(String string) {
            return Enum.valueOf(Flag.class, string);
        }

        private Flag(int shift) {
            this.shift = shift;
        }

        private int getMask() {
            return 1 << this.shift;
        }

        private boolean isSet(int mask) {
            return (mask & this.getMask()) == this.getMask();
        }

        public static Set<Flag> getFlags(int mask) {
            EnumSet<Flag> set = EnumSet.noneOf(Flag.class);
            for (Flag flag : Flag.values()) {
                if (!flag.isSet(mask)) continue;
                set.add(flag);
            }
            return set;
        }

        public static int getBitfield(Set<Flag> flags) {
            int i = 0;
            for (Flag flag : flags) {
                i |= flag.getMask();
            }
            return i;
        }

        private static /* synthetic */ Flag[] method_36952() {
            return new Flag[]{X, Y, Z, Y_ROT, X_ROT};
        }

        static {
            field_12402 = Flag.method_36952();
        }
    }
}

