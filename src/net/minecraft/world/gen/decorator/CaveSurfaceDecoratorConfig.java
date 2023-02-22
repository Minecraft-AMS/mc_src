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
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CaveSurfaceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CaveSurfaceDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)VerticalSurfaceType.CODEC.fieldOf("surface").forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.surface), (App)Codec.INT.fieldOf("floor_to_ceiling_search_range").forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.searchRange)).apply((Applicative)instance, CaveSurfaceDecoratorConfig::new));
    public final VerticalSurfaceType surface;
    public final int searchRange;

    public CaveSurfaceDecoratorConfig(VerticalSurfaceType surface, int searchRange) {
        this.surface = surface;
        this.searchRange = searchRange;
    }
}

