/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.util.thread.FutureQueue;
import org.slf4j.Logger;

public class MessageChainTaskQueue
implements FutureQueue {
    private static final Logger field_39828 = LogUtils.getLogger();
    private CompletableFuture<?> current = CompletableFuture.completedFuture(null);
    private final Executor executor;

    public MessageChainTaskQueue(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void append(FutureQueue.FutureSupplier futureSupplier) {
        this.current = ((CompletableFuture)this.current.thenComposeAsync(void_ -> (CompletionStage)futureSupplier.get(), this.executor)).exceptionally(throwable -> {
            if (throwable instanceof CompletionException) {
                CompletionException completionException = (CompletionException)throwable;
                throwable = completionException.getCause();
            }
            if (throwable instanceof CancellationException) {
                CancellationException cancellationException = (CancellationException)throwable;
                throw cancellationException;
            }
            field_39828.error("Chain link failed, continuing to next one", throwable);
            return null;
        });
    }
}

