/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.metadata.ResourceMetadataReader;

public interface ResourceMetadataSerializer<T>
extends ResourceMetadataReader<T> {
    public JsonObject toJson(T var1);

    public static <T> ResourceMetadataSerializer<T> fromCodec(final String key, final Codec<T> codec) {
        return new ResourceMetadataSerializer<T>(){

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public T fromJson(JsonObject json) {
                return codec.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).getOrThrow(false, error -> {});
            }

            @Override
            public JsonObject toJson(T metadata) {
                return ((JsonElement)codec.encodeStart((DynamicOps)JsonOps.INSTANCE, metadata).getOrThrow(false, error -> {})).getAsJsonObject();
            }
        };
    }
}

