/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfiledResourceReload
extends SimpleResourceReload<Summary> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

    public ProfiledResourceReload(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
        super(prepareExecutor, applyExecutor, manager, reloaders, (synchronizer, resourceManager, reloader, prepare, apply) -> {
            AtomicLong atomicLong = new AtomicLong();
            AtomicLong atomicLong2 = new AtomicLong();
            ProfilerSystem profilerSystem = new ProfilerSystem(Util.nanoTimeSupplier, () -> 0, false);
            ProfilerSystem profilerSystem2 = new ProfilerSystem(Util.nanoTimeSupplier, () -> 0, false);
            CompletableFuture<Void> completableFuture = reloader.reload(synchronizer, resourceManager, profilerSystem, profilerSystem2, runnable -> prepare.execute(() -> {
                long l = Util.getMeasuringTimeNano();
                runnable.run();
                atomicLong.addAndGet(Util.getMeasuringTimeNano() - l);
            }), runnable -> apply.execute(() -> {
                long l = Util.getMeasuringTimeNano();
                runnable.run();
                atomicLong2.addAndGet(Util.getMeasuringTimeNano() - l);
            }));
            return completableFuture.thenApplyAsync(void_ -> new Summary(reloader.getName(), profilerSystem.getResult(), profilerSystem2.getResult(), atomicLong, atomicLong2), applyExecutor);
        }, initialStage);
        this.reloadTimer.start();
        this.applyStageFuture.thenAcceptAsync(this::finish, applyExecutor);
    }

    private void finish(List<Summary> summaries) {
        this.reloadTimer.stop();
        int i = 0;
        LOGGER.info("Resource reload finished after " + this.reloadTimer.elapsed(TimeUnit.MILLISECONDS) + " ms");
        for (Summary summary : summaries) {
            ProfileResult profileResult = summary.prepareProfile;
            ProfileResult profileResult2 = summary.applyProfile;
            int j = (int)((double)summary.prepareTimeMs.get() / 1000000.0);
            int k = (int)((double)summary.applyTimeMs.get() / 1000000.0);
            int l = j + k;
            String string = summary.name;
            LOGGER.info(string + " took approximately " + l + " ms (" + j + " ms preparing, " + k + " ms applying)");
            i += k;
        }
        LOGGER.info("Total blocking time: " + i + " ms");
    }

    public static class Summary {
        private final String name;
        private final ProfileResult prepareProfile;
        private final ProfileResult applyProfile;
        private final AtomicLong prepareTimeMs;
        private final AtomicLong applyTimeMs;

        private Summary(String name, ProfileResult prepareProfile, ProfileResult applyProfile, AtomicLong prepareTimeMs, AtomicLong applyTimeMs) {
            this.name = name;
            this.prepareProfile = prepareProfile;
            this.applyProfile = applyProfile;
            this.prepareTimeMs = prepareTimeMs;
            this.applyTimeMs = applyTimeMs;
        }
    }
}
