/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.util.Util;

public record MessageMetadata(UUID sender, Instant timestamp, long salt) {
    public MessageMetadata(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readInstant(), buf.readLong());
    }

    public static MessageMetadata of(UUID sender) {
        return new MessageMetadata(sender, Instant.now(), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
    }

    public static MessageMetadata of() {
        return MessageMetadata.of(Util.NIL_UUID);
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.sender);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
    }

    public boolean lacksSender() {
        return this.sender.equals(Util.NIL_UUID);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageMetadata.class, "profileId;timeStamp;salt", "sender", "timestamp", "salt"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageMetadata.class, "profileId;timeStamp;salt", "sender", "timestamp", "salt"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageMetadata.class, "profileId;timeStamp;salt", "sender", "timestamp", "salt"}, this, object);
    }
}

