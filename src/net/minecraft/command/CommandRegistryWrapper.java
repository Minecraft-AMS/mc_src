/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

public interface CommandRegistryWrapper<T> {
    public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> var1);

    public Stream<RegistryKey<T>> streamKeys();

    public Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> var1);

    public Stream<TagKey<T>> streamTags();

    public static <T> CommandRegistryWrapper<T> of(Registry<T> registry) {
        return new Impl<T>(registry);
    }

    public static class Impl<T>
    implements CommandRegistryWrapper<T> {
        protected final Registry<T> registry;

        public Impl(Registry<T> registry) {
            this.registry = registry;
        }

        @Override
        public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {
            return this.registry.getEntry(key);
        }

        @Override
        public Stream<RegistryKey<T>> streamKeys() {
            return this.registry.getEntrySet().stream().map(Map.Entry::getKey);
        }

        @Override
        public Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> tag) {
            return this.registry.getEntryList(tag);
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return this.registry.streamTags();
        }
    }
}

