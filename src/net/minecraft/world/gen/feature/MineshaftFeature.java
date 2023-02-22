/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class MineshaftFeature
extends StructureFeature<MineshaftFeatureConfig> {
    public MineshaftFeature(Codec<MineshaftFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(MineshaftFeature::canGenerate, MineshaftFeature::addPieces));
    }

    private static boolean canGenerate(StructureGeneratorFactory.Context<MineshaftFeatureConfig> context) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        double d = context.config().probability;
        if (chunkRandom.nextDouble() >= d) {
            return false;
        }
        return context.validBiome().test(context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(context.chunkPos().getCenterX()), BiomeCoords.fromBlock(50), BiomeCoords.fromBlock(context.chunkPos().getCenterZ())));
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<MineshaftFeatureConfig> context) {
        MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2), context.config().type);
        collector.addPiece(mineshaftRoom);
        mineshaftRoom.fillOpenings(mineshaftRoom, collector, context.random());
        int i = context.chunkGenerator().getSeaLevel();
        if (context.config().type == Type.MESA) {
            BlockPos blockPos = collector.getBoundingBox().getCenter();
            int j = context.chunkGenerator().getHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
            int k = j <= i ? i : MathHelper.nextBetween((Random)context.random(), i, j);
            int l = k - blockPos.getY();
            collector.shift(l);
        } else {
            collector.shiftInto(i, context.chunkGenerator().getMinimumY(), context.random(), 10);
        }
    }

    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        public static final /* enum */ Type NORMAL = new Type("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE);
        public static final /* enum */ Type MESA = new Type("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);
        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
        private final String name;
        private final BlockState log;
        private final BlockState planks;
        private final BlockState fence;
        private static final /* synthetic */ Type[] field_13688;

        public static Type[] values() {
            return (Type[])field_13688.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String name, Block log, Block planks, Block fence) {
            this.name = name;
            this.log = log.getDefaultState();
            this.planks = planks.getDefaultState();
            this.fence = fence.getDefaultState();
        }

        public String getName() {
            return this.name;
        }

        private static Type byName(String name) {
            return BY_NAME.get(name);
        }

        public static Type byIndex(int index) {
            if (index < 0 || index >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[index];
        }

        public BlockState getLog() {
            return this.log;
        }

        public BlockState getPlanks() {
            return this.planks;
        }

        public BlockState getFence() {
            return this.fence;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ Type[] method_36755() {
            return new Type[]{NORMAL, MESA};
        }

        static {
            field_13688 = Type.method_36755();
            CODEC = StringIdentifiable.createCodec(Type::values, Type::byName);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }
}

