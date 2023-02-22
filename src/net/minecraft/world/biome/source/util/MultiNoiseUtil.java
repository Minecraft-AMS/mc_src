/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome.source.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import org.jetbrains.annotations.Nullable;

public class MultiNoiseUtil {
    private static final boolean field_34477 = false;
    private static final float TO_LONG_FACTOR = 10000.0f;
    @VisibleForTesting
    protected static final int HYPERCUBE_DIMENSION = 7;

    public static NoiseValuePoint createNoiseValuePoint(float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise) {
        return new NoiseValuePoint(MultiNoiseUtil.toLong(temperatureNoise), MultiNoiseUtil.toLong(humidityNoise), MultiNoiseUtil.toLong(continentalnessNoise), MultiNoiseUtil.toLong(erosionNoise), MultiNoiseUtil.toLong(depth), MultiNoiseUtil.toLong(weirdnessNoise));
    }

    public static NoiseHypercube createNoiseHypercube(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset) {
        return new NoiseHypercube(ParameterRange.of(temperature), ParameterRange.of(humidity), ParameterRange.of(continentalness), ParameterRange.of(erosion), ParameterRange.of(depth), ParameterRange.of(weirdness), MultiNoiseUtil.toLong(offset));
    }

    public static NoiseHypercube createNoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, float offset) {
        return new NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, MultiNoiseUtil.toLong(offset));
    }

    public static long toLong(float value) {
        return (long)(value * 10000.0f);
    }

    public static float toFloat(long value) {
        return (float)value / 10000.0f;
    }

    public static MultiNoiseSampler createEmptyMultiNoiseSampler() {
        DensityFunction densityFunction = DensityFunctionTypes.zero();
        return new MultiNoiseSampler(densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, List.of());
    }

    public static BlockPos findFittestPosition(List<NoiseHypercube> noises, MultiNoiseSampler sampler) {
        return new FittestPositionFinder(noises, (MultiNoiseSampler)sampler).bestResult.location();
    }

    public static final class NoiseValuePoint
    extends Record {
        final long temperatureNoise;
        final long humidityNoise;
        final long continentalnessNoise;
        final long erosionNoise;
        final long depth;
        final long weirdnessNoise;

        public NoiseValuePoint(long l, long m, long n, long o, long p, long q) {
            this.temperatureNoise = l;
            this.humidityNoise = m;
            this.continentalnessNoise = n;
            this.erosionNoise = o;
            this.depth = p;
            this.weirdnessNoise = q;
        }

        @VisibleForTesting
        protected long[] getNoiseValueList() {
            return new long[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0L};
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{NoiseValuePoint.class, "temperature;humidity;continentalness;erosion;depth;weirdness", "temperatureNoise", "humidityNoise", "continentalnessNoise", "erosionNoise", "depth", "weirdnessNoise"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NoiseValuePoint.class, "temperature;humidity;continentalness;erosion;depth;weirdness", "temperatureNoise", "humidityNoise", "continentalnessNoise", "erosionNoise", "depth", "weirdnessNoise"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NoiseValuePoint.class, "temperature;humidity;continentalness;erosion;depth;weirdness", "temperatureNoise", "humidityNoise", "continentalnessNoise", "erosionNoise", "depth", "weirdnessNoise"}, this, object);
        }

        public long temperatureNoise() {
            return this.temperatureNoise;
        }

        public long humidityNoise() {
            return this.humidityNoise;
        }

        public long continentalnessNoise() {
            return this.continentalnessNoise;
        }

        public long erosionNoise() {
            return this.erosionNoise;
        }

        public long depth() {
            return this.depth;
        }

        public long weirdnessNoise() {
            return this.weirdnessNoise;
        }
    }

    public record NoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, long offset) {
        public static final Codec<NoiseHypercube> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ParameterRange.CODEC.fieldOf("temperature").forGetter(noiseHypercube -> noiseHypercube.temperature), (App)ParameterRange.CODEC.fieldOf("humidity").forGetter(noiseHypercube -> noiseHypercube.humidity), (App)ParameterRange.CODEC.fieldOf("continentalness").forGetter(noiseHypercube -> noiseHypercube.continentalness), (App)ParameterRange.CODEC.fieldOf("erosion").forGetter(noiseHypercube -> noiseHypercube.erosion), (App)ParameterRange.CODEC.fieldOf("depth").forGetter(noiseHypercube -> noiseHypercube.depth), (App)ParameterRange.CODEC.fieldOf("weirdness").forGetter(noiseHypercube -> noiseHypercube.weirdness), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("offset").xmap(MultiNoiseUtil::toLong, MultiNoiseUtil::toFloat).forGetter(noiseHypercube -> noiseHypercube.offset)).apply((Applicative)instance, NoiseHypercube::new));

        long getSquaredDistance(NoiseValuePoint point) {
            return MathHelper.square(this.temperature.getDistance(point.temperatureNoise)) + MathHelper.square(this.humidity.getDistance(point.humidityNoise)) + MathHelper.square(this.continentalness.getDistance(point.continentalnessNoise)) + MathHelper.square(this.erosion.getDistance(point.erosionNoise)) + MathHelper.square(this.depth.getDistance(point.depth)) + MathHelper.square(this.weirdness.getDistance(point.weirdnessNoise)) + MathHelper.square(this.offset);
        }

        protected List<ParameterRange> getParameters() {
            return ImmutableList.of((Object)this.temperature, (Object)this.humidity, (Object)this.continentalness, (Object)this.erosion, (Object)this.depth, (Object)this.weirdness, (Object)new ParameterRange(this.offset, this.offset));
        }
    }

    public record ParameterRange(long min, long max) {
        public static final Codec<ParameterRange> CODEC = Codecs.createCodecForPairObject(Codec.floatRange((float)-2.0f, (float)2.0f), "min", "max", (min, max) -> {
            if (min.compareTo((Float)max) > 0) {
                return DataResult.error((String)("Cannon construct interval, min > max (" + min + " > " + max + ")"));
            }
            return DataResult.success((Object)new ParameterRange(MultiNoiseUtil.toLong(min.floatValue()), MultiNoiseUtil.toLong(max.floatValue())));
        }, parameterRange -> Float.valueOf(MultiNoiseUtil.toFloat(parameterRange.min())), parameterRange -> Float.valueOf(MultiNoiseUtil.toFloat(parameterRange.max())));

        public static ParameterRange of(float point) {
            return ParameterRange.of(point, point);
        }

        public static ParameterRange of(float min, float max) {
            if (min > max) {
                throw new IllegalArgumentException("min > max: " + min + " " + max);
            }
            return new ParameterRange(MultiNoiseUtil.toLong(min), MultiNoiseUtil.toLong(max));
        }

        public static ParameterRange combine(ParameterRange min, ParameterRange max) {
            if (min.min() > max.max()) {
                throw new IllegalArgumentException("min > max: " + min + " " + max);
            }
            return new ParameterRange(min.min(), max.max());
        }

        @Override
        public String toString() {
            return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
        }

        public long getDistance(long noise) {
            long l = noise - this.max;
            long m = this.min - noise;
            if (l > 0L) {
                return l;
            }
            return Math.max(m, 0L);
        }

        public long getDistance(ParameterRange other) {
            long l = other.min() - this.max;
            long m = this.min - other.max();
            if (l > 0L) {
                return l;
            }
            return Math.max(m, 0L);
        }

        public ParameterRange combine(@Nullable ParameterRange other) {
            return other == null ? this : new ParameterRange(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
        }
    }

    public record MultiNoiseSampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<NoiseHypercube> spawnTarget) {
        public NoiseValuePoint sample(int x, int y, int z) {
            int i = BiomeCoords.toBlock(x);
            int j = BiomeCoords.toBlock(y);
            int k = BiomeCoords.toBlock(z);
            DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(i, j, k);
            return MultiNoiseUtil.createNoiseValuePoint((float)this.temperature.sample(unblendedNoisePos), (float)this.humidity.sample(unblendedNoisePos), (float)this.continentalness.sample(unblendedNoisePos), (float)this.erosion.sample(unblendedNoisePos), (float)this.depth.sample(unblendedNoisePos), (float)this.weirdness.sample(unblendedNoisePos));
        }

        public BlockPos findBestSpawnPosition() {
            if (this.spawnTarget.isEmpty()) {
                return BlockPos.ORIGIN;
            }
            return MultiNoiseUtil.findFittestPosition(this.spawnTarget, this);
        }
    }

    static class FittestPositionFinder {
        Result bestResult;

        FittestPositionFinder(List<NoiseHypercube> noises, MultiNoiseSampler sampler) {
            this.bestResult = FittestPositionFinder.calculateFitness(noises, sampler, 0, 0);
            this.findFittest(noises, sampler, 2048.0f, 512.0f);
            this.findFittest(noises, sampler, 512.0f, 32.0f);
        }

        private void findFittest(List<NoiseHypercube> noises, MultiNoiseSampler sampler, float maxDistance, float step) {
            float f = 0.0f;
            float g = step;
            BlockPos blockPos = this.bestResult.location();
            while (g <= maxDistance) {
                int j;
                int i = blockPos.getX() + (int)(Math.sin(f) * (double)g);
                Result result = FittestPositionFinder.calculateFitness(noises, sampler, i, j = blockPos.getZ() + (int)(Math.cos(f) * (double)g));
                if (result.fitness() < this.bestResult.fitness()) {
                    this.bestResult = result;
                }
                if (!((double)(f += step / g) > Math.PI * 2)) continue;
                f = 0.0f;
                g += step;
            }
        }

        private static Result calculateFitness(List<NoiseHypercube> noises, MultiNoiseSampler sampler, int x, int z) {
            double d = MathHelper.square(2500.0);
            int i = 2;
            long l = (long)((double)MathHelper.square(10000.0f) * Math.pow((double)(MathHelper.square((long)x) + MathHelper.square((long)z)) / d, 2.0));
            NoiseValuePoint noiseValuePoint = sampler.sample(BiomeCoords.fromBlock(x), 0, BiomeCoords.fromBlock(z));
            NoiseValuePoint noiseValuePoint2 = new NoiseValuePoint(noiseValuePoint.temperatureNoise(), noiseValuePoint.humidityNoise(), noiseValuePoint.continentalnessNoise(), noiseValuePoint.erosionNoise(), 0L, noiseValuePoint.weirdnessNoise());
            long m = Long.MAX_VALUE;
            for (NoiseHypercube noiseHypercube : noises) {
                m = Math.min(m, noiseHypercube.getSquaredDistance(noiseValuePoint2));
            }
            return new Result(new BlockPos(x, 0, z), l + m);
        }

        record Result(BlockPos location, long fitness) {
        }
    }

    public static class Entries<T> {
        private final List<Pair<NoiseHypercube, T>> entries;
        private final SearchTree<T> tree;

        public Entries(List<Pair<NoiseHypercube, T>> entries) {
            this.entries = entries;
            this.tree = SearchTree.create(entries);
        }

        public List<Pair<NoiseHypercube, T>> getEntries() {
            return this.entries;
        }

        public T get(NoiseValuePoint point) {
            return this.getValue(point);
        }

        @VisibleForTesting
        public T getValueSimple(NoiseValuePoint point) {
            Iterator<Pair<NoiseHypercube, T>> iterator = this.getEntries().iterator();
            Pair<NoiseHypercube, T> pair = iterator.next();
            long l = ((NoiseHypercube)pair.getFirst()).getSquaredDistance(point);
            Object object = pair.getSecond();
            while (iterator.hasNext()) {
                Pair<NoiseHypercube, T> pair2 = iterator.next();
                long m = ((NoiseHypercube)pair2.getFirst()).getSquaredDistance(point);
                if (m >= l) continue;
                l = m;
                object = pair2.getSecond();
            }
            return (T)object;
        }

        public T getValue(NoiseValuePoint point) {
            return this.getValue(point, SearchTree.TreeNode::getSquaredDistance);
        }

        protected T getValue(NoiseValuePoint point, NodeDistanceFunction<T> distanceFunction) {
            return this.tree.get(point, distanceFunction);
        }
    }

    protected static final class SearchTree<T> {
        private static final int MAX_NODES_FOR_SIMPLE_TREE = 6;
        private final TreeNode<T> firstNode;
        private final ThreadLocal<TreeLeafNode<T>> previousResultNode = new ThreadLocal();

        private SearchTree(TreeNode<T> firstNode) {
            this.firstNode = firstNode;
        }

        public static <T> SearchTree<T> create(List<Pair<NoiseHypercube, T>> entries) {
            if (entries.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            }
            int i = ((NoiseHypercube)entries.get(0).getFirst()).getParameters().size();
            if (i != 7) {
                throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
            }
            List list = entries.stream().map(entry -> new TreeLeafNode<Object>((NoiseHypercube)entry.getFirst(), entry.getSecond())).collect(Collectors.toCollection(ArrayList::new));
            return new SearchTree<T>(SearchTree.createNode(i, list));
        }

        private static <T> TreeNode<T> createNode(int parameterNumber, List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            }
            if (subTree.size() == 1) {
                return subTree.get(0);
            }
            if (subTree.size() <= 6) {
                subTree.sort(Comparator.comparingLong(node -> {
                    long l = 0L;
                    for (int j = 0; j < parameterNumber; ++j) {
                        ParameterRange parameterRange = node.parameters[j];
                        l += Math.abs((parameterRange.min() + parameterRange.max()) / 2L);
                    }
                    return l;
                }));
                return new TreeBranchNode(subTree);
            }
            long l = Long.MAX_VALUE;
            int i = -1;
            List<TreeBranchNode<T>> list = null;
            for (int j = 0; j < parameterNumber; ++j) {
                SearchTree.sortTree(subTree, parameterNumber, j, false);
                List<TreeBranchNode<T>> list2 = SearchTree.getBatchedTree(subTree);
                long m = 0L;
                for (TreeBranchNode<T> treeBranchNode : list2) {
                    m += SearchTree.getRangeLengthSum(treeBranchNode.parameters);
                }
                if (l <= m) continue;
                l = m;
                i = j;
                list = list2;
            }
            SearchTree.sortTree(list, parameterNumber, i, true);
            return new TreeBranchNode(list.stream().map(node -> SearchTree.createNode(parameterNumber, Arrays.asList(node.subTree))).collect(Collectors.toList()));
        }

        private static <T> void sortTree(List<? extends TreeNode<T>> subTree, int parameterNumber, int currentParameter, boolean abs) {
            Comparator<TreeNode<TreeNode<T>>> comparator = SearchTree.createNodeComparator(currentParameter, abs);
            for (int i = 1; i < parameterNumber; ++i) {
                comparator = comparator.thenComparing(SearchTree.createNodeComparator((currentParameter + i) % parameterNumber, abs));
            }
            subTree.sort(comparator);
        }

        private static <T> Comparator<TreeNode<T>> createNodeComparator(int currentParameter, boolean abs) {
            return Comparator.comparingLong(treeNode -> {
                ParameterRange parameterRange = treeNode.parameters[currentParameter];
                long l = (parameterRange.min() + parameterRange.max()) / 2L;
                return abs ? Math.abs(l) : l;
            });
        }

        private static <T> List<TreeBranchNode<T>> getBatchedTree(List<? extends TreeNode<T>> nodes) {
            ArrayList list = Lists.newArrayList();
            ArrayList list2 = Lists.newArrayList();
            int i = (int)Math.pow(6.0, Math.floor(Math.log((double)nodes.size() - 0.01) / Math.log(6.0)));
            for (TreeNode<T> treeNode : nodes) {
                list2.add(treeNode);
                if (list2.size() < i) continue;
                list.add(new TreeBranchNode(list2));
                list2 = Lists.newArrayList();
            }
            if (!list2.isEmpty()) {
                list.add(new TreeBranchNode(list2));
            }
            return list;
        }

        private static long getRangeLengthSum(ParameterRange[] parameters) {
            long l = 0L;
            for (ParameterRange parameterRange : parameters) {
                l += Math.abs(parameterRange.max() - parameterRange.min());
            }
            return l;
        }

        static <T> List<ParameterRange> getEnclosingParameters(List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            }
            int i = 7;
            ArrayList list = Lists.newArrayList();
            for (int j = 0; j < 7; ++j) {
                list.add(null);
            }
            for (TreeNode<T> treeNode : subTree) {
                for (int k = 0; k < 7; ++k) {
                    list.set(k, treeNode.parameters[k].combine((ParameterRange)list.get(k)));
                }
            }
            return list;
        }

        public T get(NoiseValuePoint point, NodeDistanceFunction<T> distanceFunction) {
            long[] ls = point.getNoiseValueList();
            TreeLeafNode<T> treeLeafNode = this.firstNode.getResultingNode(ls, this.previousResultNode.get(), distanceFunction);
            this.previousResultNode.set(treeLeafNode);
            return treeLeafNode.value;
        }

        static abstract class TreeNode<T> {
            protected final ParameterRange[] parameters;

            protected TreeNode(List<ParameterRange> parameters) {
                this.parameters = parameters.toArray(new ParameterRange[0]);
            }

            protected abstract TreeLeafNode<T> getResultingNode(long[] var1, @Nullable TreeLeafNode<T> var2, NodeDistanceFunction<T> var3);

            protected long getSquaredDistance(long[] otherParameters) {
                long l = 0L;
                for (int i = 0; i < 7; ++i) {
                    l += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
                }
                return l;
            }

            public String toString() {
                return Arrays.toString(this.parameters);
            }
        }

        static final class TreeBranchNode<T>
        extends TreeNode<T> {
            final TreeNode<T>[] subTree;

            protected TreeBranchNode(List<? extends TreeNode<T>> list) {
                this(SearchTree.getEnclosingParameters(list), list);
            }

            protected TreeBranchNode(List<ParameterRange> parameters, List<? extends TreeNode<T>> subTree) {
                super(parameters);
                this.subTree = subTree.toArray(new TreeNode[0]);
            }

            @Override
            protected TreeLeafNode<T> getResultingNode(long[] otherParameters, @Nullable TreeLeafNode<T> alternative, NodeDistanceFunction<T> distanceFunction) {
                long l = alternative == null ? Long.MAX_VALUE : distanceFunction.getDistance(alternative, otherParameters);
                TreeLeafNode<T> treeLeafNode = alternative;
                for (TreeNode<T> treeNode : this.subTree) {
                    long n;
                    long m = distanceFunction.getDistance(treeNode, otherParameters);
                    if (l <= m) continue;
                    TreeLeafNode<T> treeLeafNode2 = treeNode.getResultingNode(otherParameters, treeLeafNode, distanceFunction);
                    long l2 = n = treeNode == treeLeafNode2 ? m : distanceFunction.getDistance(treeLeafNode2, otherParameters);
                    if (l <= n) continue;
                    l = n;
                    treeLeafNode = treeLeafNode2;
                }
                return treeLeafNode;
            }
        }

        static final class TreeLeafNode<T>
        extends TreeNode<T> {
            final T value;

            TreeLeafNode(NoiseHypercube parameters, T value) {
                super(parameters.getParameters());
                this.value = value;
            }

            @Override
            protected TreeLeafNode<T> getResultingNode(long[] otherParameters, @Nullable TreeLeafNode<T> alternative, NodeDistanceFunction<T> distanceFunction) {
                return this;
            }
        }
    }

    static interface NodeDistanceFunction<T> {
        public long getDistance(SearchTree.TreeNode<T> var1, long[] var2);
    }
}

