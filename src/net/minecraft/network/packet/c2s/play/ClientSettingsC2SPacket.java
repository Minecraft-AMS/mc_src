/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;

public record ClientSettingsC2SPacket(String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColors, int playerModelBitMask, Arm mainArm, boolean filterText, boolean allowsListing) implements Packet<ServerPlayPacketListener>
{
    public static final int MAX_LANGUAGE_LENGTH = 16;

    public ClientSettingsC2SPacket(PacketByteBuf buf) {
        this(buf.readString(16), buf.readByte(), buf.readEnumConstant(ChatVisibility.class), buf.readBoolean(), buf.readUnsignedByte(), buf.readEnumConstant(Arm.class), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.language);
        buf.writeByte(this.viewDistance);
        buf.writeEnumConstant(this.chatVisibility);
        buf.writeBoolean(this.chatColors);
        buf.writeByte(this.playerModelBitMask);
        buf.writeEnumConstant(this.mainArm);
        buf.writeBoolean(this.filterText);
        buf.writeBoolean(this.allowsListing);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onClientSettings(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientSettingsC2SPacket.class, "language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing", "language", "viewDistance", "chatVisibility", "chatColors", "playerModelBitMask", "mainArm", "filterText", "allowsListing"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientSettingsC2SPacket.class, "language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing", "language", "viewDistance", "chatVisibility", "chatColors", "playerModelBitMask", "mainArm", "filterText", "allowsListing"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientSettingsC2SPacket.class, "language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing", "language", "viewDistance", "chatVisibility", "chatColors", "playerModelBitMask", "mainArm", "filterText", "allowsListing"}, this, object);
    }
}

