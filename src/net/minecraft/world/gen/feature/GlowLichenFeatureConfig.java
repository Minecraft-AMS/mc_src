/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GlowLichenFeatureConfig
implements FeatureConfig {
    public static final Codec<GlowLichenFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)1, (int)64).fieldOf("search_range").orElse((Object)10).forGetter(config -> config.searchRange), (App)Codec.BOOL.fieldOf("can_place_on_floor").orElse((Object)false).forGetter(config -> config.placeOnFloor), (App)Codec.BOOL.fieldOf("can_place_on_ceiling").orElse((Object)false).forGetter(config -> config.placeOnCeiling), (App)Codec.BOOL.fieldOf("can_place_on_wall").orElse((Object)false).forGetter(config -> config.placeOnWalls), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_spreading").orElse((Object)Float.valueOf(0.5f)).forGetter(config -> Float.valueOf(config.spreadChance)), (App)RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_be_placed_on").forGetter(config -> config.canPlaceOn)).apply((Applicative)instance, GlowLichenFeatureConfig::new));
    public final int searchRange;
    public final boolean placeOnFloor;
    public final boolean placeOnCeiling;
    public final boolean placeOnWalls;
    public final float spreadChance;
    public final RegistryEntryList<Block> canPlaceOn;
    public final List<Direction> directions;

    public GlowLichenFeatureConfig(int searchRange, boolean placeOnFloor, boolean placeOnCeiling, boolean placeOnWalls, float spreadChance, RegistryEntryList<Block> canPlaceOn) {
        this.searchRange = searchRange;
        this.placeOnFloor = placeOnFloor;
        this.placeOnCeiling = placeOnCeiling;
        this.placeOnWalls = placeOnWalls;
        this.spreadChance = spreadChance;
        this.canPlaceOn = canPlaceOn;
        ArrayList list = Lists.newArrayList();
        if (placeOnCeiling) {
            list.add(Direction.UP);
        }
        if (placeOnFloor) {
            list.add(Direction.DOWN);
        }
        if (placeOnWalls) {
            Direction.Type.HORIZONTAL.forEach(list::add);
        }
        this.directions = Collections.unmodifiableList(list);
    }
}

