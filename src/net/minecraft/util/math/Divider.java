/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  it.unimi.dsi.fastutil.ints.IntIterator
 */
package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.NoSuchElementException;

public class Divider
implements IntIterator {
    private final int divisor;
    private final int quotient;
    private final int mod;
    private int returnedCount;
    private int remainder;

    public Divider(int dividend, int divisor) {
        this.divisor = divisor;
        if (divisor > 0) {
            this.quotient = dividend / divisor;
            this.mod = dividend % divisor;
        } else {
            this.quotient = 0;
            this.mod = 0;
        }
    }

    public boolean hasNext() {
        return this.returnedCount < this.divisor;
    }

    public int nextInt() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        int i = this.quotient;
        this.remainder += this.mod;
        if (this.remainder >= this.divisor) {
            this.remainder -= this.divisor;
            ++i;
        }
        ++this.returnedCount;
        return i;
    }

    @VisibleForTesting
    public static Iterable<Integer> asIterable(int dividend, int divisor) {
        return () -> new Divider(dividend, divisor);
    }
}

