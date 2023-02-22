/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.LongSerializationPolicy
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.util.profiling.jfr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.JfrProfile;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public class JfrJsonReport {
    private static final String BYTES_PER_SECOND = "bytesPerSecond";
    private static final String COUNT = "count";
    private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
    private static final String TOTAL_BYTES = "totalBytes";
    private static final String COUNT_PER_SECOND = "countPerSecond";
    final Gson gson = new GsonBuilder().setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();

    public String toString(JfrProfile profile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("startedEpoch", (Number)profile.startTime().toEpochMilli());
        jsonObject.addProperty("endedEpoch", (Number)profile.endTime().toEpochMilli());
        jsonObject.addProperty("durationMs", (Number)profile.duration().toMillis());
        Duration duration = profile.worldGenDuration();
        if (duration != null) {
            jsonObject.addProperty("worldGenDurationMs", (Number)duration.toMillis());
        }
        jsonObject.add("heap", this.collectHeapSection(profile.gcHeapSummaryStatistics()));
        jsonObject.add("cpuPercent", this.collectCpuPercentSection(profile.cpuLoadSamples()));
        jsonObject.add("network", this.collectNetworkSection(profile));
        jsonObject.add("fileIO", this.collectFileIoSection(profile));
        jsonObject.add("serverTick", this.collectServerTickSection(profile.serverTickTimeSamples()));
        jsonObject.add("threadAllocation", this.collectThreadAllocationSection(profile.threadAllocationMap()));
        jsonObject.add("chunkGen", this.collectChunkGenSection(profile.getChunkGenerationSampleStatistics()));
        return this.gson.toJson((JsonElement)jsonObject);
    }

    private JsonElement collectHeapSection(GcHeapSummarySample.Statistics statistics) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("allocationRateBytesPerSecond", (Number)statistics.allocatedBytesPerSecond());
        jsonObject.addProperty("gcCount", (Number)statistics.count());
        jsonObject.addProperty("gcOverHeadPercent", (Number)Float.valueOf(statistics.getGcDurationRatio()));
        jsonObject.addProperty("gcTotalDurationMs", (Number)statistics.gcDuration().toMillis());
        return jsonObject;
    }

    private JsonElement collectChunkGenSection(List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> statistics) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DURATION_NANOS_TOTAL, (Number)statistics.stream().mapToDouble(pair -> ((LongRunningSampleStatistics)pair.getSecond()).totalDuration().toNanos()).sum());
        JsonArray jsonArray = Util.make(new JsonArray(), json -> jsonObject.add("status", (JsonElement)json));
        for (Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>> pair2 : statistics) {
            LongRunningSampleStatistics longRunningSampleStatistics = (LongRunningSampleStatistics)pair2.getSecond();
            JsonObject jsonObject2 = Util.make(new JsonObject(), arg_0 -> ((JsonArray)jsonArray).add(arg_0));
            jsonObject2.addProperty("state", ((ChunkStatus)pair2.getFirst()).getId());
            jsonObject2.addProperty(COUNT, (Number)longRunningSampleStatistics.count());
            jsonObject2.addProperty(DURATION_NANOS_TOTAL, (Number)longRunningSampleStatistics.totalDuration().toNanos());
            jsonObject2.addProperty("durationNanosAvg", (Number)(longRunningSampleStatistics.totalDuration().toNanos() / (long)longRunningSampleStatistics.count()));
            JsonObject jsonObject3 = Util.make(new JsonObject(), json -> jsonObject2.add("durationNanosPercentiles", (JsonElement)json));
            longRunningSampleStatistics.quantiles().forEach((quantile, value) -> jsonObject3.addProperty("p" + quantile, (Number)value));
            Function<ChunkGenerationSample, JsonElement> function = sample -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("durationNanos", (Number)sample.duration().toNanos());
                jsonObject.addProperty("level", sample.worldKey());
                jsonObject.addProperty("chunkPosX", (Number)sample.chunkPos().x);
                jsonObject.addProperty("chunkPosZ", (Number)sample.chunkPos().z);
                jsonObject.addProperty("worldPosX", (Number)sample.centerPos().x());
                jsonObject.addProperty("worldPosZ", (Number)sample.centerPos().z());
                return jsonObject;
            };
            jsonObject2.add("fastest", function.apply((ChunkGenerationSample)longRunningSampleStatistics.fastestSample()));
            jsonObject2.add("slowest", function.apply((ChunkGenerationSample)longRunningSampleStatistics.slowestSample()));
            jsonObject2.add("secondSlowest", (JsonElement)(longRunningSampleStatistics.secondSlowestSample() != null ? function.apply((ChunkGenerationSample)longRunningSampleStatistics.secondSlowestSample()) : JsonNull.INSTANCE));
        }
        return jsonObject;
    }

    private JsonElement collectThreadAllocationSection(ThreadAllocationStatisticsSample.AllocationMap statistics) {
        JsonArray jsonArray = new JsonArray();
        statistics.allocations().forEach((threadName, allocation) -> jsonArray.add((JsonElement)Util.make(new JsonObject(), json -> {
            json.addProperty("thread", threadName);
            json.addProperty(BYTES_PER_SECOND, (Number)allocation);
        })));
        return jsonArray;
    }

    private JsonElement collectServerTickSection(List<ServerTickTimeSample> samples) {
        if (samples.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        double[] ds = samples.stream().mapToDouble(sample -> (double)sample.averageTickMs().toNanos() / 1000000.0).toArray();
        DoubleSummaryStatistics doubleSummaryStatistics = DoubleStream.of(ds).summaryStatistics();
        jsonObject.addProperty("minMs", (Number)doubleSummaryStatistics.getMin());
        jsonObject.addProperty("averageMs", (Number)doubleSummaryStatistics.getAverage());
        jsonObject.addProperty("maxMs", (Number)doubleSummaryStatistics.getMax());
        Map<Integer, Double> map = Quantiles.create(ds);
        map.forEach((quantile, value) -> jsonObject.addProperty("p" + quantile, (Number)value));
        return jsonObject;
    }

    private JsonElement collectFileIoSection(JfrProfile profile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("write", this.collectFileIoSection(profile.fileWriteStatistics()));
        jsonObject.add("read", this.collectFileIoSection(profile.fileReadStatistics()));
        return jsonObject;
    }

    private JsonElement collectFileIoSection(FileIoSample.Statistics statistics) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TOTAL_BYTES, (Number)statistics.totalBytes());
        jsonObject.addProperty(COUNT, (Number)statistics.count());
        jsonObject.addProperty(BYTES_PER_SECOND, (Number)statistics.bytesPerSecond());
        jsonObject.addProperty(COUNT_PER_SECOND, (Number)statistics.countPerSecond());
        JsonArray jsonArray = new JsonArray();
        jsonObject.add("topContributors", (JsonElement)jsonArray);
        statistics.topContributors().forEach(pair -> {
            JsonObject jsonObject = new JsonObject();
            jsonArray.add((JsonElement)jsonObject);
            jsonObject.addProperty("path", (String)pair.getFirst());
            jsonObject.addProperty(TOTAL_BYTES, (Number)pair.getSecond());
        });
        return jsonObject;
    }

    private JsonElement collectNetworkSection(JfrProfile profile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("sent", this.collectPacketSection(profile.packetSentStatistics()));
        jsonObject.add("received", this.collectPacketSection(profile.packetReadStatistics()));
        return jsonObject;
    }

    private JsonElement collectPacketSection(NetworkIoStatistics statistics) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TOTAL_BYTES, (Number)statistics.getTotalSize());
        jsonObject.addProperty(COUNT, (Number)statistics.getTotalCount());
        jsonObject.addProperty(BYTES_PER_SECOND, (Number)statistics.getBytesPerSecond());
        jsonObject.addProperty(COUNT_PER_SECOND, (Number)statistics.getCountPerSecond());
        JsonArray jsonArray = new JsonArray();
        jsonObject.add("topContributors", (JsonElement)jsonArray);
        statistics.getTopContributors().forEach(pair -> {
            JsonObject jsonObject = new JsonObject();
            jsonArray.add((JsonElement)jsonObject);
            NetworkIoStatistics.Packet packet = (NetworkIoStatistics.Packet)pair.getFirst();
            NetworkIoStatistics.PacketStatistics packetStatistics = (NetworkIoStatistics.PacketStatistics)pair.getSecond();
            jsonObject.addProperty("protocolId", (Number)packet.protocolId());
            jsonObject.addProperty("packetId", (Number)packet.packetId());
            jsonObject.addProperty("packetName", packet.getName());
            jsonObject.addProperty(TOTAL_BYTES, (Number)packetStatistics.totalSize());
            jsonObject.addProperty(COUNT, (Number)packetStatistics.totalCount());
        });
        return jsonObject;
    }

    private JsonElement collectCpuPercentSection(List<CpuLoadSample> samples2) {
        JsonObject jsonObject = new JsonObject();
        BiFunction<List, ToDoubleFunction, JsonObject> biFunction = (samples, valueGetter) -> {
            JsonObject jsonObject = new JsonObject();
            DoubleSummaryStatistics doubleSummaryStatistics = samples.stream().mapToDouble(valueGetter).summaryStatistics();
            jsonObject.addProperty("min", (Number)doubleSummaryStatistics.getMin());
            jsonObject.addProperty("average", (Number)doubleSummaryStatistics.getAverage());
            jsonObject.addProperty("max", (Number)doubleSummaryStatistics.getMax());
            return jsonObject;
        };
        jsonObject.add("jvm", (JsonElement)biFunction.apply(samples2, CpuLoadSample::jvm));
        jsonObject.add("userJvm", (JsonElement)biFunction.apply(samples2, CpuLoadSample::userJvm));
        jsonObject.add("system", (JsonElement)biFunction.apply(samples2, CpuLoadSample::system));
        return jsonObject;
    }
}

