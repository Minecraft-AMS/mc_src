/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.HeightmapDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class TopSolidHeightmapDecorator
extends HeightmapDecorator<NopeDecoratorConfig> {
    public TopSolidHeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type getHeightmapType(NopeDecoratorConfig nopeDecoratorConfig) {
        return Heightmap.Type.OCEAN_FLOOR_WG;
    }
}

