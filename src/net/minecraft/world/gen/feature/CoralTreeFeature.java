/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralTreeFeature
extends CoralFeature {
    public CoralTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected boolean spawnCoral(IWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        int i = random.nextInt(3) + 1;
        for (int j = 0; j < i; ++j) {
            if (!this.spawnCoralPiece(world, random, mutable, state)) {
                return true;
            }
            mutable.setOffset(Direction.UP);
        }
        BlockPos blockPos = mutable.toImmutable();
        int k = random.nextInt(3) + 2;
        ArrayList list = Lists.newArrayList((Iterable)Direction.Type.HORIZONTAL);
        Collections.shuffle(list, random);
        List list2 = list.subList(0, k);
        for (Direction direction : list2) {
            mutable.set(blockPos);
            mutable.setOffset(direction);
            int l = random.nextInt(5) + 2;
            int m = 0;
            for (int n = 0; n < l && this.spawnCoralPiece(world, random, mutable, state); ++n) {
                mutable.setOffset(Direction.UP);
                if (n != 0 && (++m < 2 || !(random.nextFloat() < 0.25f))) continue;
                mutable.setOffset(direction);
                m = 0;
            }
        }
        return true;
    }
}

