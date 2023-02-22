/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.Nullable;

public interface MessageVerifier {
    public static MessageVerifier create(@Nullable PlayerPublicKey publicKey, boolean secureChatEnforced) {
        if (publicKey != null) {
            return new Impl(publicKey.createSignatureInstance());
        }
        return new Unsigned(secureChatEnforced);
    }

    public Status verify(MessageHeader var1, MessageSignatureData var2, byte[] var3);

    public Status verify(SignedMessage var1);

    public static class Impl
    implements MessageVerifier {
        private final SignatureVerifier signatureVerifier;
        @Nullable
        private MessageSignatureData precedingSignature;
        private boolean lastMessageVerified = true;

        public Impl(SignatureVerifier signatureVerifier) {
            this.signatureVerifier = signatureVerifier;
        }

        private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData signature, boolean fullMessage) {
            if (signature.isEmpty()) {
                return false;
            }
            if (fullMessage && signature.equals(this.precedingSignature)) {
                return true;
            }
            return this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature());
        }

        private boolean verifyInternal(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean fullMessage) {
            return this.verifyPrecedingSignature(header, signature, fullMessage) && signature.verify(this.signatureVerifier, header, bodyDigest);
        }

        private Status getStatus(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean fullMessage) {
            boolean bl = this.lastMessageVerified = this.lastMessageVerified && this.verifyInternal(header, signature, bodyDigest, fullMessage);
            if (!this.lastMessageVerified) {
                return Status.BROKEN_CHAIN;
            }
            this.precedingSignature = signature;
            return Status.SECURE;
        }

        @Override
        public Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.getStatus(header, signature, bodyDigest, false);
        }

        @Override
        public Status verify(SignedMessage message) {
            byte[] bs = message.signedBody().digest().asBytes();
            return this.getStatus(message.signedHeader(), message.headerSignature(), bs, true);
        }
    }

    public static class Unsigned
    implements MessageVerifier {
        private final boolean secureChatEnforced;

        public Unsigned(boolean secureChatEnforced) {
            this.secureChatEnforced = secureChatEnforced;
        }

        private Status getStatus(MessageSignatureData signature) {
            if (!signature.isEmpty()) {
                return Status.BROKEN_CHAIN;
            }
            return this.secureChatEnforced ? Status.BROKEN_CHAIN : Status.NOT_SECURE;
        }

        @Override
        public Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.getStatus(signature);
        }

        @Override
        public Status verify(SignedMessage message) {
            return this.getStatus(message.headerSignature());
        }
    }

    public static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status SECURE = new Status();
        public static final /* enum */ Status NOT_SECURE = new Status();
        public static final /* enum */ Status BROKEN_CHAIN = new Status();
        private static final /* synthetic */ Status[] field_39913;

        public static Status[] values() {
            return (Status[])field_39913.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static /* synthetic */ Status[] method_45049() {
            return new Status[]{SECURE, NOT_SECURE, BROKEN_CHAIN};
        }

        static {
            field_39913 = Status.method_45049();
        }
    }
}

