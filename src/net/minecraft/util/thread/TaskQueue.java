/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;

public interface TaskQueue<T, F> {
    @Nullable
    public F poll();

    public boolean add(T var1);

    public boolean isEmpty();

    public static final class PrioritizedQueueMailbox
    implements TaskQueue<PrioritizedMessage, Runnable> {
        private final List<Queue<Runnable>> queues;

        public PrioritizedQueueMailbox(int priorityCount) {
            this.queues = IntStream.range(0, priorityCount).mapToObj(i -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }

        @Override
        @Nullable
        public Runnable poll() {
            for (Queue<Runnable> queue : this.queues) {
                Runnable runnable = queue.poll();
                if (runnable == null) continue;
                return runnable;
            }
            return null;
        }

        @Override
        public boolean add(PrioritizedMessage prioritizedMessage) {
            int i = prioritizedMessage.getPriority();
            this.queues.get(i).add(prioritizedMessage);
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.queues.stream().allMatch(Collection::isEmpty);
        }

        @Override
        @Nullable
        public /* synthetic */ Object poll() {
            return this.poll();
        }
    }

    public static final class PrioritizedMessage
    implements Runnable {
        private final int priority;
        private final Runnable runnable;

        public PrioritizedMessage(int priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            this.runnable.run();
        }

        public int getPriority() {
            return this.priority;
        }
    }

    public static final class QueueMailbox<T>
    implements TaskQueue<T, T> {
        private final Queue<T> queue;

        public QueueMailbox(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        @Nullable
        public T poll() {
            return this.queue.poll();
        }

        @Override
        public boolean add(T message) {
            return this.queue.add(message);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }
}

