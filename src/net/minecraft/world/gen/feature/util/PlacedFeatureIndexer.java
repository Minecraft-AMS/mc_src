/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.gen.feature.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class PlacedFeatureIndexer {
    public static <T> List<IndexedFeatures> collectIndexedFeatures(List<T> biomes, Function<T, List<RegistryEntryList<PlacedFeature>>> biomesToPlacedFeaturesList, boolean listInvolvedBiomesOnFailure) {
        ArrayList list;
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        MutableInt mutableInt = new MutableInt(0);
        record IndexedFeature(int featureIndex, int step, PlacedFeature feature) {
        }
        Comparator<IndexedFeature> comparator = Comparator.comparingInt(IndexedFeature::step).thenComparingInt(IndexedFeature::featureIndex);
        TreeMap<IndexedFeature, Set> map = new TreeMap<IndexedFeature, Set>(comparator);
        int i = 0;
        for (T object : biomes) {
            int j;
            list = Lists.newArrayList();
            List<RegistryEntryList<PlacedFeature>> list2 = biomesToPlacedFeaturesList.apply(object);
            i = Math.max(i, list2.size());
            for (j = 0; j < list2.size(); ++j) {
                for (RegistryEntry registryEntry : (RegistryEntryList)list2.get(j)) {
                    PlacedFeature placedFeature = (PlacedFeature)registryEntry.value();
                    list.add(new IndexedFeature(object2IntMap.computeIfAbsent((Object)placedFeature, feature -> mutableInt.getAndIncrement()), j, placedFeature));
                }
            }
            for (j = 0; j < list.size(); ++j) {
                Set set = map.computeIfAbsent((IndexedFeature)list.get(j), feature -> new TreeSet(comparator));
                if (j >= list.size() - 1) continue;
                set.add((IndexedFeature)list.get(j + 1));
            }
        }
        TreeSet<IndexedFeature> set2 = new TreeSet<IndexedFeature>(comparator);
        TreeSet<IndexedFeature> set3 = new TreeSet<IndexedFeature>(comparator);
        list = Lists.newArrayList();
        for (IndexedFeature indexedFeature : map.keySet()) {
            if (!set3.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if (set2.contains(indexedFeature) || !TopologicalSorts.sort(map, set2, set3, list::add, indexedFeature)) continue;
            if (listInvolvedBiomesOnFailure) {
                int k;
                ArrayList<T> list3 = new ArrayList<T>(biomes);
                do {
                    k = list3.size();
                    ListIterator listIterator = list3.listIterator();
                    while (listIterator.hasNext()) {
                        Object object2 = listIterator.next();
                        listIterator.remove();
                        try {
                            PlacedFeatureIndexer.collectIndexedFeatures(list3, biomesToPlacedFeaturesList, false);
                        }
                        catch (IllegalStateException illegalStateException) {
                            continue;
                        }
                        listIterator.add(object2);
                    }
                } while (k != list3.size());
                throw new IllegalStateException("Feature order cycle found, involved sources: " + list3);
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse(list);
        ImmutableList.Builder builder = ImmutableList.builder();
        int j = 0;
        while (j < i) {
            int l = j++;
            List<PlacedFeature> list4 = list.stream().filter(feature -> feature.step() == l).map(IndexedFeature::feature).collect(Collectors.toList());
            builder.add((Object)new IndexedFeatures(list4));
        }
        return builder.build();
    }

    public record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        IndexedFeatures(List<PlacedFeature> features) {
            this(features, Util.lastIndexGetter(features, size -> new Object2IntOpenCustomHashMap(size, Util.identityHashStrategy())));
        }
    }
}

