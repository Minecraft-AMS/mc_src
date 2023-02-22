/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T>
extends ForwardingDynamicOps<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final EntryLoader entryLoader;
    private final DynamicRegistryManager.Impl registryManager;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders;
    private final RegistryOps<JsonElement> entryOps;

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
        return RegistryOps.of(delegate, EntryLoader.resourceBacked(resourceManager), impl);
    }

    public static <T> RegistryOps<T> of(DynamicOps<T> dynamicOps, EntryLoader entryLoader, DynamicRegistryManager.Impl impl) {
        RegistryOps<T> registryOps = new RegistryOps<T>(dynamicOps, entryLoader, impl, Maps.newIdentityHashMap());
        DynamicRegistryManager.load(impl, registryOps);
        return registryOps;
    }

    private RegistryOps(DynamicOps<T> delegate, EntryLoader entryLoader, DynamicRegistryManager.Impl impl, IdentityHashMap<RegistryKey<? extends Registry<?>>, ValueHolder<?>> identityHashMap) {
        super(delegate);
        this.entryLoader = entryLoader;
        this.registryManager = impl;
        this.valueHolders = identityHashMap;
        this.entryOps = delegate == JsonOps.INSTANCE ? this : new RegistryOps<T>(JsonOps.INSTANCE, entryLoader, impl, (IdentityHashMap<RegistryKey<Registry<?>>, ValueHolder<?>>)identityHashMap);
    }

    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> key, Codec<E> codec, boolean allowInlineDefinitions) {
        Optional optional = this.registryManager.getOptionalMutable(key);
        if (!optional.isPresent()) {
            return DataResult.error((String)("Unknown registry: " + key));
        }
        MutableRegistry mutableRegistry = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            if (!allowInlineDefinitions) {
                return DataResult.error((String)"Inline definitions not allowed here");
            }
            return codec.decode((DynamicOps)this, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Pair pair2 = (Pair)dataResult.result().get();
        Identifier identifier = (Identifier)pair2.getFirst();
        return this.readSupplier(key, mutableRegistry, codec, identifier).map(supplier -> Pair.of((Object)supplier, (Object)pair2.getSecond()));
    }

    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> key, Codec<E> codec) {
        Collection<Identifier> collection = this.entryLoader.getKnownEntryPaths(key);
        DataResult dataResult = DataResult.success(registry, (Lifecycle)Lifecycle.stable());
        String string = key.getValue().getPath() + "/";
        for (Identifier identifier : collection) {
            String string2 = identifier.getPath();
            if (!string2.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)identifier);
                continue;
            }
            if (!string2.startsWith(string)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier);
                continue;
            }
            String string3 = string2.substring(string.length(), string2.length() - ".json".length());
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string3);
            dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(key, (MutableRegistry)simpleRegistry, codec, identifier2).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(registry);
    }

    private <E> DataResult<java.util.function.Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> key, MutableRegistry<E> registry, Codec<E> codec, Identifier elementId) {
        RegistryKey registryKey = RegistryKey.of(key, elementId);
        ValueHolder<E> valueHolder = this.getValueHolder(key);
        DataResult dataResult = (DataResult)((ValueHolder)valueHolder).values.get(registryKey);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier supplier = Suppliers.memoize(() -> {
            Object object = registry.get(registryKey);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
            }
            return object;
        });
        ((ValueHolder)valueHolder).values.put(registryKey, DataResult.success((Object)supplier));
        DataResult dataResult2 = this.entryLoader.load((DynamicOps<JsonElement>)this.entryOps, key, registryKey, codec);
        Optional optional = dataResult2.result();
        if (optional.isPresent()) {
            Pair pair2 = (Pair)optional.get();
            registry.replace((OptionalInt)pair2.getSecond(), registryKey, pair2.getFirst(), dataResult2.lifecycle());
        }
        DataResult dataResult3 = !optional.isPresent() && registry.get(registryKey) != null ? DataResult.success(() -> registry.get(registryKey), (Lifecycle)Lifecycle.stable()) : dataResult2.map(pair -> () -> registry.get(registryKey));
        ((ValueHolder)valueHolder).values.put(registryKey, dataResult3);
        return dataResult3;
    }

    private <E> ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, registryKey -> new ValueHolder());
    }

    protected <E> DataResult<Registry<E>> method_31152(RegistryKey<? extends Registry<E>> registryKey) {
        return this.registryManager.getOptionalMutable(registryKey).map(mutableRegistry -> DataResult.success((Object)mutableRegistry, (Lifecycle)mutableRegistry.getLifecycle())).orElseGet(() -> DataResult.error((String)("Unknown registry: " + registryKey)));
    }

    public static interface EntryLoader {
        public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> var1);

        public <E> DataResult<Pair<E, OptionalInt>> load(DynamicOps<JsonElement> var1, RegistryKey<? extends Registry<E>> var2, RegistryKey<E> var3, Decoder<E> var4);

        public static EntryLoader resourceBacked(final ResourceManager resourceManager) {
            return new EntryLoader(){

                @Override
                public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> registryKey) {
                    return resourceManager.findResources(registryKey.getValue().getPath(), string -> string.endsWith(".json"));
                }

                /*
                 * Exception decompiling
                 */
                @Override
                public <E> DataResult<Pair<E, OptionalInt>> load(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
                    /*
                     * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                     * 
                     * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                     *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                     *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                     *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
                     *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
                     *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                     *     at org.benf.cfr.reader.Main.main(Main.java:54)
                     */
                    throw new IllegalStateException("Decompilation failed");
                }

                public String toString() {
                    return "ResourceAccess[" + resourceManager + "]";
                }

                private static /* synthetic */ Pair method_31157(Object object) {
                    return Pair.of((Object)object, (Object)OptionalInt.empty());
                }
            };
        }

        public static final class Impl
        implements EntryLoader {
            private final Map<RegistryKey<?>, JsonElement> values = Maps.newIdentityHashMap();
            private final Object2IntMap<RegistryKey<?>> entryToRawId = new Object2IntOpenCustomHashMap(Util.identityHashStrategy());
            private final Map<RegistryKey<?>, Lifecycle> entryToLifecycle = Maps.newIdentityHashMap();

            public <E> void add(DynamicRegistryManager.Impl impl, RegistryKey<E> registryKey, Encoder<E> encoder, int rawId, E object, Lifecycle lifecycle) {
                DataResult dataResult = encoder.encodeStart(RegistryReadingOps.of(JsonOps.INSTANCE, impl), object);
                Optional optional = dataResult.error();
                if (optional.isPresent()) {
                    LOGGER.error("Error adding element: {}", (Object)((DataResult.PartialResult)optional.get()).message());
                    return;
                }
                this.values.put(registryKey, (JsonElement)dataResult.result().get());
                this.entryToRawId.put(registryKey, rawId);
                this.entryToLifecycle.put(registryKey, lifecycle);
            }

            @Override
            public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> registryKey) {
                return this.values.keySet().stream().filter(registryKey2 -> registryKey2.isOf(registryKey)).map(registryKey2 -> new Identifier(registryKey2.getValue().getNamespace(), registryKey.getValue().getPath() + "/" + registryKey2.getValue().getPath() + ".json")).collect(Collectors.toList());
            }

            @Override
            public <E> DataResult<Pair<E, OptionalInt>> load(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
                JsonElement jsonElement = this.values.get(entryId);
                if (jsonElement == null) {
                    return DataResult.error((String)("Unknown element: " + entryId));
                }
                return decoder.parse(dynamicOps, (Object)jsonElement).setLifecycle(this.entryToLifecycle.get(entryId)).map(object -> Pair.of((Object)object, (Object)OptionalInt.of(this.entryToRawId.getInt((Object)entryId))));
            }
        }
    }

    static final class ValueHolder<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> values = Maps.newIdentityHashMap();

        private ValueHolder() {
        }
    }
}

