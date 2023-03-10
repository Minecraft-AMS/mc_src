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
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public abstract class TrunkPlacer {
    public static final Codec<TrunkPlacer> TYPE_CODEC = Registry.TRUNK_PLACER_TYPE.getCodec().dispatch(TrunkPlacer::getType, TrunkPlacerType::getCodec);
    private static final int MAX_BASE_HEIGHT = 32;
    private static final int MAX_RANDOM_HEIGHT = 24;
    public static final int field_31530 = 80;
    protected final int baseHeight;
    protected final int firstRandomHeight;
    protected final int secondRandomHeight;

    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> fillTrunkPlacerFields(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.intRange((int)0, (int)32).fieldOf("base_height").forGetter(placer -> placer.baseHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_a").forGetter(placer -> placer.firstRandomHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_b").forGetter(placer -> placer.secondRandomHeight));
    }

    public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        this.baseHeight = baseHeight;
        this.firstRandomHeight = firstRandomHeight;
        this.secondRandomHeight = secondRandomHeight;
    }

    protected abstract TrunkPlacerType<?> getType();

    public abstract List<FoliagePlacer.TreeNode> generate(TestableWorld var1, BiConsumer<BlockPos, BlockState> var2, Random var3, int var4, BlockPos var5, TreeFeatureConfig var6);

    public int getHeight(Random random) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    private static boolean canGenerate(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> Feature.isSoil(state) && !state.isOf(Blocks.GRASS_BLOCK) && !state.isOf(Blocks.MYCELIUM));
    }

    protected static void setToDirt(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
        if (config.forceDirt || !TrunkPlacer.canGenerate(world, pos)) {
            replacer.accept(pos, config.dirtProvider.getBlockState(random, pos));
        }
    }

    protected static boolean getAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
        return TrunkPlacer.getAndSetState(world, replacer, random, pos, config, Function.identity());
    }

    protected static boolean getAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config, Function<BlockState, BlockState> stateProvider) {
        if (TreeFeature.canReplace(world, pos)) {
            replacer.accept(pos, stateProvider.apply(config.trunkProvider.getBlockState(random, pos)));
            return true;
        }
        return false;
    }

    protected static void trySetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos.Mutable pos, TreeFeatureConfig config) {
        if (TreeFeature.canTreeReplace(world, pos)) {
            TrunkPlacer.getAndSetState(world, replacer, random, pos, config);
        }
    }
}

