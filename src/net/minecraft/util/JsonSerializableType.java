/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.JsonSerializer;

public class JsonSerializableType<T> {
    private final JsonSerializer<? extends T> jsonSerializer;

    public JsonSerializableType(JsonSerializer<? extends T> jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    public JsonSerializer<? extends T> getJsonSerializer() {
        return this.jsonSerializer;
    }
}

