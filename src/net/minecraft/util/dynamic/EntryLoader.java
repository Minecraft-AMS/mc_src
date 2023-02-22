/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  org.slf4j.Logger
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public interface EntryLoader {
    public <E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> var1);

    public <E> Optional<DataResult<Entry<E>>> load(DynamicOps<JsonElement> var1, RegistryKey<? extends Registry<E>> var2, RegistryKey<E> var3, Decoder<E> var4);

    public static EntryLoader resourceBacked(final ResourceManager resourceManager) {
        return new EntryLoader(){
            private static final String JSON = ".json";

            @Override
            public <E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
                String string = 1.getPath(key);
                HashSet set = new HashSet();
                resourceManager.findResources(string, name -> name.endsWith(JSON)).forEach(id -> {
                    String string2 = id.getPath();
                    String string3 = string2.substring(string.length() + 1, string2.length() - JSON.length());
                    set.add(RegistryKey.of(key, new Identifier(id.getNamespace(), string3)));
                });
                return set;
            }

            /*
             * Enabled aggressive exception aggregation
             */
            @Override
            public <E> Optional<DataResult<Entry<E>>> load(DynamicOps<JsonElement> json, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
                Identifier identifier = 1.createId(registryId, entryId);
                if (!resourceManager.containsResource(identifier)) {
                    return Optional.empty();
                }
                try (Resource resource = resourceManager.getResource(identifier);){
                    Optional<DataResult<Entry<E>>> optional;
                    try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);){
                        JsonElement jsonElement = JsonParser.parseReader((Reader)reader);
                        optional = Optional.of(decoder.parse(json, (Object)jsonElement).map(Entry::of));
                    }
                    return optional;
                }
                catch (JsonIOException | JsonSyntaxException | IOException exception) {
                    return Optional.of(DataResult.error((String)("Failed to parse " + identifier + " file: " + exception.getMessage())));
                }
            }

            private static String getPath(RegistryKey<? extends Registry<?>> registryKey) {
                return registryKey.getValue().getPath();
            }

            private static <E> Identifier createId(RegistryKey<? extends Registry<E>> rootKey, RegistryKey<E> key) {
                return new Identifier(key.getValue().getNamespace(), 1.getPath(rootKey) + "/" + key.getValue().getPath() + JSON);
            }

            public String toString() {
                return "ResourceAccess[" + resourceManager + "]";
            }
        };
    }

    public static final class Impl
    implements EntryLoader {
        private static final Logger LOGGER = LogUtils.getLogger();
        private final Map<RegistryKey<?>, Element> values = Maps.newIdentityHashMap();

        public <E> void add(DynamicRegistryManager registryManager, RegistryKey<E> key, Encoder<E> encoder, int rawId, E entry, Lifecycle lifecycle) {
            DataResult dataResult = encoder.encodeStart(RegistryOps.of(JsonOps.INSTANCE, registryManager), entry);
            Optional optional = dataResult.error();
            if (optional.isPresent()) {
                LOGGER.error("Error adding element: {}", (Object)((DataResult.PartialResult)optional.get()).message());
            } else {
                this.values.put(key, new Element((JsonElement)dataResult.result().get(), rawId, lifecycle));
            }
        }

        @Override
        public <E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
            return this.values.keySet().stream().flatMap(registryKey -> registryKey.tryCast(key).stream()).collect(Collectors.toList());
        }

        @Override
        public <E> Optional<DataResult<Entry<E>>> load(DynamicOps<JsonElement> json, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
            Element element = this.values.get(entryId);
            if (element == null) {
                return Optional.of(DataResult.error((String)("Unknown element: " + entryId)));
            }
            return Optional.of(decoder.parse(json, (Object)element.data).setLifecycle(element.lifecycle).map(value -> Entry.of(value, element.id)));
        }

        static final class Element
        extends Record {
            final JsonElement data;
            final int id;
            final Lifecycle lifecycle;

            Element(JsonElement jsonElement, int i, Lifecycle lifecycle) {
                this.data = jsonElement;
                this.id = i;
                this.lifecycle = lifecycle;
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Element.class, "data;id;lifecycle", "data", "id", "lifecycle"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Element.class, "data;id;lifecycle", "data", "id", "lifecycle"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Element.class, "data;id;lifecycle", "data", "id", "lifecycle"}, this, object);
            }

            public JsonElement data() {
                return this.data;
            }

            public int id() {
                return this.id;
            }

            public Lifecycle lifecycle() {
                return this.lifecycle;
            }
        }
    }

    public record Entry<E>(E value, OptionalInt fixedId) {
        public static <E> Entry<E> of(E value) {
            return new Entry<E>(value, OptionalInt.empty());
        }

        public static <E> Entry<E> of(E value, int id) {
            return new Entry<E>(value, OptionalInt.of(id));
        }
    }
}

