/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record SignedMessage(MessageHeader signedHeader, MessageSignatureData headerSignature, MessageBody signedBody, Optional<Text> unsignedContent, FilterMask filterMask) {
    public static final Duration SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
    public static final Duration CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

    public SignedMessage(PacketByteBuf buf) {
        this(new MessageHeader(buf), new MessageSignatureData(buf), new MessageBody(buf), buf.readOptional(PacketByteBuf::readText), FilterMask.readMask(buf));
    }

    public static SignedMessage ofUnsigned(DecoratedContents content) {
        return SignedMessage.ofUnsigned(MessageMetadata.of(), content);
    }

    public static SignedMessage ofUnsigned(MessageMetadata metadata, DecoratedContents content) {
        MessageBody messageBody = new MessageBody(content, metadata.timestamp(), metadata.salt(), LastSeenMessageList.EMPTY);
        MessageHeader messageHeader = new MessageHeader(null, metadata.sender());
        return new SignedMessage(messageHeader, MessageSignatureData.EMPTY, messageBody, Optional.empty(), FilterMask.PASS_THROUGH);
    }

    public void write(PacketByteBuf buf) {
        this.signedHeader.write(buf);
        this.headerSignature.write(buf);
        this.signedBody.write(buf);
        buf.writeOptional(this.unsignedContent, PacketByteBuf::writeText);
        FilterMask.writeMask(buf, this.filterMask);
    }

    public SignedMessage withUnsignedContent(Text unsignedContent) {
        Optional<Text> optional = !this.getSignedContent().decorated().equals(unsignedContent) ? Optional.of(unsignedContent) : Optional.empty();
        return new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, optional, this.filterMask);
    }

    public SignedMessage withoutUnsigned() {
        if (this.unsignedContent.isPresent()) {
            return new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, Optional.empty(), this.filterMask);
        }
        return this;
    }

    public SignedMessage withFilterMask(FilterMask filterMask) {
        if (this.filterMask.equals(filterMask)) {
            return this;
        }
        return new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, this.unsignedContent, filterMask);
    }

    public SignedMessage withFilterMaskEnabled(boolean enabled) {
        return this.withFilterMask(enabled ? this.filterMask : FilterMask.PASS_THROUGH);
    }

    public boolean verify(SignatureVerifier verifier) {
        return this.headerSignature.verify(verifier, this.signedHeader, this.signedBody);
    }

    public boolean verify(PlayerPublicKey key) {
        SignatureVerifier signatureVerifier = key.createSignatureInstance();
        return this.verify(signatureVerifier);
    }

    public boolean verify(MessageSourceProfile profile) {
        PlayerPublicKey playerPublicKey = profile.playerPublicKey();
        return playerPublicKey != null && this.verify(playerPublicKey);
    }

    public DecoratedContents getSignedContent() {
        return this.signedBody.content();
    }

    public Text getContent() {
        return this.unsignedContent().orElse(this.getSignedContent().decorated());
    }

    public Instant getTimestamp() {
        return this.signedBody.timestamp();
    }

    public long getSalt() {
        return this.signedBody.salt();
    }

    public boolean isExpiredOnServer(Instant currentTime) {
        return currentTime.isAfter(this.getTimestamp().plus(SERVERBOUND_TIME_TO_LIVE));
    }

    public boolean isExpiredOnClient(Instant currentTime) {
        return currentTime.isAfter(this.getTimestamp().plus(CLIENTBOUND_TIME_TO_LIVE));
    }

    public MessageMetadata createMetadata() {
        return new MessageMetadata(this.signedHeader.sender(), this.getTimestamp(), this.getSalt());
    }

    @Nullable
    public LastSeenMessageList.Entry toLastSeenMessageEntry() {
        MessageMetadata messageMetadata = this.createMetadata();
        if (!this.headerSignature.isEmpty() && !messageMetadata.lacksSender()) {
            return new LastSeenMessageList.Entry(messageMetadata.sender(), this.headerSignature);
        }
        return null;
    }

    public boolean canVerifyFrom(UUID sender) {
        return !this.headerSignature.isEmpty() && this.signedHeader.sender().equals(sender);
    }

    public boolean isFullyFiltered() {
        return this.filterMask.isFullyFiltered();
    }
}

