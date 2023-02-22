/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.report.log;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public interface ReceivedMessage
extends ChatLogEntry {
    public static ChatMessage of(GameProfile gameProfile, Text displayName, SignedMessage message, MessageTrustStatus trustStatus) {
        return new ChatMessage(gameProfile, displayName, message, trustStatus);
    }

    public static GameMessage of(Text message, Instant timestamp) {
        return new GameMessage(message, timestamp);
    }

    public Text getContent();

    default public Text getNarration() {
        return this.getContent();
    }

    public boolean isSentFrom(UUID var1);

    @Environment(value=EnvType.CLIENT)
    public record ChatMessage(GameProfile profile, Text displayName, SignedMessage message, MessageTrustStatus trustStatus) implements ReceivedMessage,
    HeaderEntry
    {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        @Override
        public Text getContent() {
            if (!this.message.filterMask().isPassThrough()) {
                Text text = this.message.filterMask().filter(this.message.getSignedContent());
                return Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
            }
            return this.message.getContent();
        }

        @Override
        public Text getNarration() {
            Text text = this.getContent();
            Text text2 = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.message.narrate", this.displayName, text, text2);
        }

        public Text getHeadingText() {
            Text text = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.heading", this.displayName, text);
        }

        private Text getFormattedTimestamp() {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(this.message.getTimestamp(), ZoneOffset.systemDefault());
            return Text.literal(localDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return this.message.canVerifyFrom(uuid);
        }

        @Override
        public MessageHeader header() {
            return this.message.signedHeader();
        }

        @Override
        public byte[] bodyDigest() {
            return this.message.signedBody().digest().asBytes();
        }

        @Override
        public MessageSignatureData headerSignature() {
            return this.message.headerSignature();
        }

        public UUID getSenderUuid() {
            return this.profile.getId();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatMessage.class, "profile;displayName;message;trustLevel", "profile", "displayName", "message", "trustStatus"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatMessage.class, "profile;displayName;message;trustLevel", "profile", "displayName", "message", "trustStatus"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatMessage.class, "profile;displayName;message;trustLevel", "profile", "displayName", "message", "trustStatus"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record GameMessage(Text message, Instant timestamp) implements ReceivedMessage
    {
        @Override
        public Text getContent() {
            return this.message;
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this, object);
        }
    }
}

