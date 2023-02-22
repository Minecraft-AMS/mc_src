/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record SignedMessage(MessageLink link, @Nullable MessageSignatureData signature, MessageBody signedBody, @Nullable Text unsignedContent, FilterMask filterMask) {
    public static final MapCodec<SignedMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)MessageLink.CODEC.fieldOf("link").forGetter(SignedMessage::link), (App)MessageSignatureData.CODEC.optionalFieldOf("signature").forGetter(message -> Optional.ofNullable(message.signature)), (App)MessageBody.CODEC.forGetter(SignedMessage::signedBody), (App)Codecs.TEXT.optionalFieldOf("unsigned_content").forGetter(message -> Optional.ofNullable(message.unsignedContent)), (App)FilterMask.CODEC.optionalFieldOf("filter_mask", (Object)FilterMask.PASS_THROUGH).forGetter(SignedMessage::filterMask)).apply((Applicative)instance, (link, signature, signedBody, unsignedContent, filterMask) -> new SignedMessage((MessageLink)link, signature.orElse(null), (MessageBody)signedBody, unsignedContent.orElse(null), (FilterMask)filterMask)));
    private static final UUID NIL_UUID = Util.NIL_UUID;
    public static final Duration SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
    public static final Duration CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

    public static SignedMessage ofUnsigned(String content) {
        return SignedMessage.ofUnsigned(NIL_UUID, content);
    }

    public static SignedMessage ofUnsigned(UUID sender, String content) {
        MessageBody messageBody = MessageBody.ofUnsigned(content);
        MessageLink messageLink = MessageLink.of(sender);
        return new SignedMessage(messageLink, null, messageBody, null, FilterMask.PASS_THROUGH);
    }

    public SignedMessage withUnsignedContent(Text unsignedContent) {
        Text text = !unsignedContent.equals(Text.literal(this.getSignedContent())) ? unsignedContent : null;
        return new SignedMessage(this.link, this.signature, this.signedBody, text, this.filterMask);
    }

    public SignedMessage withoutUnsigned() {
        if (this.unsignedContent != null) {
            return new SignedMessage(this.link, this.signature, this.signedBody, null, this.filterMask);
        }
        return this;
    }

    public SignedMessage withFilterMask(FilterMask filterMask) {
        if (this.filterMask.equals(filterMask)) {
            return this;
        }
        return new SignedMessage(this.link, this.signature, this.signedBody, this.unsignedContent, filterMask);
    }

    public SignedMessage withFilterMaskEnabled(boolean enabled) {
        return this.withFilterMask(enabled ? this.filterMask : FilterMask.PASS_THROUGH);
    }

    public static void update(SignatureUpdatable.SignatureUpdater updater, MessageLink link, MessageBody body) throws SignatureException {
        updater.update(Ints.toByteArray((int)1));
        link.update(updater);
        body.update(updater);
    }

    public boolean verify(SignatureVerifier verifier) {
        return this.signature != null && this.signature.verify(verifier, updater -> SignedMessage.update(updater, this.link, this.signedBody));
    }

    public String getSignedContent() {
        return this.signedBody.content();
    }

    public Text getContent() {
        return Objects.requireNonNullElseGet(this.unsignedContent, () -> Text.literal(this.getSignedContent()));
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

    public UUID getSender() {
        return this.link.sender();
    }

    public boolean isSenderMissing() {
        return this.getSender().equals(NIL_UUID);
    }

    public boolean hasSignature() {
        return this.signature != null;
    }

    public boolean canVerifyFrom(UUID sender) {
        return this.hasSignature() && this.link.sender().equals(sender);
    }

    public boolean isFullyFiltered() {
        return this.filterMask.isFullyFiltered();
    }
}

