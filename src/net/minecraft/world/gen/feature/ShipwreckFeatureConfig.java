/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ShipwreckFeatureConfig
implements FeatureConfig {
    public static final Codec<ShipwreckFeatureConfig> CODEC = Codec.BOOL.fieldOf("is_beached").orElse((Object)false).xmap(ShipwreckFeatureConfig::new, config -> config.isBeached).codec();
    public final boolean isBeached;

    public ShipwreckFeatureConfig(boolean isBeached) {
        this.isBeached = isBeached;
    }
}

