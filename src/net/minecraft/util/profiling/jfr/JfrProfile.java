/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.profiling.jfr.JfrJsonReport;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public record JfrProfile(Instant startTime, Instant endTime, Duration duration, @Nullable Duration worldGenDuration, List<ServerTickTimeSample> serverTickTimeSamples, List<CpuLoadSample> cpuLoadSamples, GcHeapSummarySample.Statistics gcHeapSummaryStatistics, ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap, NetworkIoStatistics packetReadStatistics, NetworkIoStatistics packetSentStatistics, FileIoSample.Statistics fileWriteStatistics, FileIoSample.Statistics fileReadStatistics, List<ChunkGenerationSample> chunkGenerationSamples) {
    public List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> getChunkGenerationSampleStatistics() {
        Map<ChunkStatus, List<ChunkGenerationSample>> map = this.chunkGenerationSamples.stream().collect(Collectors.groupingBy(ChunkGenerationSample::chunkStatus));
        return map.entrySet().stream().map(entry -> Pair.of((Object)((ChunkStatus)entry.getKey()), LongRunningSampleStatistics.fromSamples((List)entry.getValue()))).sorted(Comparator.comparing(pair -> ((LongRunningSampleStatistics)pair.getSecond()).totalDuration()).reversed()).toList();
    }

    public String toJson() {
        return new JfrJsonReport().toString(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{JfrProfile.class, "recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats", "startTime", "endTime", "duration", "worldGenDuration", "serverTickTimeSamples", "cpuLoadSamples", "gcHeapSummaryStatistics", "threadAllocationMap", "packetReadStatistics", "packetSentStatistics", "fileWriteStatistics", "fileReadStatistics", "chunkGenerationSamples"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{JfrProfile.class, "recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats", "startTime", "endTime", "duration", "worldGenDuration", "serverTickTimeSamples", "cpuLoadSamples", "gcHeapSummaryStatistics", "threadAllocationMap", "packetReadStatistics", "packetSentStatistics", "fileWriteStatistics", "fileReadStatistics", "chunkGenerationSamples"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{JfrProfile.class, "recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats", "startTime", "endTime", "duration", "worldGenDuration", "serverTickTimeSamples", "cpuLoadSamples", "gcHeapSummaryStatistics", "threadAllocationMap", "packetReadStatistics", "packetSentStatistics", "fileWriteStatistics", "fileReadStatistics", "chunkGenerationSamples"}, this, o);
    }
}

