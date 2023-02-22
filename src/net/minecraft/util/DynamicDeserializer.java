/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T> {
    public static final Logger LOGGER = LogManager.getLogger();

    public T deserialize(Dynamic<?> var1);

    public static <T, V, U extends DynamicDeserializer<V>> V deserialize(Dynamic<T> dynamic2, Registry<U> dynamic, String registry, V typeFieldName) {
        Object object;
        DynamicDeserializer dynamicDeserializer = (DynamicDeserializer)dynamic.get(new Identifier(dynamic2.get(registry).asString("")));
        if (dynamicDeserializer != null) {
            object = dynamicDeserializer.deserialize(dynamic2);
        } else {
            LOGGER.error("Unknown type {}, replacing with {}", (Object)dynamic2.get(registry).asString(""), typeFieldName);
            object = typeFieldName;
        }
        return (V)object;
    }
}

