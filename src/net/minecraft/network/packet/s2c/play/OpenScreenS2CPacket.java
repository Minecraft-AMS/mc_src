/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class OpenScreenS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int syncId;
    private final ScreenHandlerType<?> screenHandlerId;
    private final Text name;

    public OpenScreenS2CPacket(int syncId, ScreenHandlerType<?> type, Text name) {
        this.syncId = syncId;
        this.screenHandlerId = type;
        this.name = name;
    }

    public OpenScreenS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readVarInt();
        this.screenHandlerId = buf.readRegistryValue(Registry.SCREEN_HANDLER);
        this.name = buf.readText();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.syncId);
        buf.writeRegistryValue(Registry.SCREEN_HANDLER, this.screenHandlerId);
        buf.writeText(this.name);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenScreen(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    @Nullable
    public ScreenHandlerType<?> getScreenHandlerType() {
        return this.screenHandlerId;
    }

    public Text getName() {
        return this.name;
    }
}

