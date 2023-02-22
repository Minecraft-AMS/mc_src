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
package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public record RandomSpreadStructurePlacement(int spacing, int separation, SpreadType spreadType, int salt, Vec3i locateOffset) implements StructurePlacement
{
    public static final Codec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.intRange((int)0, (int)4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing), (App)Codec.intRange((int)0, (int)4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation), (App)SpreadType.CODEC.optionalFieldOf("spread_type", (Object)SpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType), (App)Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(RandomSpreadStructurePlacement::salt), (App)Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", (Object)Vec3i.ZERO).forGetter(RandomSpreadStructurePlacement::locateOffset)).apply((Applicative)instance, RandomSpreadStructurePlacement::new)).flatXmap(placement -> {
        if (placement.spacing <= placement.separation) {
            return DataResult.error((String)"Spacing has to be larger than separation");
        }
        return DataResult.success((Object)placement);
    }, DataResult::success).codec();

    public RandomSpreadStructurePlacement(int spacing, int separation, SpreadType spreadType, int salt) {
        this(spacing, separation, spreadType, salt, Vec3i.ZERO);
    }

    public ChunkPos getStartChunk(long seed, int x, int z) {
        int i = this.spacing();
        int j = this.separation();
        int k = Math.floorDiv(x, i);
        int l = Math.floorDiv(z, i);
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setRegionSeed(seed, k, l, this.salt());
        int m = i - j;
        int n = this.spreadType().get(chunkRandom, m);
        int o = this.spreadType().get(chunkRandom, m);
        return new ChunkPos(k * i + n, l * i + o);
    }

    @Override
    public boolean isStartChunk(ChunkGenerator chunkGenerator, long l, int i, int j) {
        ChunkPos chunkPos = this.getStartChunk(l, i, j);
        return chunkPos.x == i && chunkPos.z == j;
    }

    @Override
    public StructurePlacementType<?> getType() {
        return StructurePlacementType.RANDOM_SPREAD;
    }
}

