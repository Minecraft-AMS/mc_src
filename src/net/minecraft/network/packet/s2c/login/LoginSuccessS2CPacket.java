/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginSuccessS2CPacket
implements Packet<ClientLoginPacketListener> {
    private GameProfile profile;

    public LoginSuccessS2CPacket() {
    }

    public LoginSuccessS2CPacket(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        String string = buf.readString(36);
        String string2 = buf.readString(16);
        UUID uUID = UUID.fromString(string);
        this.profile = new GameProfile(uUID, string2);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        UUID uUID = this.profile.getId();
        buf.writeString(uUID == null ? "" : uUID.toString());
        buf.writeString(this.profile.getName());
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onLoginSuccess(this);
    }

    @Environment(value=EnvType.CLIENT)
    public GameProfile getProfile() {
        return this.profile;
    }
}

