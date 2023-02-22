/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;

public record MobSpawnerEntry(NbtCompound entity, Optional<CustomSpawnRules> customSpawnRules) {
    public static final Codec<MobSpawnerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NbtCompound.CODEC.fieldOf("entity").forGetter(entry -> entry.entity), (App)CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(mobSpawnerEntry -> mobSpawnerEntry.customSpawnRules)).apply((Applicative)instance, MobSpawnerEntry::new));
    public static final Codec<DataPool<MobSpawnerEntry>> DATA_POOL_CODEC = DataPool.method_39521(CODEC);
    public static final String DEFAULT_ENTITY_ID = "minecraft:pig";

    public MobSpawnerEntry() {
        this(Util.make(new NbtCompound(), nbt -> nbt.putString("id", DEFAULT_ENTITY_ID)), Optional.empty());
    }

    public MobSpawnerEntry {
        Identifier identifier = Identifier.tryParse(nbtCompound.getString("id"));
        nbtCompound.putString("id", identifier != null ? identifier.toString() : DEFAULT_ENTITY_ID);
    }

    public NbtCompound getNbt() {
        return this.entity;
    }

    public Optional<CustomSpawnRules> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules", "entity", "customSpawnRules"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules", "entity", "customSpawnRules"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules", "entity", "customSpawnRules"}, this, o);
    }

    public record CustomSpawnRules(Range<Integer> blockLightLimit, Range<Integer> skyLightLimit) {
        private static final Range<Integer> DEFAULT = new Range<Integer>(0, 15);
        public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Range.CODEC.optionalFieldOf("block_light_limit", DEFAULT).flatXmap(CustomSpawnRules::validate, CustomSpawnRules::validate).forGetter(rules -> rules.blockLightLimit), (App)Range.CODEC.optionalFieldOf("sky_light_limit", DEFAULT).flatXmap(CustomSpawnRules::validate, CustomSpawnRules::validate).forGetter(rules -> rules.skyLightLimit)).apply((Applicative)instance, CustomSpawnRules::new));

        private static DataResult<Range<Integer>> validate(Range<Integer> provider) {
            if (!DEFAULT.contains(provider)) {
                return DataResult.error((String)("Light values must be withing range " + DEFAULT));
            }
            return DataResult.success(provider);
        }
    }
}

