/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class HeightmapDecoratorConfig
implements DecoratorConfig {
    public static final Codec<HeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(heightmapDecoratorConfig -> heightmapDecoratorConfig.heightmap)).apply((Applicative)instance, HeightmapDecoratorConfig::new));
    public final Heightmap.Type heightmap;

    public HeightmapDecoratorConfig(Heightmap.Type heightmap) {
        this.heightmap = heightmap;
    }
}

