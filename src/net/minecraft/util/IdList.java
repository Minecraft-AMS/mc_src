/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.IndexedIterable;
import org.jetbrains.annotations.Nullable;

public class IdList<T>
implements IndexedIterable<T> {
    private int nextId;
    private final IdentityHashMap<T, Integer> idMap;
    private final List<T> list;

    public IdList() {
        this(512);
    }

    public IdList(int i) {
        this.list = Lists.newArrayListWithExpectedSize((int)i);
        this.idMap = new IdentityHashMap(i);
    }

    public void set(T value, int i) {
        this.idMap.put(value, i);
        while (this.list.size() <= i) {
            this.list.add(null);
        }
        this.list.set(i, value);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
    }

    public void add(T object) {
        this.set(object, this.nextId);
    }

    public int getId(T object) {
        Integer integer = this.idMap.get(object);
        return integer == null ? -1 : integer;
    }

    @Override
    @Nullable
    public final T get(int index) {
        if (index >= 0 && index < this.list.size()) {
            return this.list.get(index);
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.list.iterator(), (Predicate)Predicates.notNull());
    }

    public int size() {
        return this.idMap.size();
    }
}

