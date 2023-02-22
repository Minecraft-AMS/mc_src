/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class FuzzyPositions {
    private static final int GAUSS_RANGE = 10;

    public static BlockPos localFuzz(Random random, int horizontalRange, int verticalRange) {
        int i = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
        int j = random.nextInt(2 * verticalRange + 1) - verticalRange;
        int k = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
        return new BlockPos(i, j, k);
    }

    @Nullable
    public static BlockPos localFuzz(Random random, int horizontalRange, int verticalRange, int startHeight, double directionX, double directionZ, double angleRange) {
        double d = MathHelper.atan2(directionZ, directionX) - 1.5707963705062866;
        double e = d + (double)(2.0f * random.nextFloat() - 1.0f) * angleRange;
        double f = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)horizontalRange;
        double g = -f * Math.sin(e);
        double h = f * Math.cos(e);
        if (Math.abs(g) > (double)horizontalRange || Math.abs(h) > (double)horizontalRange) {
            return null;
        }
        int i = random.nextInt(2 * verticalRange + 1) - verticalRange + startHeight;
        return new BlockPos(g, (double)i, h);
    }

    @VisibleForTesting
    public static BlockPos upWhile(BlockPos pos, int maxY, Predicate<BlockPos> condition) {
        if (condition.test(pos)) {
            BlockPos blockPos = pos.up();
            while (blockPos.getY() < maxY && condition.test(blockPos)) {
                blockPos = blockPos.up();
            }
            return blockPos;
        }
        return pos;
    }

    @VisibleForTesting
    public static BlockPos upWhile(BlockPos pos, int extraAbove, int max, Predicate<BlockPos> condition) {
        if (extraAbove < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + extraAbove + ", expected >= 0");
        }
        if (condition.test(pos)) {
            BlockPos blockPos3;
            BlockPos blockPos = pos.up();
            while (blockPos.getY() < max && condition.test(blockPos)) {
                blockPos = blockPos.up();
            }
            BlockPos blockPos2 = blockPos;
            while (blockPos2.getY() < max && blockPos2.getY() - blockPos.getY() < extraAbove && !condition.test(blockPos3 = blockPos2.up())) {
                blockPos2 = blockPos3;
            }
            return blockPos2;
        }
        return pos;
    }

    @Nullable
    public static Vec3d guessBestPathTarget(PathAwareEntity entity, Supplier<BlockPos> factory) {
        return FuzzyPositions.guessBest(factory, entity::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d guessBest(Supplier<BlockPos> factory, ToDoubleFunction<BlockPos> scorer) {
        double d = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = null;
        for (int i = 0; i < 10; ++i) {
            double e;
            BlockPos blockPos2 = factory.get();
            if (blockPos2 == null || !((e = scorer.applyAsDouble(blockPos2)) > d)) continue;
            d = e;
            blockPos = blockPos2;
        }
        return blockPos != null ? Vec3d.ofBottomCenter(blockPos) : null;
    }

    public static BlockPos towardTarget(PathAwareEntity entity, int horizontalRange, Random random, BlockPos fuzz) {
        int i = fuzz.getX();
        int j = fuzz.getZ();
        if (entity.hasPositionTarget() && horizontalRange > 1) {
            BlockPos blockPos = entity.getPositionTarget();
            i = entity.getX() > (double)blockPos.getX() ? (i -= random.nextInt(horizontalRange / 2)) : (i += random.nextInt(horizontalRange / 2));
            j = entity.getZ() > (double)blockPos.getZ() ? (j -= random.nextInt(horizontalRange / 2)) : (j += random.nextInt(horizontalRange / 2));
        }
        return new BlockPos((double)i + entity.getX(), (double)fuzz.getY() + entity.getY(), (double)j + entity.getZ());
    }
}

