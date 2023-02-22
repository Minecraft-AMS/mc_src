/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private boolean clearCurrent;
    private Map<Identifier, Advancement.Task> toEarn;
    private Set<Identifier> toRemove;
    private Map<Identifier, AdvancementProgress> toSetProgress;

    public AdvancementUpdateS2CPacket() {
    }

    public AdvancementUpdateS2CPacket(boolean clearCurrent, Collection<Advancement> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress) {
        this.clearCurrent = clearCurrent;
        this.toEarn = Maps.newHashMap();
        for (Advancement advancement : toEarn) {
            this.toEarn.put(advancement.getId(), advancement.createTask());
        }
        this.toRemove = toRemove;
        this.toSetProgress = Maps.newHashMap(toSetProgress);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onAdvancements(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        Identifier identifier;
        int j;
        this.clearCurrent = buf.readBoolean();
        this.toEarn = Maps.newHashMap();
        this.toRemove = Sets.newLinkedHashSet();
        this.toSetProgress = Maps.newHashMap();
        int i = buf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = buf.readIdentifier();
            Advancement.Task task = Advancement.Task.fromPacket(buf);
            this.toEarn.put(identifier, task);
        }
        i = buf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = buf.readIdentifier();
            this.toRemove.add(identifier);
        }
        i = buf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = buf.readIdentifier();
            this.toSetProgress.put(identifier, AdvancementProgress.fromPacket(buf));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.clearCurrent);
        buf.writeVarInt(this.toEarn.size());
        for (Map.Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
            Identifier identifier = entry.getKey();
            Advancement.Task task = entry.getValue();
            buf.writeIdentifier(identifier);
            task.toPacket(buf);
        }
        buf.writeVarInt(this.toRemove.size());
        for (Identifier identifier : this.toRemove) {
            buf.writeIdentifier(identifier);
        }
        buf.writeVarInt(this.toSetProgress.size());
        for (Map.Entry entry : this.toSetProgress.entrySet()) {
            buf.writeIdentifier((Identifier)entry.getKey());
            ((AdvancementProgress)entry.getValue()).toPacket(buf);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
        return this.toEarn;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<Identifier> getAdvancementIdsToRemove() {
        return this.toRemove;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
        return this.toSetProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldClearCurrent() {
        return this.clearCurrent;
    }
}

