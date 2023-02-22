/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.TypeAdapter
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public class LowercaseEnumTypeAdapterFactory
implements TypeAdapterFactory {
    @Nullable
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class class_ = typeToken.getRawType();
        if (!class_.isEnum()) {
            return null;
        }
        final HashMap map = Maps.newHashMap();
        for (Object object : class_.getEnumConstants()) {
            map.put(this.getKey(object), object);
        }
        return new TypeAdapter<T>(){

            public void write(JsonWriter writer, T o) throws IOException {
                if (o == null) {
                    writer.nullValue();
                } else {
                    writer.value(LowercaseEnumTypeAdapterFactory.this.getKey(o));
                }
            }

            @Nullable
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                return map.get(reader.nextString());
            }
        };
    }

    String getKey(Object o) {
        if (o instanceof Enum) {
            return ((Enum)o).name().toLowerCase(Locale.ROOT);
        }
        return o.toString().toLowerCase(Locale.ROOT);
    }
}

