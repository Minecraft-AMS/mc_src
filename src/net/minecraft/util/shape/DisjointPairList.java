/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.PairList;

public class DisjointPairList
extends AbstractDoubleList
implements PairList {
    private final DoubleList first;
    private final DoubleList second;
    private final boolean field_1380;

    public DisjointPairList(DoubleList first, DoubleList second, boolean inverted) {
        this.first = first;
        this.second = second;
        this.field_1380 = inverted;
    }

    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean forEachPair(PairList.SectionPairPredicate predicate) {
        if (this.field_1380) {
            return this.method_1067((i, j, k) -> predicate.merge(j, i, k));
        }
        return this.method_1067(predicate);
    }

    private boolean method_1067(PairList.SectionPairPredicate sectionPairPredicate) {
        int j;
        int i = this.first.size() - 1;
        for (j = 0; j < i; ++j) {
            if (sectionPairPredicate.merge(j, -1, j)) continue;
            return false;
        }
        if (!sectionPairPredicate.merge(i, -1, i)) {
            return false;
        }
        for (j = 0; j < this.second.size(); ++j) {
            if (sectionPairPredicate.merge(i, j, i + 1 + j)) continue;
            return false;
        }
        return true;
    }

    public double getDouble(int position) {
        if (position < this.first.size()) {
            return this.first.getDouble(position);
        }
        return this.second.getDouble(position - this.first.size());
    }

    @Override
    public DoubleList getPairs() {
        return this;
    }
}

