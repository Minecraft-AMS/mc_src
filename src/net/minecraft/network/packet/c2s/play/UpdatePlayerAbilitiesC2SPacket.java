/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdatePlayerAbilitiesC2SPacket
implements Packet<ServerPlayPacketListener> {
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public UpdatePlayerAbilitiesC2SPacket() {
    }

    public UpdatePlayerAbilitiesC2SPacket(PlayerAbilities abilities) {
        this.setInvulnerable(abilities.invulnerable);
        this.setFlying(abilities.flying);
        this.setAllowFlying(abilities.allowFlying);
        this.setCreativeMode(abilities.creativeMode);
        this.setFlySpeed(abilities.getFlySpeed());
        this.setWalkSpeed(abilities.getWalkSpeed());
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        byte b = buf.readByte();
        this.setInvulnerable((b & 1) > 0);
        this.setFlying((b & 2) > 0);
        this.setAllowFlying((b & 4) > 0);
        this.setCreativeMode((b & 8) > 0);
        this.setFlySpeed(buf.readFloat());
        this.setWalkSpeed(buf.readFloat());
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        byte b = 0;
        if (this.isInvulnerable()) {
            b = (byte)(b | 1);
        }
        if (this.isFlying()) {
            b = (byte)(b | 2);
        }
        if (this.isFlyingAllowed()) {
            b = (byte)(b | 4);
        }
        if (this.isCreativeMode()) {
            b = (byte)(b | 8);
        }
        buf.writeByte(b);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean bl) {
        this.invulnerable = bl;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(boolean bl) {
        this.flying = bl;
    }

    public boolean isFlyingAllowed() {
        return this.allowFlying;
    }

    public void setAllowFlying(boolean bl) {
        this.allowFlying = bl;
    }

    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    public void setCreativeMode(boolean bl) {
        this.creativeMode = bl;
    }

    public void setFlySpeed(float f) {
        this.flySpeed = f;
    }

    public void setWalkSpeed(float f) {
        this.walkSpeed = f;
    }
}
