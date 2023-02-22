/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  org.slf4j.Logger
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.slf4j.Logger;

public class PointOfInterestSet {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap();
    private final Map<RegistryEntry<PointOfInterestType>, Set<PointOfInterest>> pointsOfInterestByType = Maps.newHashMap();
    private final Runnable updateListener;
    private boolean valid;

    public static Codec<PointOfInterestSet> createCodec(Runnable updateListener) {
        return RecordCodecBuilder.create(instance -> instance.group((App)RecordCodecBuilder.point((Object)updateListener), (App)Codec.BOOL.optionalFieldOf("Valid", (Object)false).forGetter(poiSet -> poiSet.valid), (App)PointOfInterest.createCodec(updateListener).listOf().fieldOf("Records").forGetter(poiSet -> ImmutableList.copyOf((Collection)poiSet.pointsOfInterestByPos.values()))).apply((Applicative)instance, PointOfInterestSet::new)).orElseGet(Util.addPrefix("Failed to read POI section: ", arg_0 -> ((Logger)LOGGER).error(arg_0)), () -> new PointOfInterestSet(updateListener, false, (List<PointOfInterest>)ImmutableList.of()));
    }

    public PointOfInterestSet(Runnable updateListener) {
        this(updateListener, true, (List<PointOfInterest>)ImmutableList.of());
    }

    private PointOfInterestSet(Runnable updateListener, boolean valid, List<PointOfInterest> pois) {
        this.updateListener = updateListener;
        this.valid = valid;
        pois.forEach(this::add);
    }

    public Stream<PointOfInterest> get(Predicate<RegistryEntry<PointOfInterestType>> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
        return this.pointsOfInterestByType.entrySet().stream().filter(entry -> predicate.test((RegistryEntry)entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter(occupationStatus.getPredicate());
    }

    public void add(BlockPos pos, RegistryEntry<PointOfInterestType> registryEntry) {
        if (this.add(new PointOfInterest(pos, registryEntry, this.updateListener))) {
            LOGGER.debug("Added POI of type {} @ {}", (Object)registryEntry.getKey().map(registryKey -> registryKey.getValue().toString()).orElse("[unregistered]"), (Object)pos);
            this.updateListener.run();
        }
    }

    private boolean add(PointOfInterest poi) {
        BlockPos blockPos = poi.getPos();
        RegistryEntry<PointOfInterestType> registryEntry2 = poi.getType();
        short s = ChunkSectionPos.packLocal(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        if (pointOfInterest != null) {
            if (registryEntry2.equals(pointOfInterest.getType())) {
                return false;
            }
            Util.error("POI data mismatch: already registered at " + blockPos);
        }
        this.pointsOfInterestByPos.put(s, (Object)poi);
        this.pointsOfInterestByType.computeIfAbsent(registryEntry2, registryEntry -> Sets.newHashSet()).add(poi);
        return true;
    }

    public void remove(BlockPos pos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.packLocal(pos));
        if (pointOfInterest == null) {
            LOGGER.error("POI data mismatch: never registered at {}", (Object)pos);
            return;
        }
        this.pointsOfInterestByType.get(pointOfInterest.getType()).remove(pointOfInterest);
        LOGGER.debug("Removed POI of type {} @ {}", LogUtils.defer(pointOfInterest::getType), LogUtils.defer(pointOfInterest::getPos));
        this.updateListener.run();
    }

    @Deprecated
    @Debug
    public int getFreeTickets(BlockPos pos) {
        return this.get(pos).map(PointOfInterest::getFreeTickets).orElse(0);
    }

    public boolean releaseTicket(BlockPos pos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos));
        if (pointOfInterest == null) {
            throw Util.throwOrPause(new IllegalStateException("POI never registered at " + pos));
        }
        boolean bl = pointOfInterest.releaseTicket();
        this.updateListener.run();
        return bl;
    }

    public boolean test(BlockPos pos, Predicate<RegistryEntry<PointOfInterestType>> predicate) {
        return this.getType(pos).filter(predicate).isPresent();
    }

    public Optional<RegistryEntry<PointOfInterestType>> getType(BlockPos pos) {
        return this.get(pos).map(PointOfInterest::getType);
    }

    private Optional<PointOfInterest> get(BlockPos pos) {
        return Optional.ofNullable((PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos)));
    }

    public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, RegistryEntry<PointOfInterestType>>> consumer) {
        if (!this.valid) {
            Short2ObjectOpenHashMap short2ObjectMap = new Short2ObjectOpenHashMap(this.pointsOfInterestByPos);
            this.clear();
            consumer.accept((arg_0, arg_1) -> this.method_20352((Short2ObjectMap)short2ObjectMap, arg_0, arg_1));
            this.valid = true;
            this.updateListener.run();
        }
    }

    private void clear() {
        this.pointsOfInterestByPos.clear();
        this.pointsOfInterestByType.clear();
    }

    boolean isValid() {
        return this.valid;
    }

    private /* synthetic */ void method_20352(Short2ObjectMap short2ObjectMap, BlockPos pos, RegistryEntry registryEntry) {
        short s2 = ChunkSectionPos.packLocal(pos);
        PointOfInterest pointOfInterest = (PointOfInterest)short2ObjectMap.computeIfAbsent(s2, s -> new PointOfInterest(pos, registryEntry, this.updateListener));
        this.add(pointOfInterest);
    }
}

