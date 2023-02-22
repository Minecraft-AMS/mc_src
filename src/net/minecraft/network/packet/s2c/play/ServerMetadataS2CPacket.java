/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ServerMetadataS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Optional<Text> description;
    private final Optional<String> favicon;
    private final boolean secureChatEnforced;

    public ServerMetadataS2CPacket(@Nullable Text description, @Nullable String favicon, boolean previewsChat) {
        this.description = Optional.ofNullable(description);
        this.favicon = Optional.ofNullable(favicon);
        this.secureChatEnforced = previewsChat;
    }

    public ServerMetadataS2CPacket(PacketByteBuf buf) {
        this.description = buf.readOptional(PacketByteBuf::readText);
        this.favicon = buf.readOptional(PacketByteBuf::readString);
        this.secureChatEnforced = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeOptional(this.description, PacketByteBuf::writeText);
        buf.writeOptional(this.favicon, PacketByteBuf::writeString);
        buf.writeBoolean(this.secureChatEnforced);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onServerMetadata(this);
    }

    public Optional<Text> getDescription() {
        return this.description;
    }

    public Optional<String> getFavicon() {
        return this.favicon;
    }

    public boolean isSecureChatEnforced() {
        return this.secureChatEnforced;
    }
}

