/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public abstract class TrunkPlacer {
    public static final Codec<TrunkPlacer> TYPE_CODEC = Registry.TRUNK_PLACER_TYPE.dispatch(TrunkPlacer::getType, TrunkPlacerType::getCodec);
    protected final int baseHeight;
    protected final int firstRandomHeight;
    protected final int secondRandomHeight;

    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> method_28904(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.intRange((int)0, (int)32).fieldOf("base_height").forGetter(trunkPlacer -> trunkPlacer.baseHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_a").forGetter(trunkPlacer -> trunkPlacer.firstRandomHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_b").forGetter(trunkPlacer -> trunkPlacer.secondRandomHeight));
    }

    public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        this.baseHeight = baseHeight;
        this.firstRandomHeight = firstRandomHeight;
        this.secondRandomHeight = secondRandomHeight;
    }

    protected abstract TrunkPlacerType<?> getType();

    public abstract List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BlockBox var6, TreeFeatureConfig var7);

    public int getHeight(Random random) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    protected static void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state, BlockBox box) {
        TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
        box.encompass(new BlockBox(pos, pos));
    }

    private static boolean canGenerate(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Block block = state.getBlock();
            return Feature.isSoil(block) && !state.isOf(Blocks.GRASS_BLOCK) && !state.isOf(Blocks.MYCELIUM);
        });
    }

    protected static void setToDirt(ModifiableTestableWorld world, BlockPos pos) {
        if (!TrunkPlacer.canGenerate(world, pos)) {
            TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, pos, Blocks.DIRT.getDefaultState());
        }
    }

    protected static boolean getAndSetState(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config) {
        if (TreeFeature.canReplace(world, pos)) {
            TrunkPlacer.setBlockState(world, pos, config.trunkProvider.getBlockState(random, pos), box);
            placedStates.add(pos.toImmutable());
            return true;
        }
        return false;
    }

    protected static void trySetState(ModifiableTestableWorld world, Random random, BlockPos.Mutable pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config) {
        if (TreeFeature.canTreeReplace(world, pos)) {
            TrunkPlacer.getAndSetState(world, random, pos, placedStates, box, config);
        }
    }
}

