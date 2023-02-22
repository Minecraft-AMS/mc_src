/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SpawnSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final SpawnSettings INSTANCE = new SpawnSettings(0.1f, (Map)Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> ImmutableList.of())), (Map<EntityType<?>, SpawnDensity>)ImmutableMap.of(), false);
    public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("creature_spawn_probability", (Object)Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), (App)Codec.simpleMap(SpawnGroup.CODEC, (Codec)SpawnEntry.CODEC.listOf().promotePartial(Util.method_29188("Spawn data: ", arg_0 -> ((Logger)LOGGER).error(arg_0))), (Keyable)StringIdentifiable.method_28142(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), (App)Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts), (App)Codec.BOOL.fieldOf("player_spawn_friendly").orElse((Object)false).forGetter(SpawnSettings::isPlayerSpawnFriendly)).apply((Applicative)instance, SpawnSettings::new));
    private final float creatureSpawnProbability;
    private final Map<SpawnGroup, List<SpawnEntry>> spawners;
    private final Map<EntityType<?>, SpawnDensity> spawnCosts;
    private final boolean playerSpawnFriendly;

    private SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, List<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts, boolean playerSpawnFriendly) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = spawners;
        this.spawnCosts = spawnCosts;
        this.playerSpawnFriendly = playerSpawnFriendly;
    }

    public List<SpawnEntry> getSpawnEntry(SpawnGroup spawnGroup) {
        return this.spawners.getOrDefault(spawnGroup, (List<SpawnEntry>)ImmutableList.of());
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public boolean isPlayerSpawnFriendly() {
        return this.playerSpawnFriendly;
    }

    public static class Builder {
        private final Map<SpawnGroup, List<SpawnEntry>> spawners = (Map)Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnDensity> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;
        private boolean playerSpawnFriendly;

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

        public Builder playerSpawnFriendly() {
            this.playerSpawnFriendly = true;
            return this;
        }

        public SpawnSettings build() {
            return new SpawnSettings(this.creatureSpawnProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)((Collection)entry.getValue())))), (Map)ImmutableMap.copyOf(this.spawnCosts), this.playerSpawnFriendly);
        }
    }

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter(spawnDensity -> spawnDensity.gravityLimit), (App)Codec.DOUBLE.fieldOf("charge").forGetter(spawnDensity -> spawnDensity.mass)).apply((Applicative)instance, SpawnDensity::new));
        private final double gravityLimit;
        private final double mass;

        private SpawnDensity(double gravityLimit, double mass) {
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
    extends WeightedPicker.Entry {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ENTITY_TYPE.fieldOf("type").forGetter(spawnEntry -> spawnEntry.type), (App)Codec.INT.fieldOf("weight").forGetter(spawnEntry -> spawnEntry.weight), (App)Codec.INT.fieldOf("minCount").forGetter(spawnEntry -> spawnEntry.minGroupSize), (App)Codec.INT.fieldOf("maxCount").forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative)instance, SpawnEntry::new));
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
            super(weight);
            this.type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
        }
    }
}

