/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundFromEntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    private SoundEvent sound;
    private SoundCategory category;
    private int entityId;
    private float volume;
    private float pitch;

    public PlaySoundFromEntityS2CPacket() {
    }

    public PlaySoundFromEntityS2CPacket(SoundEvent sound, SoundCategory category, Entity entity, float volume, float pitch) {
        Validate.notNull((Object)sound, (String)"sound", (Object[])new Object[0]);
        this.sound = sound;
        this.category = category;
        this.entityId = entity.getEntityId();
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.sound = (SoundEvent)Registry.SOUND_EVENT.get(buf.readVarInt());
        this.category = buf.readEnumConstant(SoundCategory.class);
        this.entityId = buf.readVarInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        buf.writeEnumConstant(this.category);
        buf.writeVarInt(this.entityId);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public float getVolume() {
        return this.volume;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlaySoundFromEntity(this);
    }
}

