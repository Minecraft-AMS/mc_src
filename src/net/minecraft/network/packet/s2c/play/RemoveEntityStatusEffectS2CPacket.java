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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RemoveEntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private StatusEffect effectType;

    public RemoveEntityStatusEffectS2CPacket() {
    }

    public RemoveEntityStatusEffectS2CPacket(int entityId, StatusEffect effectType) {
        this.entityId = entityId;
        this.effectType = effectType;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.effectType = StatusEffect.byRawId(buf.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(StatusEffect.getRawId(this.effectType));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onRemoveEntityEffect(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getEffectType() {
        return this.effectType;
    }
}

