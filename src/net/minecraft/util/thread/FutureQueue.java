/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.slf4j.Logger;

@FunctionalInterface
public interface FutureQueue {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FutureQueue NOOP = future -> ((CompletableFuture)future.get()).exceptionally(throwable -> {
        LOGGER.error("Task failed", throwable);
        return null;
    });

    public void append(FutureSupplier var1);

    public static interface FutureSupplier
    extends Supplier<CompletableFuture<?>> {
    }
}

