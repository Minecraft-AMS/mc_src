/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.carver.CarverConfig;

public class DefaultCarverConfig
implements CarverConfig {
    public static final Codec<DefaultCarverConfig> CODEC = Codec.unit(() -> INSTANCE);
    public static final DefaultCarverConfig INSTANCE = new DefaultCarverConfig();
}

