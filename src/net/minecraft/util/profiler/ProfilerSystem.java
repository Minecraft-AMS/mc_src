/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfileResultImpl;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem
implements ReadableProfiler {
    private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> path = Lists.newArrayList();
    private final LongList timeList = new LongArrayList();
    private final Object2LongMap<String> nameDurationMap = new Object2LongOpenHashMap();
    private final Object2LongMap<String> field_19381 = new Object2LongOpenHashMap();
    private final IntSupplier field_16266;
    private final long field_15732;
    private final int field_15729;
    private String location = "";
    private boolean tickStarted;

    public ProfilerSystem(long l, IntSupplier intSupplier) {
        this.field_15732 = l;
        this.field_15729 = intSupplier.getAsInt();
        this.field_16266 = intSupplier;
    }

    @Override
    public void startTick() {
        if (this.tickStarted) {
            LOGGER.error("Profiler tick already started - missing endTick()?");
            return;
        }
        this.tickStarted = true;
        this.location = "";
        this.path.clear();
        this.push("root");
    }

    @Override
    public void endTick() {
        if (!this.tickStarted) {
            LOGGER.error("Profiler tick already ended - missing startTick()?");
            return;
        }
        this.pop();
        this.tickStarted = false;
        if (!this.location.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new org.apache.logging.log4j.util.Supplier[]{() -> ProfileResult.method_21721(this.location)});
        }
    }

    @Override
    public void push(String location) {
        if (!this.tickStarted) {
            LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", (Object)location);
            return;
        }
        if (!this.location.isEmpty()) {
            this.location = this.location + '\u001e';
        }
        this.location = this.location + location;
        this.path.add(this.location);
        this.timeList.add(Util.getMeasuringTimeNano());
    }

    @Override
    public void push(Supplier<String> locationGetter) {
        this.push(locationGetter.get());
    }

    @Override
    public void pop() {
        if (!this.tickStarted) {
            LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
            return;
        }
        if (this.timeList.isEmpty()) {
            LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
            return;
        }
        long l = Util.getMeasuringTimeNano();
        long m = this.timeList.removeLong(this.timeList.size() - 1);
        this.path.remove(this.path.size() - 1);
        long n = l - m;
        this.nameDurationMap.put((Object)this.location, this.nameDurationMap.getLong((Object)this.location) + n);
        this.field_19381.put((Object)this.location, this.field_19381.getLong((Object)this.location) + 1L);
        if (n > TIMEOUT_NANOSECONDS) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new org.apache.logging.log4j.util.Supplier[]{() -> ProfileResult.method_21721(this.location), () -> (double)n / 1000000.0});
        }
        this.location = this.path.isEmpty() ? "" : this.path.get(this.path.size() - 1);
    }

    @Override
    public void swap(String location) {
        this.pop();
        this.push(location);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> locationGetter) {
        this.pop();
        this.push(locationGetter);
    }

    @Override
    public ProfileResult getResult() {
        return new ProfileResultImpl((Map<String, Long>)this.nameDurationMap, (Map<String, Long>)this.field_19381, this.field_15732, this.field_15729, Util.getMeasuringTimeNano(), this.field_16266.getAsInt());
    }
}

