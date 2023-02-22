/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface SentMessage {
    public Text getContent();

    public void send(ServerPlayerEntity var1, boolean var2, MessageType.Parameters var3);

    public static SentMessage of(SignedMessage message) {
        if (message.isSenderMissing()) {
            return new Profileless(message.getContent());
        }
        return new Chat(message);
    }

    public record Profileless(Text getContent) implements SentMessage
    {
        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            sender.networkHandler.sendProfilelessChatMessage(this.getContent, params);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Profileless.class, "content", "getContent"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Profileless.class, "content", "getContent"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Profileless.class, "content", "getContent"}, this, object);
        }
    }

    public record Chat(SignedMessage message) implements SentMessage
    {
        @Override
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                sender.networkHandler.sendChatMessage(signedMessage, params);
            }
        }
    }
}

