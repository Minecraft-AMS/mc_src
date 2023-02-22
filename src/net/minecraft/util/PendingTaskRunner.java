/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

public class PendingTaskRunner {
    private final AtomicReference<FutureRunnable> pending = new AtomicReference();
    @Nullable
    private CompletableFuture<?> running;

    public void tick() {
        if (this.running != null && this.running.isDone()) {
            this.running = null;
        }
        if (this.running == null) {
            this.poll();
        }
    }

    private void poll() {
        FutureRunnable futureRunnable = this.pending.getAndSet(null);
        if (futureRunnable != null) {
            this.running = futureRunnable.run();
        }
    }

    public void queue(FutureRunnable task) {
        this.pending.set(task);
    }

    @FunctionalInterface
    public static interface FutureRunnable {
        public CompletableFuture<?> run();
    }
}

