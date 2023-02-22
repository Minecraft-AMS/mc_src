/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, Identifier effects, float ambientLight, MonsterSettings monsterSettings) {
    public static final int SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
    public static final int field_33411 = 16;
    public static final int MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
    public static final int MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
    public static final int MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
    public static final int field_35478 = MAX_COLUMN_HEIGHT << 4;
    public static final int field_35479 = MIN_HEIGHT << 4;
    public static final Codec<DimensionType> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group((App)Codecs.optionalLong((MapCodec<Optional<Long>>)Codec.LONG.optionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime), (App)Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), (App)Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), (App)Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultrawarm), (App)Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), (App)Codec.doubleRange((double)1.0E-5f, (double)3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), (App)Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), (App)Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), (App)Codec.intRange((int)MIN_HEIGHT, (int)MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(DimensionType::minY), (App)Codec.intRange((int)16, (int)MAX_HEIGHT).fieldOf("height").forGetter(DimensionType::height), (App)Codec.intRange((int)0, (int)MAX_HEIGHT).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), (App)TagKey.codec(Registry.BLOCK_KEY).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), (App)Identifier.CODEC.fieldOf("effects").orElse((Object)DimensionTypes.OVERWORLD_ID).forGetter(DimensionType::effects), (App)Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), (App)MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)).apply((Applicative)instance, DimensionType::new)));
    private static final int field_31440 = 8;
    public static final float[] MOON_SIZES = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final Codec<RegistryEntry<DimensionType>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, CODEC);

    public DimensionType {
        if (j < 16) {
            throw new IllegalStateException("height has to be at least 16");
        }
        if (i + j > MAX_COLUMN_HEIGHT + 1) {
            throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_COLUMN_HEIGHT + 1));
        }
        if (k > j) {
            throw new IllegalStateException("logical_height cannot be higher than height");
        }
        if (j % 16 != 0) {
            throw new IllegalStateException("height has to be multiple of 16");
        }
        if (i % 16 != 0) {
            throw new IllegalStateException("min_y has to be a multiple of 16");
        }
    }

    @Deprecated
    public static DataResult<RegistryKey<World>> worldFromDimensionNbt(Dynamic<?> nbt) {
        Optional optional = nbt.asNumber().result();
        if (optional.isPresent()) {
            int i = ((Number)optional.get()).intValue();
            if (i == -1) {
                return DataResult.success(World.NETHER);
            }
            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }
            if (i == 1) {
                return DataResult.success(World.END);
            }
        }
        return World.CODEC.parse(nbt);
    }

    public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
        double d = fromDimension.coordinateScale();
        double e = toDimension.coordinateScale();
        return d / e;
    }

    public static Path getSaveDirectory(RegistryKey<World> worldRef, Path worldDirectory) {
        if (worldRef == World.OVERWORLD) {
            return worldDirectory;
        }
        if (worldRef == World.END) {
            return worldDirectory.resolve("DIM1");
        }
        if (worldRef == World.NETHER) {
            return worldDirectory.resolve("DIM-1");
        }
        return worldDirectory.resolve("dimensions").resolve(worldRef.getValue().getNamespace()).resolve(worldRef.getValue().getPath());
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float getSkyAngle(long time) {
        double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(time) / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    public int getMoonPhase(long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }

    public boolean piglinSafe() {
        return this.monsterSettings.piglinSafe();
    }

    public boolean hasRaids() {
        return this.monsterSettings.hasRaids();
    }

    public IntProvider monsterSpawnLightTest() {
        return this.monsterSettings.monsterSpawnLightTest();
    }

    public int monsterSpawnBlockLightLimit() {
        return this.monsterSettings.monsterSpawnBlockLightLimit();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DimensionType.class, "fixedTime;hasSkyLight;hasCeiling;ultraWarm;natural;coordinateScale;bedWorks;respawnAnchorWorks;minY;height;logicalHeight;infiniburn;effectsLocation;ambientLight;monsterSettings", "fixedTime", "hasSkyLight", "hasCeiling", "ultrawarm", "natural", "coordinateScale", "bedWorks", "respawnAnchorWorks", "minY", "height", "logicalHeight", "infiniburn", "effects", "ambientLight", "monsterSettings"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DimensionType.class, "fixedTime;hasSkyLight;hasCeiling;ultraWarm;natural;coordinateScale;bedWorks;respawnAnchorWorks;minY;height;logicalHeight;infiniburn;effectsLocation;ambientLight;monsterSettings", "fixedTime", "hasSkyLight", "hasCeiling", "ultrawarm", "natural", "coordinateScale", "bedWorks", "respawnAnchorWorks", "minY", "height", "logicalHeight", "infiniburn", "effects", "ambientLight", "monsterSettings"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DimensionType.class, "fixedTime;hasSkyLight;hasCeiling;ultraWarm;natural;coordinateScale;bedWorks;respawnAnchorWorks;minY;height;logicalHeight;infiniburn;effectsLocation;ambientLight;monsterSettings", "fixedTime", "hasSkyLight", "hasCeiling", "ultrawarm", "natural", "coordinateScale", "bedWorks", "respawnAnchorWorks", "minY", "height", "logicalHeight", "infiniburn", "effects", "ambientLight", "monsterSettings"}, this, object);
    }

    public record MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
        public static final MapCodec<MonsterSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("piglin_safe").forGetter(MonsterSettings::piglinSafe), (App)Codec.BOOL.fieldOf("has_raids").forGetter(MonsterSettings::hasRaids), (App)IntProvider.createValidatingCodec(0, 15).fieldOf("monster_spawn_light_level").forGetter(MonsterSettings::monsterSpawnLightTest), (App)Codec.intRange((int)0, (int)15).fieldOf("monster_spawn_block_light_limit").forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply((Applicative)instance, MonsterSettings::new));
    }
}

