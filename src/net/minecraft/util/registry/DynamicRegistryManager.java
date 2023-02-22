/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.message.MessageType;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.EntryLoader;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface DynamicRegistryManager {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<RegistryKey<? extends Registry<?>>, Info<?>> INFOS = (Map)Util.make(() -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        DynamicRegistryManager.register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        DynamicRegistryManager.register(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.NETWORK_CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.PLACED_FEATURE_KEY, PlacedFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_KEY, Structure.STRUCTURE_CODEC);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_SET_KEY, StructureSet.CODEC);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.PROCESSORS_CODEC);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC);
        DynamicRegistryManager.register(builder, Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC);
        DynamicRegistryManager.register(builder, Registry.NOISE_KEY, DoublePerlinNoiseSampler.NoiseParameters.CODEC);
        DynamicRegistryManager.register(builder, Registry.DENSITY_FUNCTION_KEY, DensityFunction.CODEC);
        DynamicRegistryManager.register(builder, Registry.MESSAGE_TYPE_KEY, MessageType.CODEC, MessageType.CODEC);
        DynamicRegistryManager.register(builder, Registry.WORLD_PRESET_KEY, WorldPreset.CODEC);
        DynamicRegistryManager.register(builder, Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, FlatLevelGeneratorPreset.CODEC);
        return builder.build();
    });
    public static final Codec<DynamicRegistryManager> CODEC = DynamicRegistryManager.createCodec();
    public static final Supplier<Immutable> BUILTIN = Suppliers.memoize(() -> DynamicRegistryManager.createAndLoad().toImmutable());

    public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> var1);

    default public <E> Registry<E> getManaged(RegistryKey<? extends Registry<? extends E>> key) {
        return this.getOptionalManaged(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    default public <E> Optional<? extends Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
        Optional<Registry<E>> optional = this.getOptionalManaged(key);
        if (optional.isPresent()) {
            return optional;
        }
        return Registry.REGISTRIES.getOrEmpty(key.getValue());
    }

    default public <E> Registry<E> get(RegistryKey<? extends Registry<? extends E>> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, null));
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec, Codec<E> networkEntryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, networkEntryCodec));
    }

    public static Iterable<Info<?>> getInfos() {
        return INFOS.values();
    }

    public Stream<Entry<?>> streamManagedRegistries();

    private static Stream<Entry<Object>> streamStaticRegistries() {
        return Registry.REGISTRIES.streamEntries().map(Entry::of);
    }

    default public Stream<Entry<?>> streamAllRegistries() {
        return Stream.concat(this.streamManagedRegistries(), DynamicRegistryManager.streamStaticRegistries());
    }

    default public Stream<Entry<?>> streamSyncedRegistries() {
        return Stream.concat(this.streamSyncedManagedRegistries(), DynamicRegistryManager.streamStaticRegistries());
    }

    private static <E> Codec<DynamicRegistryManager> createCodec() {
        Codec codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
        Codec codec2 = codec.partialDispatch("type", registry -> DataResult.success(registry.getKey()), registryRef -> DynamicRegistryManager.getNetworkEntryCodec(registryRef).map(codec -> RegistryCodecs.createRegistryCodec(registryRef, Lifecycle.experimental(), codec)));
        UnboundedMapCodec unboundedMapCodec = Codec.unboundedMap((Codec)codec, (Codec)codec2);
        return DynamicRegistryManager.createCodec(unboundedMapCodec);
    }

    private static <K extends RegistryKey<? extends Registry<?>>, V extends Registry<?>> Codec<DynamicRegistryManager> createCodec(UnboundedMapCodec<K, V> originalCodec) {
        return originalCodec.xmap(ImmutableImpl::new, dynamicRegistryManager -> (Map)dynamicRegistryManager.streamSyncedManagedRegistries().collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value())));
    }

    private Stream<Entry<?>> streamSyncedManagedRegistries() {
        return this.streamManagedRegistries().filter(entry -> INFOS.get(entry.key).isSynced());
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkEntryCodec(RegistryKey<? extends Registry<E>> registryKey) {
        return Optional.ofNullable(INFOS.get(registryKey)).map(info -> info.networkEntryCodec()).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown or not serializable registry: " + registryKey)));
    }

    private static Map<RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> createMutableRegistries() {
        return INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), DynamicRegistryManager::createSimpleRegistry));
    }

    private static Mutable createMutableRegistryManager() {
        return new MutableImpl(DynamicRegistryManager.createMutableRegistries());
    }

    public static Immutable of(final Registry<? extends Registry<?>> registries) {
        return new Immutable(){

            public <T> Optional<Registry<T>> getOptionalManaged(RegistryKey<? extends Registry<? extends T>> key) {
                Registry registry = registries;
                return registry.getOrEmpty(key);
            }

            @Override
            public Stream<Entry<?>> streamManagedRegistries() {
                return registries.getEntrySet().stream().map(Entry::of);
            }
        };
    }

    public static Mutable createAndLoad() {
        Mutable mutable = DynamicRegistryManager.createMutableRegistryManager();
        EntryLoader.Impl impl = new EntryLoader.Impl();
        for (Map.Entry<RegistryKey<Registry<?>>, Info<?>> entry : INFOS.entrySet()) {
            DynamicRegistryManager.addEntriesToLoad(impl, entry.getValue());
        }
        RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, impl);
        return mutable;
    }

    private static <E> void addEntriesToLoad(EntryLoader.Impl entryLoader, Info<E> info) {
        RegistryKey<Registry<E>> registryKey = info.registry();
        Registry<E> registry = BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.get(registryKey);
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntrySet()) {
            RegistryKey<E> registryKey2 = entry.getKey();
            E object = entry.getValue();
            entryLoader.add(BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER, registryKey2, info.entryCodec(), registry.getRawId(object), object, registry.getEntryLifecycle(object));
        }
    }

    public static void load(Mutable dynamicRegistryManager, DynamicOps<JsonElement> ops, RegistryLoader registryLoader) {
        RegistryLoader.LoaderAccess loaderAccess = registryLoader.createAccess(dynamicRegistryManager);
        for (Info<?> info : INFOS.values()) {
            DynamicRegistryManager.load(ops, loaderAccess, info);
        }
    }

    private static <E> void load(DynamicOps<JsonElement> ops, RegistryLoader.LoaderAccess loaderAccess, Info<E> info) {
        DataResult<Registry<E>> dataResult = loaderAccess.load(info.registry(), info.entryCodec(), ops);
        dataResult.error().ifPresent(partialResult -> {
            throw new JsonParseException("Error loading registry data: " + partialResult.message());
        });
    }

    public static DynamicRegistryManager createDynamicRegistryManager(Dynamic<?> dynamic) {
        return new ImmutableImpl(INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), registryRef -> DynamicRegistryManager.createRegistry(registryRef, dynamic))));
    }

    public static <E> Registry<E> createRegistry(RegistryKey<? extends Registry<? extends E>> registryRef, Dynamic<?> dynamic) {
        return (Registry)RegistryOps.createRegistryCodec(registryRef).codec().parse(dynamic).resultOrPartial(Util.addPrefix(registryRef + " registry: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).orElseThrow(() -> new IllegalStateException("Failed to get " + registryRef + " registry"));
    }

    public static <E> MutableRegistry<?> createSimpleRegistry(RegistryKey<? extends Registry<?>> registryRef) {
        return new SimpleRegistry(registryRef, Lifecycle.stable(), null);
    }

    default public Immutable toImmutable() {
        return new ImmutableImpl(this.streamManagedRegistries().map(Entry::freeze));
    }

    default public Lifecycle getRegistryLifecycle() {
        return this.streamManagedRegistries().map(entry -> entry.value.getLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
    }

    public record Info<E>(RegistryKey<? extends Registry<E>> registry, Codec<E> entryCodec, @Nullable Codec<E> networkEntryCodec) {
        public boolean isSynced() {
            return this.networkEntryCodec != null;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Info.class, "key;codec;networkCodec", "registry", "entryCodec", "networkEntryCodec"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Info.class, "key;codec;networkCodec", "registry", "entryCodec", "networkEntryCodec"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Info.class, "key;codec;networkCodec", "registry", "entryCodec", "networkEntryCodec"}, this, object);
        }
    }

    public static final class MutableImpl
    implements Mutable {
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries;

        MutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries) {
            this.mutableRegistries = mutableRegistries;
        }

        @Override
        public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable(this.mutableRegistries.get(key)).map(registry -> registry);
        }

        @Override
        public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable(this.mutableRegistries.get(key)).map(registry -> registry);
        }

        @Override
        public Stream<Entry<?>> streamManagedRegistries() {
            return this.mutableRegistries.entrySet().stream().map(Entry::of);
        }
    }

    public static interface Mutable
    extends DynamicRegistryManager {
        public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> var1);

        default public <E> MutableRegistry<E> getMutable(RegistryKey<? extends Registry<? extends E>> key) {
            return this.getOptionalMutable(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
        }
    }

    public static final class ImmutableImpl
    implements Immutable {
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;

        public ImmutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
            this.registries = Map.copyOf(registries);
        }

        ImmutableImpl(Stream<Entry<?>> stream) {
            this.registries = (Map)stream.collect(ImmutableMap.toImmutableMap(Entry::key, Entry::value));
        }

        @Override
        public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(registry -> registry);
        }

        @Override
        public Stream<Entry<?>> streamManagedRegistries() {
            return this.registries.entrySet().stream().map(Entry::of);
        }
    }

    public static final class Entry<T>
    extends Record {
        final RegistryKey<? extends Registry<T>> key;
        final Registry<T> value;

        public Entry(RegistryKey<? extends Registry<T>> registryKey, Registry<T> registry) {
            this.key = registryKey;
            this.value = registry;
        }

        private static <T, R extends Registry<? extends T>> Entry<T> of(Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry) {
            return Entry.of(entry.getKey(), (Registry)entry.getValue());
        }

        private static <T> Entry<T> of(RegistryEntry.Reference<? extends Registry<? extends T>> entry) {
            return Entry.of(entry.registryKey(), entry.value());
        }

        private static <T> Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
            return new Entry(key, value);
        }

        private Entry<T> freeze() {
            return new Entry<T>(this.key, this.value.freeze());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this, object);
        }

        public RegistryKey<? extends Registry<T>> key() {
            return this.key;
        }

        public Registry<T> value() {
            return this.value;
        }
    }

    public static interface Immutable
    extends DynamicRegistryManager {
        @Override
        default public Immutable toImmutable() {
            return this;
        }
    }
}

