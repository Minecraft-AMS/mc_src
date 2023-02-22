/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.state.property.Property;

public final class PropertiesMap {
    private static final PropertiesMap EMPTY = new PropertiesMap((List<Property.Value<?>>)ImmutableList.of());
    private static final Comparator<Property.Value<?>> COMPARATOR = Comparator.comparing(value -> value.getProperty().getName());
    private final List<Property.Value<?>> values;

    public PropertiesMap method_25819(Property.Value<?> value) {
        return new PropertiesMap((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).add(value).build());
    }

    public PropertiesMap copyOf(PropertiesMap propertiesMap) {
        return new PropertiesMap((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).addAll(propertiesMap.values).build());
    }

    private PropertiesMap(List<Property.Value<?>> list) {
        this.values = list;
    }

    public static PropertiesMap empty() {
        return EMPTY;
    }

    public static PropertiesMap method_25821(Property.Value<?> ... values) {
        return new PropertiesMap((List<Property.Value<?>>)ImmutableList.copyOf((Object[])values));
    }

    public boolean equals(Object object) {
        return this == object || object instanceof PropertiesMap && this.values.equals(((PropertiesMap)object).values);
    }

    public int hashCode() {
        return this.values.hashCode();
    }

    public String asString() {
        return this.values.stream().sorted(COMPARATOR).map(Property.Value::toString).collect(Collectors.joining(","));
    }

    public String toString() {
        return this.asString();
    }
}

