/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.security.SignatureException;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.jetbrains.annotations.Nullable;

public record MessageHeader(@Nullable MessageSignatureData precedingSignature, UUID sender) {
    public MessageHeader(PacketByteBuf buf) {
        this((MessageSignatureData)buf.readNullable(MessageSignatureData::new), buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeNullable(this.precedingSignature, (buf2, precedingSignature) -> precedingSignature.write((PacketByteBuf)((Object)buf2)));
        buf.writeUuid(this.sender);
    }

    public void update(SignatureUpdatable.SignatureUpdater updater, byte[] bodyDigest) throws SignatureException {
        if (this.precedingSignature != null) {
            updater.update(this.precedingSignature.data());
        }
        updater.update(DynamicSerializableUuid.toByteArray(this.sender));
        updater.update(bodyDigest);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageHeader.class, "previousSignature;sender", "precedingSignature", "sender"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageHeader.class, "previousSignature;sender", "precedingSignature", "sender"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageHeader.class, "previousSignature;sender", "precedingSignature", "sender"}, this, object);
    }
}

