/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 */
package net.minecraft.util.profiling.jfr.sample;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;

public final class NetworkIoStatistics {
    private final PacketStatistics combinedStatistics;
    private final List<Pair<Packet, PacketStatistics>> topContributors;
    private final Duration duration;

    public NetworkIoStatistics(Duration duration, List<Pair<Packet, PacketStatistics>> packetsToStatistics) {
        this.duration = duration;
        this.combinedStatistics = packetsToStatistics.stream().map(Pair::getSecond).reduce(PacketStatistics::add).orElseGet(() -> new PacketStatistics(0L, 0L));
        this.topContributors = packetsToStatistics.stream().sorted(Comparator.comparing(Pair::getSecond, PacketStatistics.COMPARATOR)).limit(10L).toList();
    }

    public double getCountPerSecond() {
        return (double)this.combinedStatistics.totalCount / (double)this.duration.getSeconds();
    }

    public double getBytesPerSecond() {
        return (double)this.combinedStatistics.totalSize / (double)this.duration.getSeconds();
    }

    public long getTotalCount() {
        return this.combinedStatistics.totalCount;
    }

    public long getTotalSize() {
        return this.combinedStatistics.totalSize;
    }

    public List<Pair<Packet, PacketStatistics>> getTopContributors() {
        return this.topContributors;
    }

    public static final class PacketStatistics
    extends Record {
        final long totalCount;
        final long totalSize;
        static final Comparator<PacketStatistics> COMPARATOR = Comparator.comparing(PacketStatistics::totalSize).thenComparing(PacketStatistics::totalCount).reversed();

        public PacketStatistics(long l, long m) {
            this.totalCount = l;
            this.totalSize = m;
        }

        PacketStatistics add(PacketStatistics statistics) {
            return new PacketStatistics(this.totalCount + statistics.totalCount, this.totalSize + statistics.totalSize);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PacketStatistics.class, "totalCount;totalSize", "totalCount", "totalSize"}, this, object);
        }

        public long totalCount() {
            return this.totalCount;
        }

        public long totalSize() {
            return this.totalSize;
        }
    }

    public record Packet(NetworkSide side, int protocolId, int packetId) {
        private static final Map<Packet, String> PACKET_TO_NAME;

        public String getName() {
            return PACKET_TO_NAME.getOrDefault(this, "unknown");
        }

        public static Packet fromEvent(RecordedEvent event) {
            return new Packet(event.getEventType().getName().equals("minecraft.PacketSent") ? NetworkSide.CLIENTBOUND : NetworkSide.SERVERBOUND, event.getInt("protocolId"), event.getInt("packetId"));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Packet.class, "direction;protocolId;packetId", "side", "protocolId", "packetId"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Packet.class, "direction;protocolId;packetId", "side", "protocolId", "packetId"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Packet.class, "direction;protocolId;packetId", "side", "protocolId", "packetId"}, this, object);
        }

        static {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (NetworkState networkState : NetworkState.values()) {
                for (NetworkSide networkSide : NetworkSide.values()) {
                    Int2ObjectMap<Class<? extends net.minecraft.network.Packet<?>>> int2ObjectMap = networkState.getPacketIdToPacketMap(networkSide);
                    int2ObjectMap.forEach((packetId, clazz) -> builder.put((Object)new Packet(networkSide, networkState.getId(), (int)packetId), (Object)clazz.getSimpleName()));
                }
            }
            PACKET_TO_NAME = builder.build();
        }
    }
}

