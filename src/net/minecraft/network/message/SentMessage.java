/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.network.message;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.DynamicRegistryManager;

public interface SentMessage {
    public Text getContent();

    public void send(ServerPlayerEntity var1, boolean var2, MessageType.Parameters var3);

    public void afterPacketsSent(PlayerManager var1);

    public static SentMessage of(SignedMessage message) {
        if (message.createMetadata().lacksSender()) {
            return new Profileless(message);
        }
        return new Chat(message);
    }

    public static class Profileless
    implements SentMessage {
        private final SignedMessage message;

        public Profileless(SignedMessage message) {
            this.message = message;
        }

        @Override
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                DynamicRegistryManager dynamicRegistryManager = sender.world.getRegistryManager();
                MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
                sender.networkHandler.sendPacket(new ChatMessageS2CPacket(signedMessage, serialized));
                sender.networkHandler.addPendingAcknowledgment(signedMessage);
            }
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
        }
    }

    public static class Chat
    implements SentMessage {
        private final SignedMessage message;
        private final Set<ServerPlayerEntity> recipients = Sets.newIdentityHashSet();

        public Chat(SignedMessage message) {
            this.message = message;
        }

        @Override
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                this.recipients.add(sender);
                DynamicRegistryManager dynamicRegistryManager = sender.world.getRegistryManager();
                MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
                sender.networkHandler.sendPacket(new ChatMessageS2CPacket(signedMessage, serialized), PacketCallbacks.of(() -> new MessageHeaderS2CPacket(this.message)));
                sender.networkHandler.addPendingAcknowledgment(signedMessage);
            }
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
            playerManager.sendMessageHeader(this.message, this.recipients);
        }
    }
}

