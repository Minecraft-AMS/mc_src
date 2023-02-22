/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SpawnSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float field_30983 = 0.1f;
    public static final Pool<SpawnEntry> EMPTY_ENTRY_POOL = Pool.empty();
    public static final SpawnSettings INSTANCE = new Builder().build();
    public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.floatRange((float)0.0f, (float)0.9999999f).optionalFieldOf("creature_spawn_probability", (Object)Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), (App)Codec.simpleMap(SpawnGroup.CODEC, (Codec)Pool.createCodec(SpawnEntry.CODEC).promotePartial(Util.addPrefix("Spawn data: ", arg_0 -> ((Logger)LOGGER).error(arg_0))), (Keyable)StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), (App)Codec.simpleMap(Registries.ENTITY_TYPE.getCodec(), SpawnDensity.CODEC, Registries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts)).apply((Applicative)instance, SpawnSettings::new));
    private final float creatureSpawnProbability;
    private final Map<SpawnGroup, Pool<SpawnEntry>> spawners;
    private final Map<EntityType<?>, SpawnDensity> spawnCosts;

    SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, Pool<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = ImmutableMap.copyOf(spawners);
        this.spawnCosts = ImmutableMap.copyOf(spawnCosts);
    }

    public Pool<SpawnEntry> getSpawnEntries(SpawnGroup spawnGroup) {
        return this.spawners.getOrDefault(spawnGroup, EMPTY_ENTRY_POOL);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter(spawnDensity -> spawnDensity.gravityLimit), (App)Codec.DOUBLE.fieldOf("charge").forGetter(spawnDensity -> spawnDensity.mass)).apply((Applicative)instance, SpawnDensity::new));
        private final double gravityLimit;
        private final double mass;

        SpawnDensity(double gravityLimit, double mass) {
            this.gravityLimit = gravityLimit;
            this.mass = mass;
        }

        public double getGravityLimit() {
            return this.gravityLimit;
        }

        public double getMass() {
            return this.mass;
        }
    }

    public static class SpawnEntry
    extends Weighted.Absent {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(spawnEntry -> spawnEntry.type), (App)Weight.CODEC.fieldOf("weight").forGetter(Weighted.Absent::getWeight), (App)Codec.INT.fieldOf("minCount").forGetter(spawnEntry -> spawnEntry.minGroupSize), (App)Codec.INT.fieldOf("maxCount").forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative)instance, SpawnEntry::new));
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
            this(type, Weight.of(weight), minGroupSize, maxGroupSize);
        }

        public SpawnEntry(EntityType<?> type, Weight weight, int minGroupSize, int maxGroupSize) {
            super(weight);
            this.type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.getWeight();
        }
    }

    public static class Builder {
        private final Map<SpawnGroup, List<SpawnEntry>> spawners = (Map)Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnDensity> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;

        public Builder spawn(SpawnGroup spawnGroup, SpawnEntry spawnEntry) {
            this.spawners.get(spawnGroup).add(spawnEntry);
            return this;
        }

        public Builder spawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            this.spawnCosts.put(entityType, new SpawnDensity(gravityLimit, mass));
            return this;
        }

        public Builder creatureSpawnProbability(float probability) {
            this.creatureSpawnProbability = probability;
            return this;
        }

        public SpawnSettings build() {
            return new SpawnSettings(this.creatureSpawnProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> Pool.of((List)entry.getValue()))), (Map<EntityType<?>, SpawnDensity>)ImmutableMap.copyOf(this.spawnCosts));
        }
    }
}

