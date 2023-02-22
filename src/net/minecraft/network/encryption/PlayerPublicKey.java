/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.network.encryption;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import net.minecraft.util.dynamic.Codecs;

public record PlayerPublicKey(PublicKeyData data) {
    public static final Text field_39953 = Text.translatable("multiplayer.disconnect.missing_public_key");
    public static final Text field_39954 = Text.translatable("multiplayer.disconnect.expired_public_key");
    private static final Text field_39956 = Text.translatable("multiplayer.disconnect.invalid_public_key_signature");
    public static final Duration field_39955 = Duration.ofHours(8L);
    public static final Codec<PlayerPublicKey> CODEC = PublicKeyData.CODEC.xmap(PlayerPublicKey::new, PlayerPublicKey::data);

    public static PlayerPublicKey verifyAndDecode(SignatureVerifier servicesSignatureVerifier, UUID playerUuid, PublicKeyData publicKeyData, Duration duration) throws class_7652 {
        if (publicKeyData.method_45103(duration)) {
            throw new class_7652(field_39954);
        }
        if (!publicKeyData.verifyKey(servicesSignatureVerifier, playerUuid)) {
            throw new class_7652(field_39956);
        }
        return new PlayerPublicKey(publicKeyData);
    }

    public SignatureVerifier createSignatureInstance() {
        return SignatureVerifier.create(this.data.key, "SHA256withRSA");
    }

    public static final class PublicKeyData
    extends Record {
        private final Instant expiresAt;
        final PublicKey key;
        private final byte[] keySignature;
        private static final int KEY_SIGNATURE_MAX_SIZE = 4096;
        public static final Codec<PublicKeyData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.INSTANT.fieldOf("expires_at").forGetter(PublicKeyData::expiresAt), (App)NetworkEncryptionUtils.RSA_PUBLIC_KEY_CODEC.fieldOf("key").forGetter(PublicKeyData::key), (App)Codecs.BASE_64.fieldOf("signature_v2").forGetter(PublicKeyData::keySignature)).apply((Applicative)instance, PublicKeyData::new));

        public PublicKeyData(PacketByteBuf buf) {
            this(buf.readInstant(), buf.readPublicKey(), buf.readByteArray(4096));
        }

        public PublicKeyData(Instant instant, PublicKey publicKey, byte[] bs) {
            this.expiresAt = instant;
            this.key = publicKey;
            this.keySignature = bs;
        }

        public void write(PacketByteBuf buf) {
            buf.writeInstant(this.expiresAt);
            buf.writePublicKey(this.key);
            buf.writeByteArray(this.keySignature);
        }

        boolean verifyKey(SignatureVerifier servicesSignatureVerifier, UUID playerUuid) {
            return servicesSignatureVerifier.validate(this.toSerializedString(playerUuid), this.keySignature);
        }

        private byte[] toSerializedString(UUID playerUuid) {
            byte[] bs = this.key.getEncoded();
            byte[] cs = new byte[24 + bs.length];
            ByteBuffer byteBuffer = ByteBuffer.wrap(cs).order(ByteOrder.BIG_ENDIAN);
            byteBuffer.putLong(playerUuid.getMostSignificantBits()).putLong(playerUuid.getLeastSignificantBits()).putLong(this.expiresAt.toEpochMilli()).put(bs);
            return cs;
        }

        public boolean isExpired() {
            return this.expiresAt.isBefore(Instant.now());
        }

        public boolean method_45103(Duration duration) {
            return this.expiresAt.plus(duration).isBefore(Instant.now());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PublicKeyData.class, "expiresAt;key;keySignature", "expiresAt", "key", "keySignature"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PublicKeyData.class, "expiresAt;key;keySignature", "expiresAt", "key", "keySignature"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PublicKeyData.class, "expiresAt;key;keySignature", "expiresAt", "key", "keySignature"}, this, object);
        }

        public Instant expiresAt() {
            return this.expiresAt;
        }

        public PublicKey key() {
            return this.key;
        }

        public byte[] keySignature() {
            return this.keySignature;
        }
    }

    public static class class_7652
    extends TextifiedException {
        public class_7652(Text text) {
            super(text);
        }
    }
}

