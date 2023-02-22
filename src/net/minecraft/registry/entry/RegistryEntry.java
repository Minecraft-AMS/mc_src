/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface RegistryEntry<T> {
    public T value();

    public boolean hasKeyAndValue();

    public boolean matchesId(Identifier var1);

    public boolean matchesKey(RegistryKey<T> var1);

    public boolean matches(Predicate<RegistryKey<T>> var1);

    public boolean isIn(TagKey<T> var1);

    public Stream<TagKey<T>> streamTags();

    public Either<RegistryKey<T>, T> getKeyOrValue();

    public Optional<RegistryKey<T>> getKey();

    public Type getType();

    public boolean ownerEquals(RegistryEntryOwner<T> var1);

    public static <T> RegistryEntry<T> of(T value) {
        return new Direct<T>(value);
    }

    public record Direct<T>(T value) implements RegistryEntry<T>
    {
        @Override
        public boolean hasKeyAndValue() {
            return true;
        }

        @Override
        public boolean matchesId(Identifier id) {
            return false;
        }

        @Override
        public boolean matchesKey(RegistryKey<T> key) {
            return false;
        }

        @Override
        public boolean isIn(TagKey<T> tag) {
            return false;
        }

        @Override
        public boolean matches(Predicate<RegistryKey<T>> predicate) {
            return false;
        }

        @Override
        public Either<RegistryKey<T>, T> getKeyOrValue() {
            return Either.right(this.value);
        }

        @Override
        public Optional<RegistryKey<T>> getKey() {
            return Optional.empty();
        }

        @Override
        public Type getType() {
            return Type.DIRECT;
        }

        @Override
        public String toString() {
            return "Direct{" + this.value + "}";
        }

        @Override
        public boolean ownerEquals(RegistryEntryOwner<T> owner) {
            return true;
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return Stream.of(new TagKey[0]);
        }
    }

    public static class Reference<T>
    implements RegistryEntry<T> {
        private final RegistryEntryOwner<T> owner;
        private Set<TagKey<T>> tags = Set.of();
        private final Type referenceType;
        @Nullable
        private RegistryKey<T> registryKey;
        @Nullable
        private T value;

        private Reference(Type referenceType, RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
            this.owner = owner;
            this.referenceType = referenceType;
            this.registryKey = registryKey;
            this.value = value;
        }

        public static <T> Reference<T> standAlone(RegistryEntryOwner<T> owner, RegistryKey<T> registryKey) {
            return new Reference<Object>(Type.STAND_ALONE, owner, registryKey, null);
        }

        @Deprecated
        public static <T> Reference<T> intrusive(RegistryEntryOwner<T> owner, @Nullable T value) {
            return new Reference<T>(Type.INTRUSIVE, owner, null, value);
        }

        public RegistryKey<T> registryKey() {
            if (this.registryKey == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.owner);
            }
            return this.registryKey;
        }

        @Override
        public T value() {
            if (this.value == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.registryKey + "' from registry " + this.owner);
            }
            return this.value;
        }

        @Override
        public boolean matchesId(Identifier id) {
            return this.registryKey().getValue().equals(id);
        }

        @Override
        public boolean matchesKey(RegistryKey<T> key) {
            return this.registryKey() == key;
        }

        @Override
        public boolean isIn(TagKey<T> tag) {
            return this.tags.contains(tag);
        }

        @Override
        public boolean matches(Predicate<RegistryKey<T>> predicate) {
            return predicate.test(this.registryKey());
        }

        @Override
        public boolean ownerEquals(RegistryEntryOwner<T> owner) {
            return this.owner.ownerEquals(owner);
        }

        @Override
        public Either<RegistryKey<T>, T> getKeyOrValue() {
            return Either.left(this.registryKey());
        }

        @Override
        public Optional<RegistryKey<T>> getKey() {
            return Optional.of(this.registryKey());
        }

        @Override
        public net.minecraft.registry.entry.RegistryEntry$Type getType() {
            return net.minecraft.registry.entry.RegistryEntry$Type.REFERENCE;
        }

        @Override
        public boolean hasKeyAndValue() {
            return this.registryKey != null && this.value != null;
        }

        void setRegistryKey(RegistryKey<T> registryKey) {
            if (this.registryKey != null && registryKey != this.registryKey) {
                throw new IllegalStateException("Can't change holder key: existing=" + this.registryKey + ", new=" + registryKey);
            }
            this.registryKey = registryKey;
        }

        void setValue(T value) {
            if (this.referenceType == Type.INTRUSIVE && this.value != value) {
                throw new IllegalStateException("Can't change holder " + this.registryKey + " value: existing=" + this.value + ", new=" + value);
            }
            this.value = value;
        }

        void setTags(Collection<TagKey<T>> tags) {
            this.tags = Set.copyOf(tags);
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return this.tags.stream();
        }

        public String toString() {
            return "Reference{" + this.registryKey + "=" + this.value + "}";
        }

        static final class Type
        extends Enum<Type> {
            public static final /* enum */ Type STAND_ALONE = new Type();
            public static final /* enum */ Type INTRUSIVE = new Type();
            private static final /* synthetic */ Type[] field_36456;

            public static Type[] values() {
                return (Type[])field_36456.clone();
            }

            public static Type valueOf(String string) {
                return Enum.valueOf(Type.class, string);
            }

            private static /* synthetic */ Type[] method_40238() {
                return new Type[]{STAND_ALONE, INTRUSIVE};
            }

            static {
                field_36456 = Type.method_40238();
            }
        }
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type REFERENCE = new Type();
        public static final /* enum */ Type DIRECT = new Type();
        private static final /* synthetic */ Type[] field_36448;

        public static Type[] values() {
            return (Type[])field_36448.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static /* synthetic */ Type[] method_40232() {
            return new Type[]{REFERENCE, DIRECT};
        }

        static {
            field_36448 = Type.method_40232();
        }
    }
}

