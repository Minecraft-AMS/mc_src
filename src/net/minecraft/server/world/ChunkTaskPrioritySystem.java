/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem
implements AutoCloseable,
ChunkHolder.LevelUpdateListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>> queues;
    private final Set<MessageListener<?>> actors;
    private final TaskExecutor<TaskQueue.PrioritizedMessage> sorter;

    public ChunkTaskPrioritySystem(List<MessageListener<?>> actors, Executor executor, int maxQueues) {
        this.queues = actors.stream().collect(Collectors.toMap(Function.identity(), messageListener -> new LevelPrioritizedQueue(messageListener.getName() + "_queue", maxQueues)));
        this.actors = Sets.newHashSet(actors);
        this.sorter = new TaskExecutor<TaskQueue.PrioritizedMessage>(new TaskQueue.PrioritizedQueueMailbox(4), executor, "sorter");
    }

    public static RunnableMessage<Runnable> createMessage(Runnable runnable, long pos, IntSupplier lastLevelUpdatedToProvider) {
        return new RunnableMessage<Runnable>(messageListener -> () -> {
            runnable.run();
            messageListener.send(Unit.INSTANCE);
        }, pos, lastLevelUpdatedToProvider);
    }

    public static RunnableMessage<Runnable> createMessage(ChunkHolder holder, Runnable runnable) {
        return ChunkTaskPrioritySystem.createMessage(runnable, holder.getPos().toLong(), holder::getCompletedLevel);
    }

    public static SorterMessage createSorterMessage(Runnable runnable, long pos, boolean bl) {
        return new SorterMessage(runnable, pos, bl);
    }

    public <T> MessageListener<RunnableMessage<T>> createExecutor(MessageListener<T> executor, boolean bl) {
        return (MessageListener)this.sorter.ask(messageListener2 -> new TaskQueue.PrioritizedMessage(0, () -> {
            this.getQueue(executor);
            messageListener2.send(MessageListener.create("chunk priority sorter around " + executor.getName(), runnableMessage -> this.execute(executor, ((RunnableMessage)runnableMessage).function, ((RunnableMessage)runnableMessage).pos, ((RunnableMessage)runnableMessage).lastLevelUpdatedToProvider, bl)));
        })).join();
    }

    public MessageListener<SorterMessage> createSorterExecutor(MessageListener<Runnable> executor) {
        return (MessageListener)this.sorter.ask(messageListener2 -> new TaskQueue.PrioritizedMessage(0, () -> messageListener2.send(MessageListener.create("chunk priority sorter around " + executor.getName(), sorterMessage -> this.sort(executor, ((SorterMessage)sorterMessage).pos, ((SorterMessage)sorterMessage).runnable, ((SorterMessage)sorterMessage).field_17451))))).join();
    }

    @Override
    public void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter) {
        this.sorter.send(new TaskQueue.PrioritizedMessage(0, () -> {
            int j = levelGetter.getAsInt();
            this.queues.values().forEach(levelPrioritizedQueue -> levelPrioritizedQueue.updateLevel(j, pos, targetLevel));
            levelSetter.accept(targetLevel);
        }));
    }

    private <T> void sort(MessageListener<T> messageListener, long l, Runnable runnable, boolean bl) {
        this.sorter.send(new TaskQueue.PrioritizedMessage(1, () -> {
            LevelPrioritizedQueue levelPrioritizedQueue = this.getQueue(messageListener);
            levelPrioritizedQueue.clearPosition(l, bl);
            if (this.actors.remove(messageListener)) {
                this.method_17630(levelPrioritizedQueue, messageListener);
            }
            runnable.run();
        }));
    }

    private <T> void execute(MessageListener<T> actor, Function<MessageListener<Unit>, T> function, long l, IntSupplier lastLevelUpdatedToProvider, boolean bl) {
        this.sorter.send(new TaskQueue.PrioritizedMessage(2, () -> {
            LevelPrioritizedQueue levelPrioritizedQueue = this.getQueue(actor);
            int i = lastLevelUpdatedToProvider.getAsInt();
            levelPrioritizedQueue.add(Optional.of(function), l, i);
            if (bl) {
                levelPrioritizedQueue.add(Optional.empty(), l, i);
            }
            if (this.actors.remove(actor)) {
                this.method_17630(levelPrioritizedQueue, actor);
            }
        }));
    }

    private <T> void method_17630(LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue, MessageListener<T> actor) {
        this.sorter.send(new TaskQueue.PrioritizedMessage(3, () -> {
            Stream<Either<Either, Runnable>> stream = levelPrioritizedQueue.poll();
            if (stream == null) {
                this.actors.add(actor);
            } else {
                Util.combine(stream.map(either -> (CompletableFuture)either.map(actor::ask, runnable -> {
                    runnable.run();
                    return CompletableFuture.completedFuture(Unit.INSTANCE);
                })).collect(Collectors.toList())).thenAccept(list -> this.method_17630(levelPrioritizedQueue, actor));
            }
        }));
    }

    private <T> LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> getQueue(MessageListener<T> actor) {
        LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue = this.queues.get(actor);
        if (levelPrioritizedQueue == null) {
            throw new IllegalArgumentException("No queue for: " + actor);
        }
        return levelPrioritizedQueue;
    }

    @VisibleForTesting
    public String method_21680() {
        return this.queues.entrySet().stream().map(entry -> ((MessageListener)entry.getKey()).getName() + "=[" + ((LevelPrioritizedQueue)entry.getValue()).method_21679().stream().map(long_ -> long_ + ":" + new ChunkPos((long)long_)).collect(Collectors.joining(",")) + "]").collect(Collectors.joining(",")) + ", s=" + this.actors.size();
    }

    @Override
    public void close() {
        this.queues.keySet().forEach(MessageListener::close);
    }

    public static final class SorterMessage {
        private final Runnable runnable;
        private final long pos;
        private final boolean field_17451;

        private SorterMessage(Runnable runnable, long l, boolean bl) {
            this.runnable = runnable;
            this.pos = l;
            this.field_17451 = bl;
        }
    }

    public static final class RunnableMessage<T> {
        private final Function<MessageListener<Unit>, T> function;
        private final long pos;
        private final IntSupplier lastLevelUpdatedToProvider;

        private RunnableMessage(Function<MessageListener<Unit>, T> function, long pos, IntSupplier lastLevelUpdatedToProvider) {
            this.function = function;
            this.pos = pos;
            this.lastLevelUpdatedToProvider = lastLevelUpdatedToProvider;
        }
    }
}

