/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.RegionBasedStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StorageIoWorker
implements NbtScannable,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();

    protected StorageIoWorker(Path directory, boolean dsync, String name) {
        this.storage = new RegionBasedStorage(directory, dsync);
        this.executor = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
        return this.run(() -> {
            Result result = this.results.computeIfAbsent(pos, chunkPos -> new Result(nbt));
            result.nbt = nbt;
            return Either.left(result.future);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public NbtCompound getNbt(ChunkPos pos) throws IOException {
        CompletableFuture<NbtCompound> completableFuture = this.readChunkData(pos);
        try {
            return completableFuture.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    protected CompletableFuture<NbtCompound> readChunkData(ChunkPos pos) {
        return this.run(() -> {
            Result result = this.results.get(pos);
            if (result != null) {
                return Either.left((Object)result.nbt);
            }
            try {
                NbtCompound nbtCompound = this.storage.getTagAt(pos);
                return Either.left((Object)nbtCompound);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)pos, (Object)exception);
                return Either.right((Object)exception);
            }
        });
    }

    public CompletableFuture<Void> completeAll(boolean sync) {
        CompletionStage completableFuture = this.run(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new)))).thenCompose(Function.identity());
        if (sync) {
            return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> {
                try {
                    this.storage.sync();
                    return Either.left(null);
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to synchronize chunks", (Throwable)exception);
                    return Either.right((Object)exception);
                }
            }));
        }
        return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> Either.left(null)));
    }

    @Override
    public CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner) {
        return this.run(() -> {
            try {
                Result result = this.results.get(pos);
                if (result != null) {
                    if (result.nbt != null) {
                        result.nbt.accept(scanner);
                    }
                } else {
                    this.storage.method_39802(pos, scanner);
                }
                return Either.left(null);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to bulk scan chunk {}", (Object)pos, (Object)exception);
                return Either.right((Object)exception);
            }
        });
    }

    private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task) {
        return this.executor.askFallible(messageListener -> new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> this.method_27939(messageListener, (Supplier)task)));
    }

    private void writeResult() {
        if (this.results.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        this.writeRemainingResults();
    }

    private void writeRemainingResults() {
        this.executor.send(new TaskQueue.PrioritizedTask(Priority.BACKGROUND.ordinal(), this::writeResult));
    }

    private void write(ChunkPos pos, Result result) {
        try {
            this.storage.write(pos, result.nbt);
            result.future.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)pos, (Object)exception);
            result.future.completeExceptionally(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        this.executor.ask(messageListener -> new TaskQueue.PrioritizedTask(Priority.SHUTDOWN.ordinal(), () -> messageListener.send(Unit.INSTANCE))).join();
        this.executor.close();
        try {
            this.storage.close();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
        }
    }

    private /* synthetic */ void method_27939(MessageListener messageListener, Supplier supplier) {
        if (!this.closed.get()) {
            messageListener.send((Either)supplier.get());
        }
        this.writeRemainingResults();
    }

    static final class Priority
    extends Enum<Priority> {
        public static final /* enum */ Priority FOREGROUND = new Priority();
        public static final /* enum */ Priority BACKGROUND = new Priority();
        public static final /* enum */ Priority SHUTDOWN = new Priority();
        private static final /* synthetic */ Priority[] field_24471;

        public static Priority[] values() {
            return (Priority[])field_24471.clone();
        }

        public static Priority valueOf(String string) {
            return Enum.valueOf(Priority.class, string);
        }

        private static /* synthetic */ Priority[] method_36744() {
            return new Priority[]{FOREGROUND, BACKGROUND, SHUTDOWN};
        }

        static {
            field_24471 = Priority.method_36744();
        }
    }

    static class Result {
        @Nullable
        NbtCompound nbt;
        final CompletableFuture<Void> future = new CompletableFuture();

        public Result(@Nullable NbtCompound nbt) {
            this.nbt = nbt;
        }
    }
}

