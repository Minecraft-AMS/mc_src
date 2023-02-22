/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.network.packet.c2s.login;

import com.mojang.datafixers.util.Either;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginKeyC2SPacket
implements Packet<ServerLoginPacketListener> {
    private final byte[] encryptedSecretKey;
    private final Either<byte[], NetworkEncryptionUtils.SignatureData> nonce;

    public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws NetworkEncryptionException {
        this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
        this.nonce = Either.left((Object)NetworkEncryptionUtils.encrypt(publicKey, nonce));
    }

    public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, long seed, byte[] signature) throws NetworkEncryptionException {
        this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
        this.nonce = Either.right((Object)new NetworkEncryptionUtils.SignatureData(seed, signature));
    }

    public LoginKeyC2SPacket(PacketByteBuf buf) {
        this.encryptedSecretKey = buf.readByteArray();
        this.nonce = buf.readEither(PacketByteBuf::readByteArray, NetworkEncryptionUtils.SignatureData::new);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByteArray(this.encryptedSecretKey);
        buf.writeEither(this.nonce, PacketByteBuf::writeByteArray, NetworkEncryptionUtils.SignatureData::write);
    }

    @Override
    public void apply(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.onKey(this);
    }

    public SecretKey decryptSecretKey(PrivateKey privateKey) throws NetworkEncryptionException {
        return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
    }

    public boolean verifySignedNonce(byte[] nonce, PlayerPublicKey publicKeyInfo) {
        return (Boolean)this.nonce.map(encrypted -> false, signature -> publicKeyInfo.createSignatureInstance().validate(updater -> {
            updater.update(nonce);
            updater.update(signature.getSalt());
        }, signature.signature()));
    }

    public boolean verifyEncryptedNonce(byte[] nonce, PrivateKey privateKey) {
        Optional optional = this.nonce.left();
        try {
            return optional.isPresent() && Arrays.equals(nonce, NetworkEncryptionUtils.decrypt(privateKey, (byte[])optional.get()));
        }
        catch (NetworkEncryptionException networkEncryptionException) {
            return false;
        }
    }
}

