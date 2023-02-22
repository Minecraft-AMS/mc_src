/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ResourcePackStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final Status status;

    public ResourcePackStatusC2SPacket(Status status) {
        this.status = status;
    }

    public ResourcePackStatusC2SPacket(PacketByteBuf buf) {
        this.status = buf.readEnumConstant(Status.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.status);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onResourcePackStatus(this);
    }

    public Status getStatus() {
        return this.status;
    }

    public static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status SUCCESSFULLY_LOADED = new Status();
        public static final /* enum */ Status DECLINED = new Status();
        public static final /* enum */ Status FAILED_DOWNLOAD = new Status();
        public static final /* enum */ Status ACCEPTED = new Status();
        private static final /* synthetic */ Status[] field_13019;

        public static Status[] values() {
            return (Status[])field_13019.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static /* synthetic */ Status[] method_36961() {
            return new Status[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED};
        }

        static {
            field_13019 = Status.method_36961();
        }
    }
}

