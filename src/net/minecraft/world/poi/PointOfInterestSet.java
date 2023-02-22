/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
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
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PointOfInterestSet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap();
    private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.newHashMap();
    private final Runnable updateListener;
    private boolean valid;

    public static Codec<PointOfInterestSet> createCodec(Runnable updateListener) {
        return RecordCodecBuilder.create(instance -> instance.group((App)RecordCodecBuilder.point((Object)updateListener), (App)Codec.BOOL.optionalFieldOf("Valid", (Object)false).forGetter(pointOfInterestSet -> pointOfInterestSet.valid), (App)PointOfInterest.createCodec(updateListener).listOf().fieldOf("Records").forGetter(pointOfInterestSet -> ImmutableList.copyOf((Collection)pointOfInterestSet.pointsOfInterestByPos.values()))).apply((Applicative)instance, PointOfInterestSet::new)).orElseGet(Util.method_29188("Failed to read POI section: ", arg_0 -> ((Logger)LOGGER).error(arg_0)), () -> new PointOfInterestSet(updateListener, false, (List<PointOfInterest>)ImmutableList.of()));
    }

    public PointOfInterestSet(Runnable updateListener) {
        this(updateListener, true, (List<PointOfInterest>)ImmutableList.of());
    }

    private PointOfInterestSet(Runnable updateListener, boolean valid, List<PointOfInterest> list) {
        this.updateListener = updateListener;
        this.valid = valid;
        list.forEach(this::add);
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
        short s = ChunkSectionPos.packLocal(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        if (pointOfInterest != null) {
            if (pointOfInterestType2.equals(pointOfInterest.getType())) {
                return false;
            }
            String string = "POI data mismatch: already registered at " + blockPos;
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException(string));
            }
            LOGGER.error(string);
        }
        this.pointsOfInterestByPos.put(s, (Object)poi);
        this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType2, pointOfInterestType -> Sets.newHashSet()).add(poi);
        return true;
    }

    public void remove(BlockPos pos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.packLocal(pos));
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
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos));
        if (pointOfInterest == null) {
            throw Util.throwOrPause(new IllegalStateException("POI never registered at " + pos));
        }
        boolean bl = pointOfInterest.releaseTicket();
        this.updateListener.run();
        return bl;
    }

    public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
        short s = ChunkSectionPos.packLocal(pos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null && predicate.test(pointOfInterest.getType());
    }

    public Optional<PointOfInterestType> getType(BlockPos pos) {
        short s = ChunkSectionPos.packLocal(pos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null ? Optional.of(pointOfInterest.getType()) : Optional.empty();
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

    boolean isValid() {
        return this.valid;
    }

    private /* synthetic */ void method_20352(Short2ObjectMap short2ObjectMap, BlockPos blockPos, PointOfInterestType pointOfInterestType) {
        short s = ChunkSectionPos.packLocal(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)short2ObjectMap.computeIfAbsent(s, i -> new PointOfInterest(blockPos, pointOfInterestType, this.updateListener));
        this.add(pointOfInterest);
    }
}

