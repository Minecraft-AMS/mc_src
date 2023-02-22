/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network.message;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MessageHandler {
    private static final Text CHAT_VALIDATION_FAILED_DISCONNECT_REASON = Text.translatable("multiplayer.disconnect.chat_validation_failed");
    private final MinecraftClient client;
    private final Deque<ProcessableMessage> delayedMessages = Queues.newArrayDeque();
    private long chatDelay;
    private long lastProcessTime;

    public MessageHandler(MinecraftClient client) {
        this.client = client;
    }

    public void processDelayedMessages() {
        if (this.chatDelay == 0L) {
            return;
        }
        if (Util.getMeasuringTimeMs() >= this.lastProcessTime + this.chatDelay) {
            ProcessableMessage processableMessage = this.delayedMessages.poll();
            while (processableMessage != null && !processableMessage.accept()) {
                processableMessage = this.delayedMessages.poll();
            }
        }
    }

    public void setChatDelay(double chatDelay) {
        long l = (long)(chatDelay * 1000.0);
        if (l == 0L && this.chatDelay > 0L) {
            this.delayedMessages.forEach(ProcessableMessage::accept);
            this.delayedMessages.clear();
        }
        this.chatDelay = l;
    }

    public void process() {
        this.delayedMessages.remove().accept();
    }

    public long getUnprocessedMessageCount() {
        return this.delayedMessages.stream().filter(ProcessableMessage::isUnprocessed).count();
    }

    public void processAll() {
        this.delayedMessages.forEach(message -> {
            message.markProcessed();
            message.accept();
        });
        this.delayedMessages.clear();
    }

    public boolean removeDelayedMessage(MessageSignatureData signature) {
        for (ProcessableMessage processableMessage : this.delayedMessages) {
            if (!processableMessage.removeMatching(signature)) continue;
            return true;
        }
        return false;
    }

    private boolean shouldDelay() {
        return this.chatDelay > 0L && Util.getMeasuringTimeMs() < this.lastProcessTime + this.chatDelay;
    }

    private void process(ProcessableMessage message) {
        if (this.shouldDelay()) {
            this.delayedMessages.add(message);
        } else {
            message.accept();
        }
    }

    public void onChatMessage(final SignedMessage message, final MessageType.Parameters params) {
        final boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
        final SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
        final Text text = params.applyChatDecoration(signedMessage.getContent());
        MessageMetadata messageMetadata = message.createMetadata();
        if (!messageMetadata.lacksSender()) {
            final PlayerListEntry playerListEntry = this.getPlayerListEntry(messageMetadata.sender());
            final Instant instant = Instant.now();
            this.process(new ProcessableMessage(){
                private boolean processed;

                @Override
                public boolean accept() {
                    if (this.processed) {
                        byte[] bs = message.signedBody().digest().asBytes();
                        MessageHandler.this.processHeader(message.signedHeader(), message.headerSignature(), bs);
                        return false;
                    }
                    return MessageHandler.this.processChatMessage(params, message, text, playerListEntry, bl, instant);
                }

                @Override
                public boolean removeMatching(MessageSignatureData signature) {
                    if (message.headerSignature().equals(signature)) {
                        this.processed = true;
                        return true;
                    }
                    return false;
                }

                @Override
                public void markProcessed() {
                    this.processed = true;
                }

                @Override
                public boolean isUnprocessed() {
                    return !this.processed;
                }
            });
        } else {
            this.process(new ProcessableMessage(){

                @Override
                public boolean accept() {
                    return MessageHandler.this.processProfilelessMessage(params, signedMessage, text);
                }

                @Override
                public boolean isUnprocessed() {
                    return true;
                }
            });
        }
    }

    public void onMessageHeader(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
        this.process(() -> this.processHeader(header, signature, bodyDigest));
    }

    boolean processChatMessage(MessageType.Parameters params, SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry, boolean onlyShowSecureChat, Instant receptionTimestamp) {
        boolean bl = this.processChatMessageInternal(params, message, decorated, senderEntry, onlyShowSecureChat, receptionTimestamp);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.acknowledge(message, bl);
        }
        return bl;
    }

    private boolean processChatMessageInternal(MessageType.Parameters params, SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry, boolean onlyShowSecureChat, Instant receptionTimestamp) {
        MessageTrustStatus messageTrustStatus = this.getStatus(message, decorated, senderEntry, receptionTimestamp);
        if (messageTrustStatus == MessageTrustStatus.BROKEN_CHAIN) {
            this.disconnect();
            return true;
        }
        if (onlyShowSecureChat && messageTrustStatus.isInsecure()) {
            return false;
        }
        if (this.client.shouldBlockMessages(message.createMetadata().sender()) || message.isFullyFiltered()) {
            return false;
        }
        MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message);
        MessageSignatureData messageSignatureData = message.headerSignature();
        FilterMask filterMask = message.filterMask();
        if (filterMask.isPassThrough()) {
            this.client.inGameHud.getChatHud().addMessage(decorated, messageSignatureData, messageIndicator);
            this.narrate(params, message.getContent());
        } else {
            Text text = filterMask.filter(message.getSignedContent());
            if (text != null) {
                this.client.inGameHud.getChatHud().addMessage(params.applyChatDecoration(text), messageSignatureData, messageIndicator);
                this.narrate(params, text);
            }
        }
        this.addToChatLog(message, params, senderEntry, messageTrustStatus);
        this.lastProcessTime = Util.getMeasuringTimeMs();
        return true;
    }

    boolean processProfilelessMessage(MessageType.Parameters params, SignedMessage message, Text decorated) {
        this.client.inGameHud.getChatHud().addMessage(decorated);
        this.narrate(params, message.getContent());
        this.addToChatLog(decorated, message.getTimestamp());
        this.lastProcessTime = Util.getMeasuringTimeMs();
        return true;
    }

    boolean processHeader(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
        MessageVerifier.Status status;
        PlayerListEntry playerListEntry = this.getPlayerListEntry(header.sender());
        if (playerListEntry != null && (status = playerListEntry.getMessageVerifier().verify(header, signature, bodyDigest)) == MessageVerifier.Status.BROKEN_CHAIN) {
            this.disconnect();
            return true;
        }
        this.addToChatLog(header, signature, bodyDigest);
        return false;
    }

    private void disconnect() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.getConnection().disconnect(CHAT_VALIDATION_FAILED_DISCONNECT_REASON);
        }
    }

    private void narrate(MessageType.Parameters params, Text message) {
        this.client.getNarratorManager().narrateChatMessage(() -> params.applyNarrationDecoration(message));
    }

    private MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry, Instant receptionTimestamp) {
        if (this.isAlwaysTrusted(message.createMetadata().sender())) {
            return MessageTrustStatus.SECURE;
        }
        return MessageTrustStatus.getStatus(message, decorated, senderEntry, receptionTimestamp);
    }

    private void addToChatLog(SignedMessage message, MessageType.Parameters params, @Nullable PlayerListEntry senderEntry, MessageTrustStatus trustStatus) {
        GameProfile gameProfile = senderEntry != null ? senderEntry.getProfile() : new GameProfile(message.createMetadata().sender(), params.name().getString());
        ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
        chatLog.add(ReceivedMessage.of(gameProfile, params.name(), message, trustStatus));
    }

    private void addToChatLog(Text message, Instant timestamp) {
        ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
        chatLog.add(ReceivedMessage.of(message, timestamp));
    }

    private void addToChatLog(MessageHeader header, MessageSignatureData signatures, byte[] bodyDigest) {
        ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
        chatLog.add(HeaderEntry.of(header, signatures, bodyDigest));
    }

    @Nullable
    private PlayerListEntry getPlayerListEntry(UUID sender) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        return clientPlayNetworkHandler != null ? clientPlayNetworkHandler.getPlayerListEntry(sender) : null;
    }

    public void onGameMessage(Text message, boolean overlay) {
        if (this.client.options.getHideMatchedNames().getValue().booleanValue() && this.client.shouldBlockMessages(this.extractSender(message))) {
            return;
        }
        if (overlay) {
            this.client.inGameHud.setOverlayMessage(message, false);
        } else {
            this.client.inGameHud.getChatHud().addMessage(message);
            this.addToChatLog(message, Instant.now());
        }
        this.client.getNarratorManager().narrate(message);
    }

    private UUID extractSender(Text text) {
        String string = TextVisitFactory.removeFormattingCodes(text);
        String string2 = StringUtils.substringBetween((String)string, (String)"<", (String)">");
        if (string2 == null) {
            return Util.NIL_UUID;
        }
        return this.client.getSocialInteractionsManager().getUuid(string2);
    }

    private boolean isAlwaysTrusted(UUID sender) {
        if (this.client.isInSingleplayer() && this.client.player != null) {
            UUID uUID = this.client.player.getGameProfile().getId();
            return uUID.equals(sender);
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static interface ProcessableMessage {
        default public boolean removeMatching(MessageSignatureData signature) {
            return false;
        }

        default public void markProcessed() {
        }

        public boolean accept();

        default public boolean isUnprocessed() {
            return false;
        }
    }
}

