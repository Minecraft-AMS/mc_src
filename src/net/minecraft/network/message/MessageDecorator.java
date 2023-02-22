/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface MessageDecorator {
    public static final MessageDecorator NOOP = (sender, message) -> CompletableFuture.completedFuture(message);

    public CompletableFuture<Text> decorate(@Nullable ServerPlayerEntity var1, Text var2);
}

