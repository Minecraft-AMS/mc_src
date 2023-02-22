/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Streams
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class WardenAngerManager {
    @VisibleForTesting
    protected static final int field_38733 = 2;
    @VisibleForTesting
    protected static final int maxAnger = 150;
    private static final int angerDecreasePerTick = 1;
    private int updateTimer = MathHelper.nextBetween(Random.create(), 0, 2);
    int primeAnger;
    private static final Codec<Pair<UUID, Integer>> SUSPECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(Pair::getFirst), (App)Codecs.NONNEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of));
    private final Predicate<Entity> suspectPredicate;
    @VisibleForTesting
    protected final ArrayList<Entity> suspects;
    private final SuspectComparator suspectComparator;
    @VisibleForTesting
    protected final Object2IntMap<Entity> suspectsToAngerLevel;
    @VisibleForTesting
    protected final Object2IntMap<UUID> suspectUuidsToAngerLevel;

    public static Codec<WardenAngerManager> createCodec(Predicate<Entity> suspectPredicate) {
        return RecordCodecBuilder.create(instance -> instance.group((App)SUSPECT_CODEC.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(WardenAngerManager::getSuspects)).apply((Applicative)instance, suspectUuidsToAngerLevel -> new WardenAngerManager(suspectPredicate, (List<Pair<UUID, Integer>>)suspectUuidsToAngerLevel)));
    }

    public WardenAngerManager(Predicate<Entity> suspectPredicate, List<Pair<UUID, Integer>> suspectUuidsToAngerLevel) {
        this.suspectPredicate = suspectPredicate;
        this.suspects = new ArrayList();
        this.suspectComparator = new SuspectComparator(this);
        this.suspectsToAngerLevel = new Object2IntOpenHashMap();
        this.suspectUuidsToAngerLevel = new Object2IntOpenHashMap(suspectUuidsToAngerLevel.size());
        suspectUuidsToAngerLevel.forEach(suspect -> this.suspectUuidsToAngerLevel.put((Object)((UUID)suspect.getFirst()), (Integer)suspect.getSecond()));
    }

    private List<Pair<UUID, Integer>> getSuspects() {
        return Streams.concat((Stream[])new Stream[]{this.suspects.stream().map(suspect -> Pair.of((Object)suspect.getUuid(), (Object)this.suspectsToAngerLevel.getInt(suspect))), this.suspectUuidsToAngerLevel.object2IntEntrySet().stream().map(suspect -> Pair.of((Object)((UUID)suspect.getKey()), (Object)suspect.getIntValue()))}).collect(Collectors.toList());
    }

    public void tick(ServerWorld world, Predicate<Entity> suspectPredicate) {
        --this.updateTimer;
        if (this.updateTimer <= 0) {
            this.updateSuspectsMap(world);
            this.updateTimer = 2;
        }
        ObjectIterator objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
            int i = entry.getIntValue();
            if (i <= 1) {
                objectIterator.remove();
                continue;
            }
            entry.setValue(i - 1);
        }
        ObjectIterator objectIterator2 = this.suspectsToAngerLevel.object2IntEntrySet().iterator();
        while (objectIterator2.hasNext()) {
            Object2IntMap.Entry entry2 = (Object2IntMap.Entry)objectIterator2.next();
            int j = entry2.getIntValue();
            Entity entity = (Entity)entry2.getKey();
            Entity.RemovalReason removalReason = entity.getRemovalReason();
            if (j <= 1 || !suspectPredicate.test(entity) || removalReason != null) {
                this.suspects.remove(entity);
                objectIterator2.remove();
                if (j <= 1 || removalReason == null) continue;
                switch (removalReason) {
                    case CHANGED_DIMENSION: 
                    case UNLOADED_TO_CHUNK: 
                    case UNLOADED_WITH_PLAYER: {
                        this.suspectUuidsToAngerLevel.put((Object)entity.getUuid(), j - 1);
                    }
                }
                continue;
            }
            entry2.setValue(j - 1);
        }
        this.updatePrimeAnger();
    }

    private void updatePrimeAnger() {
        this.primeAnger = 0;
        this.suspects.sort(this.suspectComparator);
        if (this.suspects.size() == 1) {
            this.primeAnger = this.suspectsToAngerLevel.getInt((Object)this.suspects.get(0));
        }
    }

    private void updateSuspectsMap(ServerWorld world) {
        ObjectIterator objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
            int i = entry.getIntValue();
            Entity entity = world.getEntity((UUID)entry.getKey());
            if (entity == null) continue;
            this.suspectsToAngerLevel.put((Object)entity, i);
            this.suspects.add(entity);
            objectIterator.remove();
        }
    }

    public int increaseAngerAt(Entity entity, int amount) {
        boolean bl = !this.suspectsToAngerLevel.containsKey((Object)entity);
        int i = this.suspectsToAngerLevel.computeInt((Object)entity, (suspect, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
        if (bl) {
            int j = this.suspectUuidsToAngerLevel.removeInt((Object)entity.getUuid());
            this.suspectsToAngerLevel.put((Object)entity, i += j);
            this.suspects.add(entity);
        }
        this.updatePrimeAnger();
        return i;
    }

    public void removeSuspect(Entity entity) {
        this.suspectsToAngerLevel.removeInt((Object)entity);
        this.suspects.remove(entity);
        this.updatePrimeAnger();
    }

    @Nullable
    private Entity getPrimeSuspectInternal() {
        return this.suspects.stream().filter(this.suspectPredicate).findFirst().orElse(null);
    }

    public int getAngerFor(@Nullable Entity entity) {
        return entity == null ? this.primeAnger : this.suspectsToAngerLevel.getInt((Object)entity);
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return Optional.ofNullable(this.getPrimeSuspectInternal()).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity)suspect);
    }

    @VisibleForTesting
    protected record SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity>
    {
        @Override
        public int compare(Entity entity, Entity entity2) {
            boolean bl2;
            if (entity.equals(entity2)) {
                return 0;
            }
            int i = this.angerManagement.suspectsToAngerLevel.getOrDefault((Object)entity, 0);
            int j = this.angerManagement.suspectsToAngerLevel.getOrDefault((Object)entity2, 0);
            this.angerManagement.primeAnger = Math.max(this.angerManagement.primeAnger, Math.max(i, j));
            boolean bl = Angriness.getForAnger(i).isAngry();
            if (bl != (bl2 = Angriness.getForAnger(j).isAngry())) {
                return bl ? -1 : 1;
            }
            boolean bl3 = entity instanceof PlayerEntity;
            boolean bl4 = entity2 instanceof PlayerEntity;
            if (bl3 != bl4) {
                return bl3 ? -1 : 1;
            }
            return Integer.compare(j, i);
        }

        @Override
        public /* synthetic */ int compare(Object first, Object second) {
            return this.compare((Entity)first, (Entity)second);
        }
    }
}

