/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.util.concurrent.CompletableFuture;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface MessageDecorator {
    public static final MessageDecorator NOOP = (sender, message) -> CompletableFuture.completedFuture(message);

    public CompletableFuture<Text> decorate(@Nullable ServerPlayerEntity var1, Text var2);

    default public CompletableFuture<SignedMessage> decorate(@Nullable ServerPlayerEntity sender, SignedMessage message) {
        if (message.getSignedContent().isDecorated()) {
            return CompletableFuture.completedFuture(message);
        }
        return this.decorate(sender, message.getContent()).thenApply(message::withUnsignedContent);
    }

    public static SignedMessage attachIfNotDecorated(SignedMessage message, Text attached) {
        if (!message.getSignedContent().isDecorated()) {
            return message.withUnsignedContent(attached);
        }
        return message;
    }
}

