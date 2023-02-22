/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class Tag<T> {
    private static final Tag<?> EMPTY = new Tag(List.of());
    final List<T> values;

    public Tag(Collection<T> values) {
        this.values = List.copyOf(values);
    }

    public List<T> values() {
        return this.values;
    }

    public static <T> Tag<T> empty() {
        return EMPTY;
    }

    static class OptionalTagEntry
    implements Entry {
        private final Identifier id;

        public OptionalTagEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            Tag<T> tag = tagGetter.apply(this.id);
            if (tag != null) {
                tag.values.forEach(collector);
            }
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", "#" + this.id);
            jsonObject.addProperty("required", Boolean.valueOf(false));
            json.add((JsonElement)jsonObject);
        }

        public String toString() {
            return "#" + this.id + "?";
        }

        @Override
        public void forEachGroupId(Consumer<Identifier> consumer) {
            consumer.accept(this.id);
        }

        @Override
        public boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
            return true;
        }
    }

    static class TagEntry
    implements Entry {
        private final Identifier id;

        public TagEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            Tag<T> tag = tagGetter.apply(this.id);
            if (tag == null) {
                return false;
            }
            tag.values.forEach(collector);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add("#" + this.id);
        }

        public String toString() {
            return "#" + this.id;
        }

        @Override
        public boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
            return tagExistsTest.test(this.id);
        }

        @Override
        public void forEachTagId(Consumer<Identifier> consumer) {
            consumer.accept(this.id);
        }
    }

    static class OptionalObjectEntry
    implements Entry {
        private final Identifier id;

        public OptionalObjectEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            T object = objectGetter.apply(this.id);
            if (object != null) {
                collector.accept(object);
            }
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", this.id.toString());
            jsonObject.addProperty("required", Boolean.valueOf(false));
            json.add((JsonElement)jsonObject);
        }

        @Override
        public boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
            return true;
        }

        public String toString() {
            return this.id + "?";
        }
    }

    static class ObjectEntry
    implements Entry {
        private final Identifier id;

        public ObjectEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            T object = objectGetter.apply(this.id);
            if (object == null) {
                return false;
            }
            collector.accept(object);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add(this.id.toString());
        }

        @Override
        public boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
            return objectExistsTest.test(this.id);
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public static interface Entry {
        public <T> boolean resolve(Function<Identifier, Tag<T>> var1, Function<Identifier, T> var2, Consumer<T> var3);

        public void addToJson(JsonArray var1);

        default public void forEachTagId(Consumer<Identifier> consumer) {
        }

        default public void forEachGroupId(Consumer<Identifier> consumer) {
        }

        public boolean canAdd(Predicate<Identifier> var1, Predicate<Identifier> var2);
    }

    public static class Builder {
        private final List<TrackedEntry> entries = new ArrayList<TrackedEntry>();

        public static Builder create() {
            return new Builder();
        }

        public Builder add(TrackedEntry trackedEntry) {
            this.entries.add(trackedEntry);
            return this;
        }

        public Builder add(Entry entry, String source) {
            return this.add(new TrackedEntry(entry, source));
        }

        public Builder add(Identifier id, String source) {
            return this.add(new ObjectEntry(id), source);
        }

        public Builder addOptional(Identifier id, String source) {
            return this.add(new OptionalObjectEntry(id), source);
        }

        public Builder addTag(Identifier id, String source) {
            return this.add(new TagEntry(id), source);
        }

        public Builder addOptionalTag(Identifier id, String source) {
            return this.add(new OptionalTagEntry(id), source);
        }

        public <T> Either<Collection<TrackedEntry>, Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            ArrayList<TrackedEntry> list = new ArrayList<TrackedEntry>();
            for (TrackedEntry trackedEntry : this.entries) {
                if (trackedEntry.entry().resolve(tagGetter, objectGetter, arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0))) continue;
                list.add(trackedEntry);
            }
            return list.isEmpty() ? Either.right(new Tag(builder.build())) : Either.left(list);
        }

        public Stream<TrackedEntry> streamEntries() {
            return this.entries.stream();
        }

        public void forEachTagId(Consumer<Identifier> consumer) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachTagId(consumer));
        }

        public void forEachGroupId(Consumer<Identifier> consumer) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachGroupId(consumer));
        }

        public Builder read(JsonObject json, String source) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList<Entry> list = new ArrayList<Entry>();
            for (JsonElement jsonElement : jsonArray) {
                list.add(Builder.resolveEntry(jsonElement));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            list.forEach(entry -> this.entries.add(new TrackedEntry((Entry)entry, source)));
            return this;
        }

        private static Entry resolveEntry(JsonElement json) {
            Identifier identifier;
            boolean bl;
            String string;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                string = JsonHelper.getString(jsonObject, "id");
                bl = JsonHelper.getBoolean(jsonObject, "required", true);
            } else {
                string = JsonHelper.asString(json, "id");
                bl = true;
            }
            if (string.startsWith("#")) {
                identifier = new Identifier(string.substring(1));
                return bl ? new TagEntry(identifier) : new OptionalTagEntry(identifier);
            }
            identifier = new Identifier(string);
            return bl ? new ObjectEntry(identifier) : new OptionalObjectEntry(identifier);
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (TrackedEntry trackedEntry : this.entries) {
                trackedEntry.entry().addToJson(jsonArray);
            }
            jsonObject.addProperty("replace", Boolean.valueOf(false));
            jsonObject.add("values", (JsonElement)jsonArray);
            return jsonObject;
        }
    }

    public static final class TrackedEntry
    extends Record {
        final Entry entry;
        private final String source;

        public TrackedEntry(Entry entry, String source) {
            this.entry = entry;
            this.source = source;
        }

        @Override
        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrackedEntry.class, "entry;source", "entry", "source"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrackedEntry.class, "entry;source", "entry", "source"}, this, object);
        }

        public Entry entry() {
            return this.entry;
        }

        public String source() {
            return this.source;
        }
    }
}

