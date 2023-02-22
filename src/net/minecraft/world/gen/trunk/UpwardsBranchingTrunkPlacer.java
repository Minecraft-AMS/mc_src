/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class UpwardsBranchingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<UpwardsBranchingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> UpwardsBranchingTrunkPlacer.fillTrunkPlacerFields(instance).and(instance.group((App)IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(trunkPlacer -> trunkPlacer.extraBranchSteps), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("place_branch_per_log_probability").forGetter(trunkPlacer -> Float.valueOf(trunkPlacer.placeBranchPerLogProbability)), (App)IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(trunkPlacer -> trunkPlacer.extraBranchLength), (App)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("can_grow_through").forGetter(trunkPlacer -> trunkPlacer.canGrowThrough))).apply((Applicative)instance, UpwardsBranchingTrunkPlacer::new));
    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final RegistryEntryList<Block> canGrowThrough;

    public UpwardsBranchingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength, RegistryEntryList<Block> canGrowThrough) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.UPWARDS_BRANCHING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        ArrayList list = Lists.newArrayList();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < height; ++i) {
            int j = startPos.getY() + i;
            if (this.getAndSetState(world, replacer, random, mutable.set(startPos.getX(), j, startPos.getZ()), config) && i < height - 1 && random.nextFloat() < this.placeBranchPerLogProbability) {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                int k = this.extraBranchLength.get(random);
                int l = Math.max(0, k - this.extraBranchLength.get(random) - 1);
                int m = this.extraBranchSteps.get(random);
                this.generateExtraBranch(world, replacer, random, height, config, list, mutable, j, direction, l, m);
            }
            if (i != height - 1) continue;
            list.add(new FoliagePlacer.TreeNode(mutable.set(startPos.getX(), j + 1, startPos.getZ()), 0, false));
        }
        return list;
    }

    private void generateExtraBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, TreeFeatureConfig config, List<FoliagePlacer.TreeNode> nodes, BlockPos.Mutable pos, int yOffset, Direction direction, int length, int steps) {
        int i = yOffset + length;
        int j = pos.getX();
        int k = pos.getZ();
        for (int l = length; l < height && steps > 0; ++l, --steps) {
            if (l < 1) continue;
            int m = yOffset + l;
            i = m;
            if (this.getAndSetState(world, replacer, random, pos.set(j += direction.getOffsetX(), m, k += direction.getOffsetZ()), config)) {
                ++i;
            }
            nodes.add(new FoliagePlacer.TreeNode(pos.toImmutable(), 0, false));
        }
        if (i - yOffset > 1) {
            BlockPos blockPos = new BlockPos(j, i, k);
            nodes.add(new FoliagePlacer.TreeNode(blockPos, 0, false));
            nodes.add(new FoliagePlacer.TreeNode(blockPos.down(2), 0, false));
        }
    }

    @Override
    protected boolean canReplace(TestableWorld world, BlockPos pos) {
        return super.canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(this.canGrowThrough));
    }
}

