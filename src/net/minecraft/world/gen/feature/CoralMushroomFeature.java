/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralMushroomFeature
extends CoralFeature {
    public CoralMushroomFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected boolean spawnCoral(IWorld world, Random random, BlockPos pos, BlockState state) {
        int i = random.nextInt(3) + 3;
        int j = random.nextInt(3) + 3;
        int k = random.nextInt(3) + 3;
        int l = random.nextInt(3) + 1;
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        for (int m = 0; m <= j; ++m) {
            for (int n = 0; n <= i; ++n) {
                for (int o = 0; o <= k; ++o) {
                    mutable.set(m + pos.getX(), n + pos.getY(), o + pos.getZ());
                    mutable.setOffset(Direction.DOWN, l);
                    if ((m != 0 && m != j || n != 0 && n != i) && (o != 0 && o != k || n != 0 && n != i) && (m != 0 && m != j || o != 0 && o != k) && (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k) && !(random.nextFloat() < 0.1f) && this.spawnCoralPiece(world, random, mutable, state)) continue;
                }
            }
        }
        return true;
    }
}

