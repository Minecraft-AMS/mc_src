/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public interface When
extends Supplier<JsonElement> {
    public void validate(StateManager<?, ?> var1);

    public static PropertyCondition create() {
        return new PropertyCondition();
    }

    public static When allOf(When ... conditions) {
        return new LogicalCondition(LogicalOperator.AND, Arrays.asList(conditions));
    }

    public static When anyOf(When ... conditions) {
        return new LogicalCondition(LogicalOperator.OR, Arrays.asList(conditions));
    }

    public static class PropertyCondition
    implements When {
        private final Map<Property<?>, String> properties = Maps.newHashMap();

        private static <T extends Comparable<T>> String name(Property<T> property, Stream<T> valueStream) {
            return valueStream.map(property::name).collect(Collectors.joining("|"));
        }

        private static <T extends Comparable<T>> String name(Property<T> property, T value, T[] otherValues) {
            return PropertyCondition.name(property, Stream.concat(Stream.of(value), Stream.of(otherValues)));
        }

        private <T extends Comparable<T>> void set(Property<T> property, String value) {
            String string = this.properties.put(property, value);
            if (string != null) {
                throw new IllegalStateException("Tried to replace " + property + " value from " + string + " to " + value);
            }
        }

        public final <T extends Comparable<T>> PropertyCondition set(Property<T> property, T value) {
            this.set(property, property.name(value));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PropertyCondition set(Property<T> property, T value, T ... otherValues) {
            this.set(property, PropertyCondition.name(property, value, otherValues));
            return this;
        }

        public final <T extends Comparable<T>> PropertyCondition setNegated(Property<T> property, T value) {
            this.set(property, "!" + property.name(value));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PropertyCondition setNegated(Property<T> property, T value, T ... otherValues) {
            this.set(property, "!" + PropertyCondition.name(property, value, otherValues));
            return this;
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.properties.forEach((property, value) -> jsonObject.addProperty(property.getName(), value));
            return jsonObject;
        }

        @Override
        public void validate(StateManager<?, ?> stateManager) {
            List list = this.properties.keySet().stream().filter(property -> stateManager.getProperty(property.getName()) != property).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalStateException("Properties " + list + " are missing from " + stateManager);
            }
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static class LogicalCondition
    implements When {
        private final LogicalOperator operator;
        private final List<When> components;

        LogicalCondition(LogicalOperator operator, List<When> components) {
            this.operator = operator;
            this.components = components;
        }

        @Override
        public void validate(StateManager<?, ?> stateManager) {
            this.components.forEach(component -> component.validate(stateManager));
        }

        @Override
        public JsonElement get() {
            JsonArray jsonArray = new JsonArray();
            this.components.stream().map(Supplier::get).forEach(arg_0 -> ((JsonArray)jsonArray).add(arg_0));
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(this.operator.name, (JsonElement)jsonArray);
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static final class LogicalOperator
    extends Enum<LogicalOperator> {
        public static final /* enum */ LogicalOperator AND = new LogicalOperator("AND");
        public static final /* enum */ LogicalOperator OR = new LogicalOperator("OR");
        final String name;
        private static final /* synthetic */ LogicalOperator[] field_22853;

        public static LogicalOperator[] values() {
            return (LogicalOperator[])field_22853.clone();
        }

        public static LogicalOperator valueOf(String string) {
            return Enum.valueOf(LogicalOperator.class, string);
        }

        private LogicalOperator(String name) {
            this.name = name;
        }

        private static /* synthetic */ LogicalOperator[] method_36940() {
            return new LogicalOperator[]{AND, OR};
        }

        static {
            field_22853 = LogicalOperator.method_36940();
        }
    }
}

