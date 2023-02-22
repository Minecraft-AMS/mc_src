/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteArrays
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageHeader;
import org.jetbrains.annotations.Nullable;

public record MessageSignatureData(byte[] data) {
    public static final MessageSignatureData EMPTY = new MessageSignatureData(ByteArrays.EMPTY_ARRAY);

    public MessageSignatureData(PacketByteBuf buf) {
        this(buf.readByteArray());
    }

    public void write(PacketByteBuf buf) {
        buf.writeByteArray(this.data);
    }

    public boolean verify(SignatureVerifier verifier, MessageHeader header, MessageBody body) {
        if (!this.isEmpty()) {
            byte[] bs = body.digest().asBytes();
            return verifier.validate(updatable -> header.update(updatable, bs), this.data);
        }
        return false;
    }

    public boolean verify(SignatureVerifier verifier, MessageHeader header, byte[] bodyDigest) {
        if (!this.isEmpty()) {
            return verifier.validate(updatable -> header.update(updatable, bodyDigest), this.data);
        }
        return false;
    }

    public boolean isEmpty() {
        return this.data.length == 0;
    }

    @Nullable
    public ByteBuffer toByteBuffer() {
        if (!this.isEmpty()) {
            return ByteBuffer.wrap(this.data);
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSignatureData)) return false;
        MessageSignatureData messageSignatureData = (MessageSignatureData)o;
        if (!Arrays.equals(this.data, messageSignatureData.data)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public String toString() {
        if (!this.isEmpty()) {
            return Base64.getEncoder().encodeToString(this.data);
        }
        return "empty";
    }
}

