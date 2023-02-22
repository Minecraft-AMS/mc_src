/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class RegistryBuilder {
    private final List<RegistryInfo<?>> registries = new ArrayList();

    static <T> RegistryEntryLookup<T> toLookup(final RegistryWrapper.Impl<T> wrapper) {
        return new EntryListCreatingLookup<T>(wrapper){

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return wrapper.getOptional(key);
            }
        };
    }

    public <T> RegistryBuilder addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, BootstrapFunction<T> bootstrapFunction) {
        this.registries.add(new RegistryInfo<T>(registryRef, lifecycle, bootstrapFunction));
        return this;
    }

    public <T> RegistryBuilder addRegistry(RegistryKey<? extends Registry<T>> registryRef, BootstrapFunction<T> bootstrapFunction) {
        return this.addRegistry(registryRef, Lifecycle.stable(), bootstrapFunction);
    }

    private Registries createBootstrappedRegistries(DynamicRegistryManager registryManager) {
        Registries registries = Registries.of(registryManager, this.registries.stream().map(RegistryInfo::key));
        this.registries.forEach(registry -> registry.runBootstrap(registries));
        return registries;
    }

    public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager) {
        Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
        Stream<RegistryWrapper.Impl> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
        Stream<RegistryWrapper.Impl> stream2 = this.registries.stream().map(info -> info.init(registries).toWrapper());
        RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(Stream.concat(stream, stream2.peek(registries::addOwner)));
        registries.validateReferences();
        registries.throwErrors();
        return wrapperLookup;
    }

    public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager, RegistryWrapper.WrapperLookup wrapperLookup) {
        Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
        Stream<RegistryWrapper.Impl> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
        Stream<RegistryWrapper.Impl> stream2 = this.registries.stream().map(info -> info.init(registries).toWrapper());
        RegistryWrapper.WrapperLookup wrapperLookup2 = RegistryWrapper.WrapperLookup.of(Stream.concat(stream, stream2.peek(registries::addOwner)));
        registries.setReferenceEntryValues(wrapperLookup);
        registries.validateReferences();
        registries.throwErrors();
        return wrapperLookup2;
    }

    record RegistryInfo<T>(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, BootstrapFunction<T> bootstrap) {
        void runBootstrap(Registries registries) {
            this.bootstrap.run(registries.createRegisterable());
        }

        public InitializedRegistry<T> init(Registries registries) {
            HashMap map = new HashMap();
            Iterator<Map.Entry<RegistryKey<?>, RegisteredValue<?>>> iterator = registries.registeredValues.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RegistryKey<?>, RegisteredValue<?>> entry = iterator.next();
                RegistryKey<?> registryKey = entry.getKey();
                if (!registryKey.isOf(this.key)) continue;
                RegistryKey<?> registryKey2 = registryKey;
                RegisteredValue<?> registeredValue = entry.getValue();
                RegistryEntry.Reference<Object> reference = registries.lookup.keysToEntries.remove(registryKey);
                map.put(registryKey2, new EntryAssociatedValue(registeredValue, Optional.ofNullable(reference)));
                iterator.remove();
            }
            return new InitializedRegistry(this, map);
        }
    }

    @FunctionalInterface
    public static interface BootstrapFunction<T> {
        public void run(Registerable<T> var1);
    }

    static final class Registries
    extends Record {
        private final AnyOwner owner;
        final StandAloneEntryCreatingLookup lookup;
        final Map<Identifier, RegistryEntryLookup<?>> registries;
        final Map<RegistryKey<?>, RegisteredValue<?>> registeredValues;
        final List<RuntimeException> errors;

        private Registries(AnyOwner anyOwner, StandAloneEntryCreatingLookup standAloneEntryCreatingLookup, Map<Identifier, RegistryEntryLookup<?>> map, Map<RegistryKey<?>, RegisteredValue<?>> map2, List<RuntimeException> list) {
            this.owner = anyOwner;
            this.lookup = standAloneEntryCreatingLookup;
            this.registries = map;
            this.registeredValues = map2;
            this.errors = list;
        }

        public static Registries of(DynamicRegistryManager dynamicRegistryManager, Stream<RegistryKey<? extends Registry<?>>> registryRefs) {
            AnyOwner anyOwner = new AnyOwner();
            ArrayList<RuntimeException> list = new ArrayList<RuntimeException>();
            StandAloneEntryCreatingLookup standAloneEntryCreatingLookup = new StandAloneEntryCreatingLookup(anyOwner);
            ImmutableMap.Builder builder = ImmutableMap.builder();
            dynamicRegistryManager.streamAllRegistries().forEach(entry -> builder.put((Object)entry.key().getValue(), RegistryBuilder.toLookup(entry.value().getReadOnlyWrapper())));
            registryRefs.forEach(registryRef -> builder.put((Object)registryRef.getValue(), (Object)standAloneEntryCreatingLookup));
            return new Registries(anyOwner, standAloneEntryCreatingLookup, (Map<Identifier, RegistryEntryLookup<?>>)builder.build(), new HashMap(), (List<RuntimeException>)list);
        }

        public <T> Registerable<T> createRegisterable() {
            return new Registerable<T>(){

                @Override
                public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
                    RegisteredValue registeredValue = registeredValues.put(key, new RegisteredValue(value, lifecycle));
                    if (registeredValue != null) {
                        errors.add(new IllegalStateException("Duplicate registration for " + key + ", new=" + value + ", old=" + registeredValue.value));
                    }
                    return lookup.getOrCreate(key);
                }

                @Override
                public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
                    return registries.getOrDefault(registryRef.getValue(), lookup);
                }
            };
        }

        public void validateReferences() {
            for (RegistryKey<Object> registryKey : this.lookup.keysToEntries.keySet()) {
                this.errors.add(new IllegalStateException("Unreferenced key: " + registryKey));
            }
            this.registeredValues.forEach((key, value) -> this.errors.add(new IllegalStateException("Orpaned value " + value.value + " for key " + key)));
        }

        public void throwErrors() {
            if (!this.errors.isEmpty()) {
                IllegalStateException illegalStateException = new IllegalStateException("Errors during registry creation");
                for (RuntimeException runtimeException : this.errors) {
                    illegalStateException.addSuppressed(runtimeException);
                }
                throw illegalStateException;
            }
        }

        public void addOwner(RegistryEntryOwner<?> owner) {
            this.owner.addOwner(owner);
        }

        public void setReferenceEntryValues(RegistryWrapper.WrapperLookup lookup) {
            HashMap<Identifier, Optional> map = new HashMap<Identifier, Optional>();
            Iterator<Map.Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>>> iterator = this.lookup.keysToEntries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>> entry2 = iterator.next();
                RegistryKey<Object> registryKey = entry2.getKey();
                RegistryEntry.Reference<Object> reference = entry2.getValue();
                map.computeIfAbsent(registryKey.getRegistry(), registryId -> lookup.getOptionalWrapper(RegistryKey.ofRegistry(registryId))).flatMap(entryLookup -> entryLookup.getOptional(registryKey)).ifPresent(entry -> {
                    reference.setValue(entry.value());
                    iterator.remove();
                });
            }
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Registries.class, "owner;lookup;registries;registeredValues;errors", "owner", "lookup", "registries", "registeredValues", "errors"}, this, object);
        }

        public AnyOwner owner() {
            return this.owner;
        }

        public StandAloneEntryCreatingLookup lookup() {
            return this.lookup;
        }

        public Map<Identifier, RegistryEntryLookup<?>> registries() {
            return this.registries;
        }

        public Map<RegistryKey<?>, RegisteredValue<?>> registeredValues() {
            return this.registeredValues;
        }

        public List<RuntimeException> errors() {
            return this.errors;
        }
    }

    static final class InitializedRegistry<T>
    extends Record {
        final RegistryInfo<T> stub;
        final Map<RegistryKey<T>, EntryAssociatedValue<T>> values;

        InitializedRegistry(RegistryInfo<T> registryInfo, Map<RegistryKey<T>, EntryAssociatedValue<T>> map) {
            this.stub = registryInfo;
            this.values = map;
        }

        public RegistryWrapper.Impl<T> toWrapper() {
            return new RegistryWrapper.Impl<T>(){
                private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keysToEntries;
                {
                    this.keysToEntries = values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> {
                        EntryAssociatedValue entryAssociatedValue = (EntryAssociatedValue)entry.getValue();
                        RegistryEntry.Reference reference = entryAssociatedValue.entry().orElseGet(() -> RegistryEntry.Reference.standAlone(this, (RegistryKey)entry.getKey()));
                        reference.setValue(entryAssociatedValue.value().value());
                        return reference;
                    }));
                }

                @Override
                public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
                    return stub.key();
                }

                @Override
                public Lifecycle getLifecycle() {
                    return stub.lifecycle();
                }

                @Override
                public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                    return Optional.ofNullable(this.keysToEntries.get(key));
                }

                @Override
                public Stream<RegistryEntry.Reference<T>> streamEntries() {
                    return this.keysToEntries.values().stream();
                }

                @Override
                public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
                    return Optional.empty();
                }

                @Override
                public Stream<RegistryEntryList.Named<T>> streamTags() {
                    return Stream.empty();
                }
            };
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{InitializedRegistry.class, "stub;values", "stub", "values"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InitializedRegistry.class, "stub;values", "stub", "values"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InitializedRegistry.class, "stub;values", "stub", "values"}, this, object);
        }

        public RegistryInfo<T> stub() {
            return this.stub;
        }

        public Map<RegistryKey<T>, EntryAssociatedValue<T>> values() {
            return this.values;
        }
    }

    record EntryAssociatedValue<T>(RegisteredValue<T> value, Optional<RegistryEntry.Reference<T>> entry) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntryAssociatedValue.class, "value;holder", "value", "entry"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntryAssociatedValue.class, "value;holder", "value", "entry"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntryAssociatedValue.class, "value;holder", "value", "entry"}, this, object);
        }
    }

    static final class RegisteredValue<T>
    extends Record {
        final T value;
        private final Lifecycle lifecycle;

        RegisteredValue(T object, Lifecycle lifecycle) {
            this.value = object;
            this.lifecycle = lifecycle;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegisteredValue.class, "value;lifecycle", "value", "lifecycle"}, this, object);
        }

        public T value() {
            return this.value;
        }

        public Lifecycle lifecycle() {
            return this.lifecycle;
        }
    }

    static class StandAloneEntryCreatingLookup
    extends EntryListCreatingLookup<Object> {
        final Map<RegistryKey<Object>, RegistryEntry.Reference<Object>> keysToEntries = new HashMap<RegistryKey<Object>, RegistryEntry.Reference<Object>>();

        public StandAloneEntryCreatingLookup(RegistryEntryOwner<Object> registryEntryOwner) {
            super(registryEntryOwner);
        }

        @Override
        public Optional<RegistryEntry.Reference<Object>> getOptional(RegistryKey<Object> key) {
            return Optional.of(this.getOrCreate(key));
        }

        <T> RegistryEntry.Reference<T> getOrCreate(RegistryKey<T> key) {
            return this.keysToEntries.computeIfAbsent(key, key2 -> RegistryEntry.Reference.standAlone(this.entryOwner, key2));
        }
    }

    static class AnyOwner
    implements RegistryEntryOwner<Object> {
        private final Set<RegistryEntryOwner<?>> owners = Sets.newIdentityHashSet();

        AnyOwner() {
        }

        @Override
        public boolean ownerEquals(RegistryEntryOwner<Object> other) {
            return this.owners.contains(other);
        }

        public void addOwner(RegistryEntryOwner<?> owner) {
            this.owners.add(owner);
        }
    }

    static abstract class EntryListCreatingLookup<T>
    implements RegistryEntryLookup<T> {
        protected final RegistryEntryOwner<T> entryOwner;

        protected EntryListCreatingLookup(RegistryEntryOwner<T> entryOwner) {
            this.entryOwner = entryOwner;
        }

        @Override
        public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
            return Optional.of(RegistryEntryList.of(this.entryOwner, tag));
        }
    }
}

