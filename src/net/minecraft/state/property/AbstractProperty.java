/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import net.minecraft.state.property.Property;

public abstract class AbstractProperty<T extends Comparable<T>>
implements Property<T> {
    private final Class<T> type;
    private final String name;
    private Integer computedHashCode;

    protected AbstractProperty(String name, Class<T> type) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.name).add("clazz", this.type).add("values", this.getValues()).toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractProperty) {
            AbstractProperty abstractProperty = (AbstractProperty)obj;
            return this.type.equals(abstractProperty.type) && this.name.equals(abstractProperty.name);
        }
        return false;
    }

    public final int hashCode() {
        if (this.computedHashCode == null) {
            this.computedHashCode = this.computeHashCode();
        }
        return this.computedHashCode;
    }

    public int computeHashCode() {
        return 31 * this.type.hashCode() + this.name.hashCode();
    }
}

