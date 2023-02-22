/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicRegistryManager {
    private static final Logger LOGGER = LogManager.getLogger();
    static final Map<RegistryKey<? extends Registry<?>>, Info<?>> INFOS = (Map)Util.make(() -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        DynamicRegistryManager.register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        DynamicRegistryManager.register(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.field_26633);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_SURFACE_BUILDER_KEY, ConfiguredSurfaceBuilder.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, ConfiguredStructureFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.field_25876);
        DynamicRegistryManager.register(builder, Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC);
        DynamicRegistryManager.register(builder, Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC);
        return builder.build();
    });
    private static final Impl BUILTIN = Util.make(() -> {
        Impl impl = new Impl();
        DimensionType.addRegistryDefaults(impl);
        INFOS.keySet().stream().filter(registryKey -> !registryKey.equals(Registry.DIMENSION_TYPE_KEY)).forEach(registryKey -> DynamicRegistryManager.copyFromBuiltin(impl, registryKey));
        return impl;
    });

    public abstract <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> var1);

    public <E> MutableRegistry<E> getMutable(RegistryKey<? extends Registry<? extends E>> key) {
        return this.getOptionalMutable(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    public <E> Optional<? extends Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
        Optional<MutableRegistry<E>> optional = this.getOptionalMutable(key);
        if (optional.isPresent()) {
            return optional;
        }
        return Registry.REGISTRIES.getOrEmpty(key.getValue());
    }

    public <E> Registry<E> get(RegistryKey<? extends Registry<? extends E>> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, null));
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec, Codec<E> networkEntryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, networkEntryCodec));
    }

    public static Impl create() {
        Impl impl = new Impl();
        RegistryOps.EntryLoader.Impl impl2 = new RegistryOps.EntryLoader.Impl();
        for (Info<?> info : INFOS.values()) {
            DynamicRegistryManager.method_31141(impl, impl2, info);
        }
        RegistryOps.ofLoaded(JsonOps.INSTANCE, impl2, (DynamicRegistryManager)impl);
        return impl;
    }

    private static <E> void method_31141(Impl registryManager, RegistryOps.EntryLoader.Impl entryLoader, Info<E> info) {
        RegistryKey<Registry<E>> registryKey = info.getRegistry();
        boolean bl = !registryKey.equals(Registry.CHUNK_GENERATOR_SETTINGS_KEY) && !registryKey.equals(Registry.DIMENSION_TYPE_KEY);
        Registry<E> registry = BUILTIN.get(registryKey);
        MutableRegistry<E> mutableRegistry = registryManager.getMutable(registryKey);
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            RegistryKey<E> registryKey2 = entry.getKey();
            E object = entry.getValue();
            if (bl) {
                entryLoader.add(BUILTIN, registryKey2, info.getEntryCodec(), registry.getRawId(object), object, registry.getEntryLifecycle(object));
                continue;
            }
            mutableRegistry.set(registry.getRawId(object), registryKey2, object, registry.getEntryLifecycle(object));
        }
    }

    private static <R extends Registry<?>> void copyFromBuiltin(Impl manager, RegistryKey<R> registryRef) {
        Registry<Registry<?>> registry = BuiltinRegistries.REGISTRIES;
        Registry<?> registry2 = registry.getOrThrow(registryRef);
        DynamicRegistryManager.addBuiltinEntries(manager, registry2);
    }

    private static <E> void addBuiltinEntries(Impl manager, Registry<E> registry) {
        MutableRegistry<E> mutableRegistry = manager.getMutable(registry.getKey());
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            E object = entry.getValue();
            mutableRegistry.set(registry.getRawId(object), entry.getKey(), object, registry.getEntryLifecycle(object));
        }
    }

    public static void load(DynamicRegistryManager dynamicRegistryManager, RegistryOps<?> registryOps) {
        for (Info<?> info : INFOS.values()) {
            DynamicRegistryManager.load(registryOps, dynamicRegistryManager, info);
        }
    }

    private static <E> void load(RegistryOps<?> ops, DynamicRegistryManager dynamicRegistryManager, Info<E> info) {
        RegistryKey<Registry<E>> registryKey = info.getRegistry();
        SimpleRegistry simpleRegistry = (SimpleRegistry)dynamicRegistryManager.getMutable(registryKey);
        DataResult<SimpleRegistry<E>> dataResult = ops.loadToRegistry(simpleRegistry, info.getRegistry(), info.getEntryCodec());
        dataResult.error().ifPresent(partialResult -> {
            throw new JsonParseException("Error loading registry data: " + partialResult.message());
        });
    }

    static final class Info<E> {
        private final RegistryKey<? extends Registry<E>> registry;
        private final Codec<E> entryCodec;
        @Nullable
        private final Codec<E> networkEntryCodec;

        public Info(RegistryKey<? extends Registry<E>> registry, Codec<E> entryCodec, @Nullable Codec<E> networkEntryCodec) {
            this.registry = registry;
            this.entryCodec = entryCodec;
            this.networkEntryCodec = networkEntryCodec;
        }

        public RegistryKey<? extends Registry<E>> getRegistry() {
            return this.registry;
        }

        public Codec<E> getEntryCodec() {
            return this.entryCodec;
        }

        @Nullable
        public Codec<E> getNetworkEntryCodec() {
            return this.networkEntryCodec;
        }

        public boolean isSynced() {
            return this.networkEntryCodec != null;
        }
    }

    public static final class Impl
    extends DynamicRegistryManager {
        public static final Codec<Impl> CODEC = Impl.setupCodec();
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries;

        private static <E> Codec<Impl> setupCodec() {
            Codec codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
            Codec codec2 = codec.partialDispatch("type", simpleRegistry -> DataResult.success(simpleRegistry.getKey()), registryKey -> Impl.getDataResultForCodec(registryKey).map(codec -> SimpleRegistry.createRegistryManagerCodec(registryKey, Lifecycle.experimental(), codec)));
            UnboundedMapCodec unboundedMapCodec = Codec.unboundedMap((Codec)codec, (Codec)codec2);
            return Impl.fromRegistryCodecs(unboundedMapCodec);
        }

        private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<Impl> fromRegistryCodecs(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(Impl::new, impl -> (Map)impl.registries.entrySet().stream().filter(entry -> INFOS.get(entry.getKey()).isSynced()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends Codec<E>> getDataResultForCodec(RegistryKey<? extends Registry<E>> registryRef) {
            return Optional.ofNullable(INFOS.get(registryRef)).map(info -> info.getNetworkEntryCodec()).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown or not serializable registry: " + registryRef)));
        }

        public Impl() {
            this(INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), Impl::createRegistry)));
        }

        private Impl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries) {
            this.registries = registries;
        }

        private static <E> SimpleRegistry<?> createRegistry(RegistryKey<? extends Registry<?>> registryRef) {
            return new SimpleRegistry(registryRef, Lifecycle.stable());
        }

        @Override
        public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(simpleRegistry -> simpleRegistry);
        }
    }
}

