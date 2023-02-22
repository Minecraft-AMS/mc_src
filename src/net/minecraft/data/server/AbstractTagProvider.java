/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public abstract class AbstractTagProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator root;
    protected final Registry<T> registry;
    private final Map<Identifier, Tag.Builder> tagBuilders = Maps.newLinkedHashMap();

    protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
        this.root = root;
        this.registry = registry;
    }

    protected abstract void configure();

    @Override
    public void run(DataCache cache) {
        this.tagBuilders.clear();
        this.configure();
        this.tagBuilders.forEach((id, builder) -> {
            List<Tag.TrackedEntry> list = builder.streamEntries().filter(tag -> !tag.entry().canAdd(this.registry::containsId, this.tagBuilders::containsKey)).toList();
            if (!list.isEmpty()) {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", id, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            JsonObject jsonObject = builder.toJson();
            Path path = this.getOutput((Identifier)id);
            try {
                String string = GSON.toJson((JsonElement)jsonObject);
                String string2 = SHA1.hashUnencodedChars((CharSequence)string).toString();
                if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                    Files.createDirectories(path.getParent(), new FileAttribute[0]);
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                        bufferedWriter.write(string);
                    }
                }
                cache.updateSha1(path, string2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save tags to {}", (Object)path, (Object)iOException);
            }
        });
    }

    private Path getOutput(Identifier id) {
        RegistryKey<Registry<T>> registryKey = this.registry.getKey();
        return this.root.getOutput().resolve("data/" + id.getNamespace() + "/" + TagManagerLoader.getPath(registryKey) + "/" + id.getPath() + ".json");
    }

    protected ObjectBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
        Tag.Builder builder = this.getTagBuilder(tag);
        return new ObjectBuilder<T>(builder, this.registry, "vanilla");
    }

    protected Tag.Builder getTagBuilder(TagKey<T> tag) {
        return this.tagBuilders.computeIfAbsent(tag.id(), id -> new Tag.Builder());
    }

    protected static class ObjectBuilder<T> {
        private final Tag.Builder builder;
        private final Registry<T> registry;
        private final String source;

        ObjectBuilder(Tag.Builder builder, Registry<T> registry, String source) {
            this.builder = builder;
            this.registry = registry;
            this.source = source;
        }

        public ObjectBuilder<T> add(T element) {
            this.builder.add(this.registry.getId(element), this.source);
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(RegistryKey<T> ... keys) {
            for (RegistryKey<T> registryKey : keys) {
                this.builder.add(registryKey.getValue(), this.source);
            }
            return this;
        }

        public ObjectBuilder<T> addOptional(Identifier id) {
            this.builder.addOptional(id, this.source);
            return this;
        }

        public ObjectBuilder<T> addTag(TagKey<T> identifiedTag) {
            this.builder.addTag(identifiedTag.id(), this.source);
            return this;
        }

        public ObjectBuilder<T> addOptionalTag(Identifier id) {
            this.builder.addOptionalTag(id, this.source);
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T ... elements) {
            Stream.of(elements).map(this.registry::getId).forEach(id -> this.builder.add((Identifier)id, this.source));
            return this;
        }
    }
}

