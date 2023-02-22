/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

public class Model {
    private final Optional<Identifier> parent;
    private final Set<TextureKey> requiredTextures;
    private final Optional<String> variant;

    public Model(Optional<Identifier> parent, Optional<String> variant, TextureKey ... requiredTextureKeys) {
        this.parent = parent;
        this.variant = variant;
        this.requiredTextures = ImmutableSet.copyOf((Object[])requiredTextureKeys);
    }

    public Identifier upload(Block block, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier upload(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix + this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier uploadWithoutVariant(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix), textures, modelCollector);
    }

    public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        Map<TextureKey, Identifier> map = this.createTextureMap(textures);
        modelCollector.accept(id, () -> {
            JsonObject jsonObject = new JsonObject();
            this.parent.ifPresent(parentId -> jsonObject.addProperty("parent", parentId.toString()));
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((textureKey, textureId) -> jsonObject2.addProperty(textureKey.getName(), textureId.toString()));
                jsonObject.add("textures", (JsonElement)jsonObject2);
            }
            return jsonObject;
        });
        return id;
    }

    private Map<TextureKey, Identifier> createTextureMap(TextureMap textures) {
        return (Map)Streams.concat((Stream[])new Stream[]{this.requiredTextures.stream(), textures.getInherited()}).collect(ImmutableMap.toImmutableMap(Function.identity(), textures::getTexture));
    }
}

