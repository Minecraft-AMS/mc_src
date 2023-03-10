/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Task<E extends LivingEntity> {
    private static final int DEFAULT_RUN_TIME = 60;
    protected final Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryStates;
    private Status status = Status.STOPPED;
    private long endTime;
    private final int minRunTime;
    private final int maxRunTime;

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
        this(requiredMemoryState, 60);
    }

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int runTime) {
        this(requiredMemoryState, runTime, runTime);
    }

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime, int maxRunTime) {
        this.minRunTime = minRunTime;
        this.maxRunTime = maxRunTime;
        this.requiredMemoryStates = requiredMemoryState;
    }

    public Status getStatus() {
        return this.status;
    }

    public final boolean tryStarting(ServerWorld world, E entity, long time) {
        if (this.hasRequiredMemoryState(entity) && this.shouldRun(world, entity)) {
            this.status = Status.RUNNING;
            int i = this.minRunTime + world.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
            this.endTime = time + (long)i;
            this.run(world, entity, time);
            return true;
        }
        return false;
    }

    protected void run(ServerWorld world, E entity, long time) {
    }

    public final void tick(ServerWorld world, E entity, long time) {
        if (!this.isTimeLimitExceeded(time) && this.shouldKeepRunning(world, entity, time)) {
            this.keepRunning(world, entity, time);
        } else {
            this.stop(world, entity, time);
        }
    }

    protected void keepRunning(ServerWorld world, E entity, long time) {
    }

    public final void stop(ServerWorld world, E entity, long time) {
        this.status = Status.STOPPED;
        this.finishRunning(world, entity, time);
    }

    protected void finishRunning(ServerWorld world, E entity, long time) {
    }

    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return false;
    }

    protected boolean isTimeLimitExceeded(long time) {
        return time > this.endTime;
    }

    protected boolean shouldRun(ServerWorld world, E entity) {
        return true;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private boolean hasRequiredMemoryState(E entity) {
        for (Map.Entry<MemoryModuleType<?>, MemoryModuleState> entry : this.requiredMemoryStates.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            MemoryModuleState memoryModuleState = entry.getValue();
            if (((LivingEntity)entity).getBrain().isMemoryInState(memoryModuleType, memoryModuleState)) continue;
            return false;
        }
        return true;
    }

    public static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status STOPPED = new Status();
        public static final /* enum */ Status RUNNING = new Status();
        private static final /* synthetic */ Status[] field_18339;

        public static Status[] values() {
            return (Status[])field_18339.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static /* synthetic */ Status[] method_36615() {
            return new Status[]{STOPPED, RUNNING};
        }

        static {
            field_18339 = Status.method_36615();
        }
    }
}

