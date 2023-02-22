/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class VariantPredicates<V> {
    private static final String VARIANT_KEY = "variant";
    final Codec<V> codec;
    final Function<Entity, Optional<V>> variantGetter;
    final TypeSpecificPredicate.Deserializer deserializer;

    public static <V> VariantPredicates<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
        return new VariantPredicates<V>(registry.getCodec(), variantGetter);
    }

    public static <V> VariantPredicates<V> create(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
        return new VariantPredicates<V>(codec, variantGetter);
    }

    private VariantPredicates(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
        this.codec = codec;
        this.variantGetter = variantGetter;
        this.deserializer = json -> {
            JsonElement jsonElement = json.get(VARIANT_KEY);
            if (jsonElement == null) {
                throw new JsonParseException("Missing variant field");
            }
            Object object = ((Pair)Util.getResult(codec.decode(new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement)), JsonParseException::new)).getFirst();
            return this.createPredicate(object);
        };
    }

    public TypeSpecificPredicate.Deserializer getDeserializer() {
        return this.deserializer;
    }

    public TypeSpecificPredicate createPredicate(final V variant) {
        return new TypeSpecificPredicate(){

            @Override
            public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
                return VariantPredicates.this.variantGetter.apply(entity).filter(variant -> variant.equals(variant)).isPresent();
            }

            @Override
            public JsonObject typeSpecificToJson() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add(VariantPredicates.VARIANT_KEY, (JsonElement)Util.getResult(VariantPredicates.this.codec.encodeStart((DynamicOps)JsonOps.INSTANCE, variant), string -> new JsonParseException("Can't serialize variant " + variant + ", message " + string)));
                return jsonObject;
            }

            @Override
            public TypeSpecificPredicate.Deserializer getDeserializer() {
                return VariantPredicates.this.deserializer;
            }
        };
    }
}

