/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PointOfInterestSet
implements DynamicSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap();
    private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.newHashMap();
    private final Runnable updateListener;
    private boolean valid;

    public PointOfInterestSet(Runnable updateListener) {
        this.updateListener = updateListener;
        this.valid = true;
    }

    public <T> PointOfInterestSet(Runnable runnable, Dynamic<T> dynamic2) {
        this.updateListener = runnable;
        try {
            this.valid = dynamic2.get("Valid").asBoolean(false);
            dynamic2.get("Records").asStream().forEach(dynamic -> this.add(new PointOfInterest(dynamic, runnable)));
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load POI chunk", (Throwable)exception);
            this.clear();
            this.valid = false;
        }
    }

    public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
        return this.pointsOfInterestByType.entrySet().stream().filter(entry -> predicate.test((PointOfInterestType)entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter(occupationStatus.getPredicate());
    }

    public void add(BlockPos pos, PointOfInterestType type) {
        if (this.add(new PointOfInterest(pos, type, this.updateListener))) {
            LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> type, () -> pos});
            this.updateListener.run();
        }
    }

    private boolean add(PointOfInterest poi) {
        BlockPos blockPos = poi.getPos();
        PointOfInterestType pointOfInterestType2 = poi.getType();
        short s = ChunkSectionPos.getPackedLocalPos(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        if (pointOfInterest != null) {
            if (pointOfInterestType2.equals(pointOfInterest.getType())) {
                return false;
            }
            throw new IllegalStateException("POI data mismatch: already registered at " + blockPos);
        }
        this.pointsOfInterestByPos.put(s, (Object)poi);
        this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType2, pointOfInterestType -> Sets.newHashSet()).add(poi);
        return true;
    }

    public void remove(BlockPos pos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.getPackedLocalPos(pos));
        if (pointOfInterest == null) {
            LOGGER.error("POI data mismatch: never registered at " + pos);
            return;
        }
        this.pointsOfInterestByType.get(pointOfInterest.getType()).remove(pointOfInterest);
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = pointOfInterest::getType;
        supplierArray[1] = pointOfInterest::getPos;
        LOGGER.debug("Removed POI of type {} @ {}", supplierArray);
        this.updateListener.run();
    }

    public boolean releaseTicket(BlockPos pos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.getPackedLocalPos(pos));
        if (pointOfInterest == null) {
            throw new IllegalStateException("POI never registered at " + pos);
        }
        boolean bl = pointOfInterest.releaseTicket();
        this.updateListener.run();
        return bl;
    }

    public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
        short s = ChunkSectionPos.getPackedLocalPos(pos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null && predicate.test(pointOfInterest.getType());
    }

    public Optional<PointOfInterestType> getType(BlockPos pos) {
        short s = ChunkSectionPos.getPackedLocalPos(pos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null ? Optional.of(pointOfInterest.getType()) : Optional.empty();
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        Object object = ops.createList(this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.serialize(ops)));
        return (T)ops.createMap((Map)ImmutableMap.of((Object)ops.createString("Records"), (Object)object, (Object)ops.createString("Valid"), (Object)ops.createBoolean(this.valid)));
    }

    public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
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

    private /* synthetic */ void method_20352(Short2ObjectMap short2ObjectMap, BlockPos blockPos, PointOfInterestType pointOfInterestType) {
        short s = ChunkSectionPos.getPackedLocalPos(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)short2ObjectMap.computeIfAbsent(s, i -> new PointOfInterest(blockPos, pointOfInterestType, this.updateListener));
        this.add(pointOfInterest);
    }
}

