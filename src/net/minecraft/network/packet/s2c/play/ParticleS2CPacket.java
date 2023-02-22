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
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ParticleS2CPacket
implements Packet<ClientPlayPacketListener> {
    private float x;
    private float y;
    private float z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float speed;
    private int count;
    private boolean longDistance;
    private ParticleEffect parameters;

    public ParticleS2CPacket() {
    }

    public <T extends ParticleEffect> ParticleS2CPacket(T parameters, boolean longDistance, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        this.parameters = parameters;
        this.longDistance = longDistance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.count = count;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        ParticleType particleType = (ParticleType)Registry.PARTICLE_TYPE.get(buf.readInt());
        if (particleType == null) {
            particleType = ParticleTypes.BARRIER;
        }
        this.longDistance = buf.readBoolean();
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.offsetX = buf.readFloat();
        this.offsetY = buf.readFloat();
        this.offsetZ = buf.readFloat();
        this.speed = buf.readFloat();
        this.count = buf.readInt();
        this.parameters = this.readParticleParameters(buf, particleType);
    }

    private <T extends ParticleEffect> T readParticleParameters(PacketByteBuf buf, ParticleType<T> type) {
        return type.getParametersFactory().read(type, buf);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(Registry.PARTICLE_TYPE.getRawId(this.parameters.getType()));
        buf.writeBoolean(this.longDistance);
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.offsetX);
        buf.writeFloat(this.offsetY);
        buf.writeFloat(this.offsetZ);
        buf.writeFloat(this.speed);
        buf.writeInt(this.count);
        this.parameters.write(buf);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLongDistance() {
        return this.longDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public float getOffsetX() {
        return this.offsetX;
    }

    @Environment(value=EnvType.CLIENT)
    public float getOffsetY() {
        return this.offsetY;
    }

    @Environment(value=EnvType.CLIENT)
    public float getOffsetZ() {
        return this.offsetZ;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSpeed() {
        return this.speed;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCount() {
        return this.count;
    }

    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParameters() {
        return this.parameters;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onParticle(this);
    }
}

