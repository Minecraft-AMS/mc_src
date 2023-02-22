/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BasaltPillarFeature
extends Feature<DefaultFeatureConfig> {
    public BasaltPillarFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        if (!structureWorldAccess.isAir(blockPos) || structureWorldAccess.isAir(blockPos.up())) {
            return false;
        }
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        BlockPos.Mutable mutable2 = blockPos.mutableCopy();
        boolean bl = true;
        boolean bl2 = true;
        boolean bl3 = true;
        boolean bl4 = true;
        while (structureWorldAccess.isAir(mutable)) {
            if (structureWorldAccess.isOutOfHeightLimit(mutable)) {
                return true;
            }
            structureWorldAccess.setBlockState(mutable, Blocks.BASALT.getDefaultState(), 2);
            bl = bl && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.NORTH));
            bl2 = bl2 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.SOUTH));
            bl3 = bl3 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.WEST));
            bl4 = bl4 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.EAST));
            mutable.move(Direction.DOWN);
        }
        mutable.move(Direction.UP);
        this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.NORTH));
        this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.SOUTH));
        this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.WEST));
        this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set((Vec3i)mutable, Direction.EAST));
        mutable.move(Direction.DOWN);
        BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        for (int i = -3; i < 4; ++i) {
            for (int j = -3; j < 4; ++j) {
                int k = MathHelper.abs(i) * MathHelper.abs(j);
                if (random.nextInt(10) >= 10 - k) continue;
                mutable3.set(mutable.add(i, 0, j));
                int l = 3;
                while (structureWorldAccess.isAir(mutable2.set((Vec3i)mutable3, Direction.DOWN))) {
                    mutable3.move(Direction.DOWN);
                    if (--l > 0) continue;
                }
                if (structureWorldAccess.isAir(mutable2.set((Vec3i)mutable3, Direction.DOWN))) continue;
                structureWorldAccess.setBlockState(mutable3, Blocks.BASALT.getDefaultState(), 2);
            }
        }
        return true;
    }

    private void tryPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
        if (random.nextBoolean()) {
            world.setBlockState(pos, Blocks.BASALT.getDefaultState(), 2);
        }
    }

    private boolean stopOrPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
        if (random.nextInt(10) != 0) {
            world.setBlockState(pos, Blocks.BASALT.getDefaultState(), 2);
            return true;
        }
        return false;
    }
}

