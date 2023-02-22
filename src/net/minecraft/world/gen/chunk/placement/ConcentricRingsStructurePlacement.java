/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public record ConcentricRingsStructurePlacement(int distance, int spread, int count) implements StructurePlacement
{
    public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance), (App)Codec.intRange((int)0, (int)1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread), (App)Codec.intRange((int)1, (int)4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count)).apply((Applicative)instance, ConcentricRingsStructurePlacement::new));

    @Override
    public boolean isStartChunk(ChunkGenerator chunkGenerator, long l, int i, int j) {
        List<ChunkPos> list = chunkGenerator.getConcentricRingsStartChunks(this);
        if (list == null) {
            return false;
        }
        return list.contains(new ChunkPos(i, j));
    }

    @Override
    public StructurePlacementType<?> getType() {
        return StructurePlacementType.CONCENTRIC_RINGS;
    }
}

