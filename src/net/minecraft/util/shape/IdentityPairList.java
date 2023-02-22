/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.PairList;

public class IdentityPairList
implements PairList {
    private final DoubleList merged;

    public IdentityPairList(DoubleList doubleList) {
        this.merged = doubleList;
    }

    @Override
    public boolean forEachPair(PairList.SectionPairPredicate predicate) {
        for (int i = 0; i <= this.merged.size(); ++i) {
            if (predicate.merge(i, i, i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getPairs() {
        return this.merged;
    }
}
