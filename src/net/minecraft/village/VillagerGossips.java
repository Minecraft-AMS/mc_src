/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.village.VillageGossipType;

public class VillagerGossips {
    private final Map<UUID, Reputation> entityReputation = Maps.newHashMap();

    public void decay() {
        Iterator<Reputation> iterator = this.entityReputation.values().iterator();
        while (iterator.hasNext()) {
            Reputation reputation = iterator.next();
            reputation.decay();
            if (!reputation.isObsolete()) continue;
            iterator.remove();
        }
    }

    private Stream<GossipEntry> entries() {
        return this.entityReputation.entrySet().stream().flatMap(entry -> ((Reputation)entry.getValue()).entriesFor((UUID)entry.getKey()));
    }

    private Collection<GossipEntry> pickGossips(Random random, int count) {
        List list = this.entries().collect(Collectors.toList());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        int[] is = new int[list.size()];
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            GossipEntry gossipEntry = (GossipEntry)list.get(j);
            is[j] = (i += Math.abs(gossipEntry.getValue())) - 1;
        }
        Set set = Sets.newIdentityHashSet();
        for (int k = 0; k < count; ++k) {
            int l = random.nextInt(i);
            int m = Arrays.binarySearch(is, l);
            set.add(list.get(m < 0 ? -m - 1 : m));
        }
        return set;
    }

    private Reputation getReputationFor(UUID target) {
        return this.entityReputation.computeIfAbsent(target, uUID -> new Reputation());
    }

    public void shareGossipFrom(VillagerGossips from, Random random, int count) {
        Collection<GossipEntry> collection = from.pickGossips(random, count);
        collection.forEach(gossipEntry -> {
            int i = gossipEntry.value - gossipEntry.type.shareDecrement;
            if (i >= 2) {
                this.getReputationFor(gossipEntry.target).associatedGossip.mergeInt((Object)gossipEntry.type, i, VillagerGossips::max);
            }
        });
    }

    public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
        Reputation reputation = this.entityReputation.get(target);
        return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
    }

    public void startGossip(UUID target, VillageGossipType type, int value) {
        Reputation reputation = this.getReputationFor(target);
        reputation.associatedGossip.mergeInt((Object)type, value, (integer, integer2) -> this.mergeReputation(type, (int)integer, (int)integer2));
        reputation.clamp(type);
        if (reputation.isObsolete()) {
            this.entityReputation.remove(target);
        }
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createList(this.entries().map(gossipEntry -> gossipEntry.serialize(ops)).map(Dynamic::getValue)));
    }

    public void deserialize(Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::deserialize).flatMap(Util::stream).forEach(gossipEntry -> this.getReputationFor(gossipEntry.target).associatedGossip.put((Object)gossipEntry.type, gossipEntry.value));
    }

    private static int max(int left, int right) {
        return Math.max(left, right);
    }

    private int mergeReputation(VillageGossipType type, int left, int right) {
        int i = left + right;
        return i > type.maxValue ? Math.max(type.maxValue, left) : i;
    }

    static class Reputation {
        private final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap();

        private Reputation() {
        }

        public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
            return this.associatedGossip.object2IntEntrySet().stream().filter(entry -> gossipTypeFilter.test((VillageGossipType)((Object)entry.getKey()))).mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)((Object)((Object)entry.getKey()))).multiplier).sum();
        }

        public Stream<GossipEntry> entriesFor(UUID target) {
            return this.associatedGossip.object2IntEntrySet().stream().map(entry -> new GossipEntry(target, (VillageGossipType)((Object)((Object)entry.getKey())), entry.getIntValue()));
        }

        public void decay() {
            ObjectIterator objectIterator = this.associatedGossip.object2IntEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
                int i = entry.getIntValue() - ((VillageGossipType)((Object)entry.getKey())).decay;
                if (i < 2) {
                    objectIterator.remove();
                    continue;
                }
                entry.setValue(i);
            }
        }

        public boolean isObsolete() {
            return this.associatedGossip.isEmpty();
        }

        public void clamp(VillageGossipType gossipType) {
            int i = this.associatedGossip.getInt((Object)gossipType);
            if (i > gossipType.maxValue) {
                this.associatedGossip.put((Object)gossipType, gossipType.maxValue);
            }
            if (i < 2) {
                this.remove(gossipType);
            }
        }

        public void remove(VillageGossipType gossipType) {
            this.associatedGossip.removeInt((Object)gossipType);
        }
    }

    static class GossipEntry {
        public final UUID target;
        public final VillageGossipType type;
        public final int value;

        public GossipEntry(UUID target, VillageGossipType type, int value) {
            this.target = target;
            this.type = type;
            this.value = value;
        }

        public int getValue() {
            return this.value * this.type.multiplier;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + (Object)((Object)this.type) + ", value=" + this.value + '}';
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
            return Util.writeUuid("Target", this.target, new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("Type"), (Object)ops.createString(this.type.key), (Object)ops.createString("Value"), (Object)ops.createInt(this.value)))));
        }

        public static Optional<GossipEntry> deserialize(Dynamic<?> dynamic) {
            return dynamic.get("Type").asString().map(VillageGossipType::byKey).flatMap(villageGossipType -> Util.readUuid("Target", dynamic).flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new GossipEntry((UUID)uUID, (VillageGossipType)((Object)villageGossipType), number.intValue()))));
        }
    }
}
