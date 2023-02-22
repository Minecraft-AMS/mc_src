/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.DynamicSerializable;

public final class Timestamp
implements DynamicSerializable {
    private final long time;

    private Timestamp(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)ops.createLong(this.time);
    }

    public static Timestamp of(Dynamic<?> dynamic) {
        return new Timestamp(dynamic.asNumber((Number)0).longValue());
    }

    public static Timestamp of(long time) {
        return new Timestamp(time);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Timestamp timestamp = (Timestamp)obj;
        return this.time == timestamp.time;
    }

    public int hashCode() {
        return Long.hashCode(this.time);
    }

    public String toString() {
        return Long.toString(this.time);
    }
}

