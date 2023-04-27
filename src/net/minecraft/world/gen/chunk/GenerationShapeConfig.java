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
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.dimension.DimensionType;

public record GenerationShapeConfig(int minimumY, int height, int horizontalSize, int verticalSize) {
    public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)DimensionType.MIN_HEIGHT, (int)DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(GenerationShapeConfig::minimumY), (App)Codec.intRange((int)0, (int)DimensionType.MAX_HEIGHT).fieldOf("height").forGetter(GenerationShapeConfig::height), (App)Codec.intRange((int)1, (int)4).fieldOf("size_horizontal").forGetter(GenerationShapeConfig::horizontalSize), (App)Codec.intRange((int)1, (int)4).fieldOf("size_vertical").forGetter(GenerationShapeConfig::verticalSize)).apply((Applicative)instance, GenerationShapeConfig::new)).comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());
    protected static final GenerationShapeConfig SURFACE = GenerationShapeConfig.create(-64, 384, 1, 2);
    protected static final GenerationShapeConfig NETHER = GenerationShapeConfig.create(0, 128, 1, 2);
    protected static final GenerationShapeConfig END = GenerationShapeConfig.create(0, 128, 2, 1);
    protected static final GenerationShapeConfig CAVES = GenerationShapeConfig.create(-64, 192, 1, 2);
    protected static final GenerationShapeConfig FLOATING_ISLANDS = GenerationShapeConfig.create(0, 256, 2, 1);

    private static DataResult<GenerationShapeConfig> checkHeight(GenerationShapeConfig config) {
        if (config.minimumY() + config.height() > DimensionType.MAX_COLUMN_HEIGHT + 1) {
            return DataResult.error(() -> "min_y + height cannot be higher than: " + (DimensionType.MAX_COLUMN_HEIGHT + 1));
        }
        if (config.height() % 16 != 0) {
            return DataResult.error(() -> "height has to be a multiple of 16");
        }
        if (config.minimumY() % 16 != 0) {
            return DataResult.error(() -> "min_y has to be a multiple of 16");
        }
        return DataResult.success((Object)config);
    }

    public static GenerationShapeConfig create(int minimumY, int height, int horizontalSize, int verticalSize) {
        GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(minimumY, height, horizontalSize, verticalSize);
        GenerationShapeConfig.checkHeight(generationShapeConfig).error().ifPresent(result -> {
            throw new IllegalStateException(result.message());
        });
        return generationShapeConfig;
    }

    public int verticalCellBlockCount() {
        return BiomeCoords.toBlock(this.verticalSize());
    }

    public int horizontalCellBlockCount() {
        return BiomeCoords.toBlock(this.horizontalSize());
    }

    public GenerationShapeConfig trimHeight(HeightLimitView world) {
        int i = Math.max(this.minimumY, world.getBottomY());
        int j = Math.min(this.minimumY + this.height, world.getTopY()) - i;
        return new GenerationShapeConfig(i, j, this.horizontalSize, this.verticalSize);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GenerationShapeConfig.class, "minY;height;noiseSizeHorizontal;noiseSizeVertical", "minimumY", "height", "horizontalSize", "verticalSize"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GenerationShapeConfig.class, "minY;height;noiseSizeHorizontal;noiseSizeVertical", "minimumY", "height", "horizontalSize", "verticalSize"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GenerationShapeConfig.class, "minY;height;noiseSizeHorizontal;noiseSizeVertical", "minimumY", "height", "horizontalSize", "verticalSize"}, this, object);
    }
}

