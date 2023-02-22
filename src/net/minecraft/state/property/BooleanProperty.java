/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.property.AbstractProperty;

public class BooleanProperty
extends AbstractProperty<Boolean> {
    private final ImmutableSet<Boolean> values = ImmutableSet.of((Object)true, (Object)false);

    protected BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getValues() {
        return this.values;
    }

    public static BooleanProperty of(String name) {
        return new BooleanProperty(name);
    }

    @Override
    public Optional<Boolean> parse(String name) {
        if ("true".equals(name) || "false".equals(name)) {
            return Optional.of(Boolean.valueOf(name));
        }
        return Optional.empty();
    }

    @Override
    public String name(Boolean boolean_) {
        return boolean_.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BooleanProperty && super.equals(o)) {
            BooleanProperty booleanProperty = (BooleanProperty)o;
            return this.values.equals(booleanProperty.values);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }
}

