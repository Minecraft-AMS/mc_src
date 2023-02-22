/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttributesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private final List<Entry> entries = Lists.newArrayList();

    public EntityAttributesS2CPacket() {
    }

    public EntityAttributesS2CPacket(int entityId, Collection<EntityAttributeInstance> attributes) {
        this.entityId = entityId;
        for (EntityAttributeInstance entityAttributeInstance : attributes) {
            this.entries.add(new Entry(entityAttributeInstance.getAttribute().getId(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers()));
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        int i = buf.readInt();
        for (int j = 0; j < i; ++j) {
            String string = buf.readString(64);
            double d = buf.readDouble();
            ArrayList list = Lists.newArrayList();
            int k = buf.readVarInt();
            for (int l = 0; l < k; ++l) {
                UUID uUID = buf.readUuid();
                list.add(new EntityAttributeModifier(uUID, "Unknown synced attribute modifier", buf.readDouble(), EntityAttributeModifier.Operation.fromId(buf.readByte())));
            }
            this.entries.add(new Entry(string, d, list));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeInt(this.entries.size());
        for (Entry entry : this.entries) {
            buf.writeString(entry.getId());
            buf.writeDouble(entry.getBaseValue());
            buf.writeVarInt(entry.getModifiers().size());
            for (EntityAttributeModifier entityAttributeModifier : entry.getModifiers()) {
                buf.writeUuid(entityAttributeModifier.getId());
                buf.writeDouble(entityAttributeModifier.getAmount());
                buf.writeByte(entityAttributeModifier.getOperation().getId());
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAttributes(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }

    public class Entry {
        private final String id;
        private final double baseValue;
        private final Collection<EntityAttributeModifier> modifiers;

        public Entry(String baseValue, double d, Collection<EntityAttributeModifier> collection) {
            this.id = baseValue;
            this.baseValue = d;
            this.modifiers = collection;
        }

        public String getId() {
            return this.id;
        }

        public double getBaseValue() {
            return this.baseValue;
        }

        public Collection<EntityAttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

