/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network.message;

import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class MessageTrustStatus
extends Enum<MessageTrustStatus> {
    public static final /* enum */ MessageTrustStatus SECURE = new MessageTrustStatus();
    public static final /* enum */ MessageTrustStatus MODIFIED = new MessageTrustStatus();
    public static final /* enum */ MessageTrustStatus FILTERED = new MessageTrustStatus();
    public static final /* enum */ MessageTrustStatus NOT_SECURE = new MessageTrustStatus();
    public static final /* enum */ MessageTrustStatus BROKEN_CHAIN = new MessageTrustStatus();
    private static final /* synthetic */ MessageTrustStatus[] field_39784;

    public static MessageTrustStatus[] values() {
        return (MessageTrustStatus[])field_39784.clone();
    }

    public static MessageTrustStatus valueOf(String string) {
        return Enum.valueOf(MessageTrustStatus.class, string);
    }

    public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry sender, Instant receptionTimestamp) {
        if (sender == null) {
            return NOT_SECURE;
        }
        MessageVerifier.Status status = sender.getMessageVerifier().verify(message);
        if (status == MessageVerifier.Status.BROKEN_CHAIN) {
            return BROKEN_CHAIN;
        }
        if (status == MessageVerifier.Status.NOT_SECURE) {
            return NOT_SECURE;
        }
        if (message.isExpiredOnClient(receptionTimestamp)) {
            return NOT_SECURE;
        }
        if (!message.filterMask().isPassThrough()) {
            return FILTERED;
        }
        if (message.unsignedContent().isPresent()) {
            return MODIFIED;
        }
        if (!decorated.contains(message.getSignedContent().decorated())) {
            return MODIFIED;
        }
        return SECURE;
    }

    public boolean isInsecure() {
        return this == NOT_SECURE || this == BROKEN_CHAIN;
    }

    @Nullable
    public MessageIndicator createIndicator(SignedMessage message) {
        return switch (this) {
            case MODIFIED -> MessageIndicator.modified(message.getSignedContent().plain());
            case FILTERED -> MessageIndicator.filtered();
            case NOT_SECURE -> MessageIndicator.notSecure();
            default -> null;
        };
    }

    private static /* synthetic */ MessageTrustStatus[] method_44743() {
        return new MessageTrustStatus[]{SECURE, MODIFIED, FILTERED, NOT_SECURE, BROKEN_CHAIN};
    }

    static {
        field_39784 = MessageTrustStatus.method_44743();
    }
}

